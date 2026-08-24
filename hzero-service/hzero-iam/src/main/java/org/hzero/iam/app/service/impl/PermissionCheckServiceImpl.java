package org.hzero.iam.app.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.AopProxy;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.MenuPermissionSetDTO;
import org.hzero.iam.app.service.IDocumentService;
import org.hzero.iam.app.service.PermissionCheckService;
import org.hzero.iam.domain.entity.PermissionCheck;
import org.hzero.iam.domain.repository.PermissionCheckRepository;
import org.hzero.iam.domain.service.MenuCoreService;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.HiamMenuType;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.PermissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 缺失权限应用服务实现
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 * @author xianzhi.chen@hand-china.com 新增菜单权限缺失刷新问题
 */
@Service
public class PermissionCheckServiceImpl implements PermissionCheckService, AopProxy<PermissionCheckServiceImpl> {

    @Autowired
    private PermissionCheckRepository permissionCheckRepository;
    @Autowired
    private MenuCoreService menuCoreService;
    @Autowired
    private IDocumentService documentService;

    @Autowired
    @Qualifier("IamCommonAsyncTaskExecutor")
    private ThreadPoolExecutor executor;

    @Override
    public void clearPermissionCheck(String clearType, String checkState) {
        LocalDate now = LocalDate.now();
        switch (clearType) {
            case Constants.ClearPermissionCheck.THREE_DAY:
                self().asyncClearPermissionCheck(now.minus(3, ChronoUnit.DAYS), checkState);
                break;
            case Constants.ClearPermissionCheck.ONE_WEEK:
                self().asyncClearPermissionCheck(now.minus(1, ChronoUnit.WEEKS), checkState);
                break;
            case Constants.ClearPermissionCheck.ONE_MONTH:
                self().asyncClearPermissionCheck(now.minus(1, ChronoUnit.MONTHS), checkState);
                break;
            case Constants.ClearPermissionCheck.THREE_MONTH:
                self().asyncClearPermissionCheck(now.minus(3, ChronoUnit.MONTHS), checkState);
                break;
            case Constants.ClearPermissionCheck.SIX_MONTH:
                self().asyncClearPermissionCheck(now.minus(6, ChronoUnit.MONTHS), checkState);
                break;
            case Constants.ClearPermissionCheck.ONE_YEAR:
                self().asyncClearPermissionCheck(now.minus(1, ChronoUnit.YEARS), checkState);
                break;
            default:
                break;
        }
    }

    @Async("IamCommonAsyncTaskExecutor")
    public void asyncClearPermissionCheck(LocalDate localDate, String checkState) {
        PageRequest pageRequest = new PageRequest(0, 1000);
        Page<Long> page;
        do {
            page = permissionCheckRepository.listPermissionCheckId(localDate, checkState, pageRequest);
            List<Long> permissionCheckIds = page.getContent();
            if (CollectionUtils.isNotEmpty(permissionCheckIds)) {
                permissionCheckRepository.batchDeleteById(permissionCheckIds);
            }
        } while (CollectionUtils.isNotEmpty(page.getContent()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPermissionSet(List<Long> menuIds, PermissionType permissionType, String[] permissionCode,
                                 String checkState) {
        if (!Constants.CheckState.PERMISSION_NOT_PASS.equals(checkState)) {
            throw new CommonException("error.permission.check.not-match");
        }
        for (Long menuId : menuIds) {
            menuCoreService.assignPsPermissions(menuId, permissionType, permissionCode);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignNotPassApi(MenuPermissionSetDTO menuPermissionSetDTO) {
        PermissionCheck param = new PermissionCheck();
        param.untreated();
        param.notPass();
        Set<String> codes = new HashSet<>(menuPermissionSetDTO.getPermissionCodes().length);
        for (String permissionCode : menuPermissionSetDTO.getPermissionCodes()) {
            param.setPermissionCode(permissionCode);
            List<PermissionCheck> list = permissionCheckRepository.select(param);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            // 更改状态
            for (PermissionCheck permissionCheck : list) {
                if (!StringUtils.equals(permissionCheck.getFdLevel(), menuPermissionSetDTO.getLevel())) {
                    throw new CommonException("hiam.warn.permissionCheck.levelNotEquals");
                }
                codes.add(permissionCheck.getPermissionCode());
                permissionCheck.processed();
                permissionCheckRepository.updateOptional(permissionCheck, PermissionCheck.FIELD_HANDLE_STATUS);
            }
        }

        if (CollectionUtils.isEmpty(codes)) {
            return;
        }
        for (Long menuId : menuPermissionSetDTO.getMenuIds()) {
            // 分配到权限集
            menuCoreService.assignPsPermissions(menuId, menuPermissionSetDTO.getPermissionType(), codes.toArray(new String[0]));
        }
    }

    @Override
    public void refreshMismatchApi(String[] serviceCodes) {
        PermissionCheck param = new PermissionCheck();
        param.untreated();
        param.mismatch();
        Set<String> services = new HashSet<>(serviceCodes.length);
        for (String serviceCode : serviceCodes) {
            param.setServiceName(serviceCode);
            List<PermissionCheck> list = permissionCheckRepository.select(param);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (PermissionCheck permissionCheck : list) {
                services.add(serviceCode);
                permissionCheck.processed();
                permissionCheckRepository.updateOptional(permissionCheck, PermissionCheck.FIELD_HANDLE_STATUS);
            }
        }
        if (CollectionUtils.isEmpty(services)) {
            return;
        }
        for (String service : services) {
            // 刷新权限
            executor.submit(() -> documentService.refreshPermission(service, null, null));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenuPermissionApi(MenuPermissionSetDTO menuPermissionSetDTO) {
        if (Objects.isNull(menuPermissionSetDTO) || ArrayUtils.isEmpty(menuPermissionSetDTO.getPermissionCodes())) {
            // 如果没传参数，则直接返回即可
            return;
        }
        // 获取权限集对应的菜单默认权限集等信息
        List<PermissionCheck> resultList = permissionCheckRepository.selectMenuPermissionSet(menuPermissionSetDTO);
        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        // 搜集数据，true则可以进行下级操作，false则需记录失败
        Map<Boolean, List<PermissionCheck>> partitionMap =
                resultList.stream().collect(Collectors.partitioningBy(pc -> {
                    if (pc.getPermissionSetId() == null || StringUtils.isBlank(pc.getMenuLevel())
                            || !HiamMenuType.PS.value().equals(pc.getMenuType())) {
                        return false;
                    }
                    // 仅过滤API层级不匹配的数据，值集的数据在菜单分配权限集步骤中去校验
                    if (Constants.PermissionTypeCode.API.equals(pc.getPermissionType())) {
                        Set<String> apiLevel = HiamResourceLevel.levelOf(pc.getMenuLevel()).getApiLevel();
                        return apiLevel.contains(pc.getFdLevel());
                    }
                    return true;
                }));
        List<PermissionCheck> trueList = partitionMap.get(true);
        List<PermissionCheck> falseList = partitionMap.get(false);
        if (CollectionUtils.isEmpty(trueList)) {
            // 处理失败状态
            processPermissionCheckStatus(falseList, Constants.HandlerCheckStatus.HANDLE_STATUS_FAILED);
            return;
        }
        // 对查询出来的权限集进行分组,(权限集ID为空的数据在之前已经去除，因此直接处理数据格式即可)
        Map<PermissionType, Map<Long, Set<String>>> resultMap =
                trueList.stream().collect(Collectors.groupingBy(permissionCheck ->
                                        this.convertPermissionType(permissionCheck.getPermissionType()),
                                Collectors.groupingBy(PermissionCheck::getPermissionSetId,
                                        Collectors.mapping(PermissionCheck::getPermissionCode,
                                                Collectors.toSet()))));
        resultMap.forEach((key, value) -> {
            Set<Entry<Long, Set<String>>> resultSet = value.entrySet();
            for (Entry<Long, Set<String>> result : resultSet) {
                Long permissionSetId = result.getKey();
                Set<String> permissionCodes = result.getValue();
                if (permissionSetId != null) {
                    // 分配到权限集
                    menuCoreService.assignPsPermissions(permissionSetId, key, permissionCodes.toArray(new String[0]));
                }
            }
        });
        // 回写成功失败状态
        processPermissionCheckStatus(trueList, Constants.HandlerCheckStatus.HANDLE_STATUS_PROCESSED);
        processPermissionCheckStatus(falseList, Constants.HandlerCheckStatus.HANDLE_STATUS_FAILED);
    }

    /**
     * 回写缺失权限状态
     *
     * @param permissionChecks 回写数据
     * @param status           回写状态
     */
    private void processPermissionCheckStatus(List<PermissionCheck> permissionChecks, String status) {
        // 回写状态
        for (PermissionCheck permissionCheck : permissionChecks) {
            PermissionCheck dbPermissionCheck = permissionCheckRepository.selectByPrimaryKey(permissionCheck.getPermissionCheckId());
            if (dbPermissionCheck != null && Constants.HandlerCheckStatus.HANDLE_STATUS_UNTREATED.equals(dbPermissionCheck.getHandleStatus())) {
                // 若数据库中的缺失权限处理状态为未处理，则更新状态，否则直接略过即可
                dbPermissionCheck.setHandleStatus(status);
                permissionCheckRepository.updateOptional(dbPermissionCheck, PermissionCheck.FIELD_HANDLE_STATUS);
            }
        }
    }

    /**
     * 将permissionCheck中的permissionType值转换为枚举值
     *
     * @param permissionType 权限类型值
     * @return PermissionType
     */
    private PermissionType convertPermissionType(String permissionType) {
        switch (permissionType) {
            case Constants.PermissionTypeCode.API:
                return PermissionType.PERMISSION;
            case Constants.PermissionTypeCode.LOV:
                return PermissionType.LOV;
            default:
                throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
    }

}
