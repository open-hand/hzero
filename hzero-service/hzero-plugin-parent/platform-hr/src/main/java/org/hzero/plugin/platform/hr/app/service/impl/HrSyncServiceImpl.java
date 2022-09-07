package org.hzero.plugin.platform.hr.app.service.impl;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.plugin.platform.hr.app.service.HrSyncDeptService;
import org.hzero.plugin.platform.hr.app.service.HrSyncEmployeeService;
import org.hzero.plugin.platform.hr.app.service.HrSyncService;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncEmployee;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncLog;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncLogRepository;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncRepository;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.plugin.platform.hr.infra.feign.HmsgUserReceiverConfigRemoteService;
import org.hzero.starter.integrate.constant.SyncType;
import org.hzero.starter.integrate.dto.SyncCorpResultDTO;
import org.hzero.starter.integrate.dto.SyncDeptDTO;
import org.hzero.starter.integrate.dto.SyncUserDTO;
import org.hzero.starter.integrate.entity.CorpHrSync;
import org.hzero.starter.integrate.service.AbstractCorpSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@Service
public class HrSyncServiceImpl implements HrSyncService {

    private static final Logger logger = LoggerFactory.getLogger(HrSyncServiceImpl.class);

    private HrSyncRepository hrSyncRepository;
    private HrSyncLogRepository hrSyncLogRepository;
    private HrSyncEmployeeService hrSyncEmployeeService;
    private HrSyncDeptService hrSyncDeptService;
    private EncryptClient encryptClient;
    private RedisHelper redisHelper;
    private HmsgUserReceiverConfigRemoteService hmsgUserReceiverConfigRemoteService;

    @Autowired
    public HrSyncServiceImpl(HrSyncRepository hrSyncRepository, HrSyncLogRepository hrSyncLogRepository,
                             HrSyncEmployeeService hrSyncEmployeeService, HrSyncDeptService hrSyncDeptService,
                             EncryptClient encryptClient, RedisHelper redisHelper,
                             HmsgUserReceiverConfigRemoteService hmsgUserReceiverConfigRemoteService) {
        this.hrSyncRepository = hrSyncRepository;
        this.hrSyncLogRepository = hrSyncLogRepository;
        this.hrSyncEmployeeService = hrSyncEmployeeService;
        this.hrSyncDeptService = hrSyncDeptService;
        this.encryptClient = encryptClient;
        this.redisHelper = redisHelper;
        this.hmsgUserReceiverConfigRemoteService = hmsgUserReceiverConfigRemoteService;
    }

    private static final ThreadPoolTaskExecutor HR_EXECUTOR = new ThreadPoolTaskExecutor();

    static {
        HR_EXECUTOR.setThreadNamePrefix("hr-executor");
        HR_EXECUTOR.setMaxPoolSize(3);
        HR_EXECUTOR.setCorePoolSize(1);
        HR_EXECUTOR.initialize();
    }

    @Override
    public void syncNow(HrSync hrSyncDto, Boolean useGeneratedUnitId) {
        DataSecurityHelper.open();
        HrSync hrSync = hrSyncRepository.selectByPrimaryKey(hrSyncDto.getSyncId());
        if (hrSync != null) {
            HR_EXECUTOR.execute(() -> doHrSync(hrSync, useGeneratedUnitId, true));
        }
    }

    @Override
    public ResponseEntity<HrSyncLog> logDetail(Long logId) {
        HrSyncLog hrSyncLog = hrSyncLogRepository.selectByPrimaryKey(logId);
        return new ResponseEntity<>(hrSyncLog, HttpStatus.OK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HrSync insertHrSync(HrSync dto) {
        if (!StringUtils.isEmpty(dto.getAppSecret())) {
            DataSecurityHelper.open();
            // 密码解密
            dto.setAppSecret(encryptClient.decrypt(dto.getAppSecret()));
        }
        hrSyncRepository.insertSelective(dto);
        return dto;
    }

    @Override
    public Page<HrSync> pageAndSort(PageRequest pageRequest, Long tenantId, HrSync hrSync) {
        return hrSyncRepository.listHrSync(pageRequest, tenantId, hrSync.getSyncTypeCode(), hrSync.getAuthType(),
                hrSync.getEnabledFlag());
    }

    @Override
    public HrSync selectHrSync(Long syncId) {
        return hrSyncRepository.getHrSyncById(syncId);
    }

    @Override
    public HrSync updateHrSync(HrSync dto) {
        if (!StringUtils.isEmpty(dto.getAppSecret())) {
            DataSecurityHelper.open();
            // 密码解密
            dto.setAppSecret(encryptClient.decrypt(dto.getAppSecret()));
        }
        hrSyncRepository.updateByPrimaryKeySelective(dto);
        return dto;
    }

    @Override
    public int deleteHrSync(HrSync dto) {
        return hrSyncRepository.deleteByPrimaryKey(dto);
    }

    @Override
    public void syncToLocal(Long tenantId, HrSync hrSyncDto) {
        hrSyncDto.setTenantId(tenantId);
        DataSecurityHelper.open();
        HrSync hrSync = hrSyncRepository.selectByPrimaryKey(hrSyncDto.getSyncId());
        HR_EXECUTOR.execute(() -> {
            if (hrSync != null) {
                doHrSync(hrSync, false, false);
            }
        });
    }

    private void doHrSync(HrSync hrSync, Boolean useGeneratedUnitId, Boolean syncToThird) {
        HrSyncLog hrSyncLog = new HrSyncLog().setSyncId(hrSync.getSyncId()).setTenantId(hrSync.getTenantId())
                .setSyncDirection(syncToThird ? PlatformHrConstants.SyncDirection.P : PlatformHrConstants.SyncDirection.O);
        hrSyncLogRepository.insertSelective(hrSyncLog);
        // 加载同步类
        AbstractCorpSyncService corpSyncService = initCorpSyncService(hrSync);
        if (corpSyncService == null) {
            hrSyncLog.setSyncFailed(hrSyncLogRepository, PlatformHrConstants.SyncStatus.ERROR_INIT_CORP_SERVICE);
            return;
        }
        if (syncToThird) {
            syncToThird(hrSync, corpSyncService, hrSyncLog, useGeneratedUnitId);
        } else {
            syncToLocal(hrSync, corpSyncService, hrSyncLog);
        }
    }

    private void updateSync(List<HrSyncDept> syncDept, List<HrSyncEmployee> syncEmployee, Map<Long, Long> deptIdMap,
                            List<String> userIds) {
        Map<Long, Long> createDeptIdMap = new HashMap<>(16);
        Map<Long, Long> otherDeptIdMap = new HashMap<>(16);
        deptIdMap.forEach((k, v) -> {
            if (v != null) {
                // 新增操作成功的部门
                createDeptIdMap.put(k, v);
            } else {
                // 更新，删除操作
                otherDeptIdMap.put(k, v);
            }
        });
        List<HrSyncDept> successDept = syncDept.stream().filter(e -> {
            if (SyncType.CREATE.equals(e.getSyncType())) {
                if (createDeptIdMap.containsKey(e.getUnitId())) {
                    e.setDepartmentId(createDeptIdMap.get(e.getUnitId()));
                    if (e.getParentid() == null) {
                        // 转换父部门id
                        e.setParentid(createDeptIdMap.get(e.getParentUnitId()));
                    }
                    return true;
                }
            } else {
                if (otherDeptIdMap.containsKey(e.getDepartmentId())) {
                    if (e.getParentid() == null) {
                        e.setParentid(createDeptIdMap.get(e.getParentUnitId()));
                    }
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        // List 对象 在第三方同步的时候已经替换了父部门Id
        List<HrSyncEmployee> successEmployee =
                syncEmployee.stream().filter(e -> userIds.contains(e.getUserid())).collect(Collectors.toList());
        hrSyncDeptService.updateSyncDept(successDept);
        hrSyncEmployeeService.updateSyncEmployee(successEmployee);
    }

    private void syncToThird(HrSync hrSync,
                             AbstractCorpSyncService corpSyncService,
                             HrSyncLog hrSyncLog,
                             Boolean useGeneratedUnitId) {
        String lock = redisHelper.strGet(PlatformHrConstants.Lock.LOCK_KEY_TO_THIRD);
        if (!StringUtils.isEmpty(lock)) {
            throw new CommonException("Synchronization failed, there is already a synchronization operation in progress");
        }
        redisHelper.strSet(PlatformHrConstants.Lock.LOCK_KEY_TO_THIRD, PlatformHrConstants.Lock.LOCK_VALUE, PlatformHrConstants.Lock.EXPIRE_TIME, null);
        CorpHrSync corpHrSync = new CorpHrSync();
        BeanUtils.copyProperties(hrSync, corpHrSync);
        // 同步部门
        List<HrSyncDept> syncDept = hrSyncDeptService.getSyncDept(hrSync.getSyncTypeCode(), hrSync.getTenantId());
        // 同步员工
        List<HrSyncEmployee> syncEmployee =
                hrSyncEmployeeService.getSyncEmployee(hrSync.getSyncTypeCode(), hrSync.getTenantId());

        List<SyncDeptDTO> syncDeptList = syncDept.stream().map(e -> {
            SyncDeptDTO syncDeptDTO = new SyncDeptDTO().setParentid(e.getParentid()).setOrder(e.getOrderSeq())
                    .setName(e.getName()).setSyncType(e.getSyncType()).setParentUnitId(e.getParentUnitId());
            if (SyncType.CREATE.equals(e.getSyncType())) {
                // 新建部门id取自unitId
                syncDeptDTO.setId(e.getUnitId());
            } else {
                syncDeptDTO.setId(e.getDepartmentId());
            }
            return syncDeptDTO;
        }).collect(Collectors.toList());
        List<SyncUserDTO> syncUserList = syncEmployee.stream()
                .map(e -> new SyncUserDTO().setUserid(e.getUserid()).setName(e.getName())
                        .setMobile(e.getMobile()).setEmail(e.getEmail())
                        .setDepartment(e.getDepartmentIds()).setPosition(e.getPosition())
                        .setSyncType(e.getSyncType()).setGender(e.getGender())
                        .setEnable(BaseConstants.Flag.YES).setIsDepartIdsMap(e.getIsDepartIdsMap()))
                .collect(Collectors.toList());

        SyncCorpResultDTO resultDTO = corpSyncService.syncCorp(syncDeptList, syncUserList, useGeneratedUnitId, corpHrSync);
        // 更新本地的第三方组织架构副本
        updateSync(syncDept, syncEmployee, resultDTO.getDeptIdMap(), resultDTO.getUserIds());
        hrSyncLogRepository.updateLog(new HrSyncLog().setSyncLogId(hrSyncLog.getSyncLogId())
                .setDeptStatusCode(resultDTO.getDeptStatus() ? 1L : -1L)
                .setEmpStatusCode(resultDTO.getEmployeeStatus() ? 1L : -1L)
                .setLogContent(resultDTO.getLog()));
        redisHelper.delKey(PlatformHrConstants.Lock.LOCK_KEY_TO_THIRD);
    }

    private void syncToLocal(HrSync hrSync,
                             AbstractCorpSyncService corpSyncService,
                             HrSyncLog hrSyncLog) {
        String lock = redisHelper.strGet(PlatformHrConstants.Lock.LOCK_KEY_TO_LOCAL);
        if (!StringUtils.isEmpty(lock)) {
            throw new CommonException("Synchronization failed, there is already a synchronization operation in progress");
        }
        redisHelper.strSet(PlatformHrConstants.Lock.LOCK_KEY_TO_THIRD, PlatformHrConstants.Lock.LOCK_VALUE, PlatformHrConstants.Lock.EXPIRE_TIME, null);
        CorpHrSync corpHrSync = new CorpHrSync();
        BeanUtils.copyProperties(hrSync, corpHrSync);
        // 获取token
        String accessToken = corpSyncService.getAccessToken(corpHrSync);
        if (accessToken == null) {
            hrSyncLog.setSyncFailed(hrSyncLogRepository, PlatformHrConstants.SyncStatus.ERROR_GET_TOKEN);
            return;
        }
        // 日志
        StringBuilder log = new StringBuilder();
        // 获取第三方平台部门信息
        List<SyncDeptDTO> thirdDeptList = corpSyncService.listDept(null, accessToken);
        if (!CollectionUtils.isEmpty(thirdDeptList)) {
            List<HrSyncDept> hrDeptList = covertThirdDeptToLocal(thirdDeptList);
            hrSyncDeptService.syncDeptToLocal(hrDeptList, hrSync.getTenantId(), hrSync.getSyncTypeCode(), log);
            // 根据部门同步员工
            for (HrSyncDept thirdDept : hrDeptList) {
                List<SyncUserDTO> thirdDeptUserList = corpSyncService.listUser(thirdDept.getDepartmentId(), accessToken);
                hrSyncEmployeeService.syncEmployee(thirdDept,
                        covertThirdEmployeeToLocal(thirdDeptUserList), hrSync.getTenantId(), hrSync.getSyncTypeCode(), log);
            }
        }
        hrSyncLogRepository.updateLog(new HrSyncLog().setSyncLogId(hrSyncLog.getSyncLogId())
                .setDeptStatusCode(1L)
                .setEmpStatusCode(1L)
                .setLogContent(log.toString()));
        redisHelper.delKey(PlatformHrConstants.Lock.LOCK_KEY_TO_THIRD);
        // 刷新用户消息接收配置
        hmsgUserReceiverConfigRemoteService.refreshTenant(hrSync.getTenantId());
    }

    private List<HrSyncDept> covertThirdDeptToLocal(List<SyncDeptDTO> syncDeptDTOS) {
        List<HrSyncDept> result = new ArrayList<>();
        syncDeptDTOS.forEach(syncDeptDTO -> {
            HrSyncDept hrSyncDept = new HrSyncDept();
            hrSyncDept.setDepartmentId(syncDeptDTO.getId());
            hrSyncDept.setName(syncDeptDTO.getName());
            hrSyncDept.setParentid(syncDeptDTO.getParentid());
            hrSyncDept.setOrderSeq(syncDeptDTO.getOrder());
            result.add(hrSyncDept);
        });
        return result;
    }

    private List<HrSyncEmployee> covertThirdEmployeeToLocal(List<SyncUserDTO> syncUserDTOS) {
        List<HrSyncEmployee> result = new ArrayList<>();
        syncUserDTOS.forEach(syncUserDTO -> {
            HrSyncEmployee hrSyncEmployee = new HrSyncEmployee();
            BeanUtils.copyProperties(syncUserDTO, hrSyncEmployee);
            hrSyncEmployee.setDepartmentIds(syncUserDTO.getDepartment());
            result.add(hrSyncEmployee);
        });
        return result;
    }

    private AbstractCorpSyncService initCorpSyncService(HrSync hrSync) {
        // 加载同步类
        Map<String, AbstractCorpSyncService> corpSyncServices =
                ApplicationContextHelper.getContext().getBeansOfType(AbstractCorpSyncService.class);
        for (Map.Entry<String, AbstractCorpSyncService> entry : corpSyncServices.entrySet()) {
            if (hrSync.getSyncTypeCode().equals(entry.getValue().corpSyncType())) {
                return entry.getValue();
            }
        }
        return null;
    }

}
