package org.hzero.scheduler.app.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.app.service.ConcurrentRequestService;
import org.hzero.scheduler.app.service.JobInfoService;
import org.hzero.scheduler.domain.entity.*;
import org.hzero.scheduler.domain.repository.ConcPermissionRepository;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.domain.repository.ConcurrentRequestRepository;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.domain.service.IJobService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.scheduler.infra.util.CronUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求ServiceImpl
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/13 14:54
 */
@Service
public class ConcurrentRequestServiceImpl implements ConcurrentRequestService {

    private final IJobService jobService;
    private final RedisHelper redisHelper;
    private final JobInfoService jobInfoService;
    private final ConcurrentRepository concurrentRepository;
    private final ExecutableRepository executableRepository;
    private final ConcPermissionRepository permissionRepository;
    private final ConcurrentRequestRepository requestRepository;

    @Autowired
    public ConcurrentRequestServiceImpl(IJobService jobService,
                                        RedisHelper redisHelper,
                                        JobInfoService jobInfoService,
                                        ConcurrentRepository concurrentRepository,
                                        ExecutableRepository executableRepository,
                                        ConcPermissionRepository permissionRepository,
                                        ConcurrentRequestRepository requestRepository) {
        this.jobService = jobService;
        this.redisHelper = redisHelper;
        this.jobInfoService = jobInfoService;
        this.concurrentRepository = concurrentRepository;
        this.executableRepository = executableRepository;
        this.permissionRepository = permissionRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Page<ConcurrentRequest> pageRequest(RequestQueryDTO requestQueryDTO, PageRequest pageRequest) {
        String jobStatus = requestQueryDTO.getJobStatus();
        if (StringUtils.isNotBlank(jobStatus)) {
            requestQueryDTO.setTriggerStatus(jobService.getTriggerStatus(jobStatus));
            if (Objects.equals(jobStatus, HsdrConstant.JobStatus.NONE)) {
                requestQueryDTO.setIncludeNull(true);
            }
        }
        Page<ConcurrentRequest> page = requestRepository.pageRequest(pageRequest, requestQueryDTO);
        page.forEach(item -> item.setJobStatus(jobService.getJobStatus(item.getJobId(), item.getTenantId())));
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcurrentRequest createRequest(ConcurrentRequest concurrentRequest) {
        // 校验权限定义配置的限制次数
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Assert.notNull(userDetails, HsdrErrorCode.USER_DETAIL_NOT_FOUND);
        Assert.notNull(userDetails.getRoleId(), HsdrErrorCode.USER_DETAIL_NOT_FOUND);
        Long roleId = userDetails.getRoleId();
        Long tenantId = concurrentRequest.getTenantId();
        Long concurrentId = concurrentRequest.getConcurrentId();
        if (DetailsHelper.getUserDetails().getAdmin()) {
            // 管理员不校验权限，直接创建任务
            addJob(concurrentRequest);
            requestRepository.insertSelective(concurrentRequest);
            return concurrentRequest;
        }
        // sql中过滤了超时的权限
        List<ConcPermission> permissions = permissionRepository.selectQuantity(roleId, tenantId, concurrentId);
        if (CollectionUtils.isEmpty(permissions)) {
            // 没有查询到当前角色权限,查询角色id为-1的权限
            permissions = permissionRepository.selectQuantity(HsdrConstant.NEGATIVE_ONE, tenantId, concurrentId);
        }
        if (CollectionUtils.isEmpty(permissions) || BaseConstants.Flag.NO.equals(permissions.get(0).getEnabledFlag())) {
            // 并发请求权限未配置或配置未启用，不可创建任务
            throw new CommonException(HsdrErrorCode.USER_AUTHORITY_ERROR);
        }
        ConcPermission permission = permissions.get(0);
        if (permission.getLimitQuantity() == null) {
            // 没有次数限制，直接创建任务
            addJob(concurrentRequest);
            requestRepository.insertSelective(concurrentRequest);
        } else {
            Integer limitQuantity = permission.getLimitQuantity();

            // 查询缓存
            boolean flag = true;
            String dateTime = String.valueOf(LocalDate.now());
            Integer quantity = concurrentRequest.getCache(redisHelper, tenantId, roleId, concurrentId, dateTime);
            if (quantity != null && quantity >= limitQuantity) {
                throw new CommonException(HsdrErrorCode.LIMIT_QUANTITY_OUT, limitQuantity);
            }
            if (quantity == null) {
                flag = false;
            }

            // 添加任务
            addJob(concurrentRequest);
            requestRepository.insertSelective(concurrentRequest);

            // 操作缓存
            if (!flag) {
                concurrentRequest.initCache(redisHelper, tenantId, roleId, concurrentId, dateTime, BaseConstants.Digital.ONE);
            } else {
                concurrentRequest.refreshCache(redisHelper, tenantId, roleId, concurrentId, dateTime, BaseConstants.Digital.ONE);
            }
        }
        return concurrentRequest;
    }

    /**
     * 并发请求与任务信息表关联
     *
     * @param request 并发请求
     */
    private void addJob(ConcurrentRequest request) {
        // 并发请求
        Concurrent concurrent = concurrentRepository.selectByPrimaryKey(request.getConcurrentId());
        Assert.notNull(concurrent, HsdrErrorCode.CONCURRENT_NOT_FIND);
        Long concurrentId = concurrent.getExecutableId();
        Executable executable = executableRepository.selectByPrimaryKey(concurrentId);
        Assert.notNull(executable, HsdrErrorCode.EXECUTABLE_NOT_EXIST);
        Long executableId = executable.getExecutableId();
        Long executorId = executable.getExecutorId();
        // 补充并发请求数据
        request.setExecutableId(executableId);
        request.setExecutorId(executorId);
        if (Objects.equals(request.getCycleFlag(), BaseConstants.Flag.YES) && StringUtils.isBlank(request.getCron())) {
            // 周期任务
            String cron = CronUtils.getCron(request.getIntervalHour(), request.getIntervalMinute(), request.getIntervalSecond(), request.getIntervalNumber(), request.getIntervalType());
            request.setCron(cron);
        }
        // 组装jobInfo, 添加任务
        JobInfo jobInfo = new JobInfo()
                .setExecutorId(executorId)
                .setJobCode(concurrent.getConcCode())
                .setJobCron(request.getCron())
                .setDescription(concurrent.getConcDescription())
                .setJobParam(request.getRequestParam())
                .setAlarmEmail(concurrent.getAlarmEmail())
                .setExecutorStrategy(executable.getExecutorStrategy())
                .setFailStrategy(executable.getFailStrategy())
                .setStrategyParam(executable.getStrategyParam())
                .setGlueType(executable.getExeTypeCode())
                .setJobHandler(executable.getJobHandler())
                .setTenantId(request.getTenantId())
                .setStartDate(request.getStartDate())
                .setEndDate(request.getEndDate())
                .setCycleFlag(request.getCycleFlag());
        jobInfo = jobInfoService.createJob(jobInfo);
        // 回写jobId
        Assert.notNull(jobInfo.getJobId(), HsdrErrorCode.CREATE_JOB);
        request.setJobId(jobInfo.getJobId());
    }
}