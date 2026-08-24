package org.hzero.iam.app.service.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.hzero.iam.app.service.PermissionService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.LabelRelRepository;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.repository.TenantPermissionRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.constant.LabelAssignType;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * API管理
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019/12/2
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private TenantPermissionRepository tenantPermissionRepository;
    @Autowired
    private LabelRelRepository labelRelRepository;

    @Override
    public void updateApis(List<Permission> permissions) {
        permissionRepository.batchUpdateOptional(permissions, Permission.FIELD_DESCRIPTION);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApis(List<Permission> permissions) {
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }
        // 权限IDs
        Set<Long> permissionIds = permissions.stream().map(Permission::getId).collect(Collectors.toSet());
        // 删除分配给租户的权限
        tenantPermissionRepository.removeByPermissionIds(permissionIds);
        // 处理删除权限的标签
        this.handleDeletePermissionLabels(permissionIds);
        // 删除权限
        permissionRepository.batchDeleteByPrimaryKey(permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTenantApis(List<Permission> permissions) {
        List<TenantPermission> tenantPermissions = permissions.stream()
                .map(p -> new TenantPermission(p.getTenantId(), p.getId())).collect(toList());

        tenantPermissionRepository.batchDelete(tenantPermissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTenantApis(Long tenantId, List<Permission> permissions) {
        Tenant tenant = tenantRepository.selectByPrimaryKey(tenantId);
        if (tenant == null) {
            throw new CommonException("hiam.warn.user.tenantNotFound");
        }

        List<Permission> ps = permissionRepository
                .selectByIds(permissions.stream().map(Permission::getId).collect(toList()));
        if (ps.stream().anyMatch(p -> !HiamResourceLevel.ORGANIZATION.getApiLevel().contains(p.getLevel()))) {
            throw new CommonException("hiam.warn.permission.assignOrganizationLevel");
        }

        for (Permission p : ps) {
            TenantPermission tp = new TenantPermission(tenantId, p.getId());
            if (tenantPermissionRepository.selectCount(tp) == 0) {
                tenantPermissionRepository.insertSelective(tp);

            }
        }
    }

    @Override
    public void assignTenantApis(Long[] tenantIds, List<Permission> permissions) {
        for (Long tenantId : tenantIds) {
            this.assignTenantApis(tenantId, permissions);
        }
    }

    @Override
    public void updateApi(Permission permission) {
        if (BooleanUtils.isTrue(permission.getSignAccess())) {
            return;
        }

        // 处理权限标签
        this.handleUpdatePermissionLabels(permission);
        // 更新权限标签和描述信息
        permissionRepository.updateOptional(permission, Permission.PERMISSION_TAG, Permission.FIELD_DESCRIPTION);
    }

    @Override
    public Page<PermissionVO> pageApis(PermissionVO params, PageRequest pageRequest) {
        return permissionRepository.pageApis(params, pageRequest);
    }

    @Override
    public Page<PermissionVO> pageTenantApis(PermissionVO params, PageRequest pageRequest) {
        return permissionRepository.pageTenantApis(params, pageRequest);
    }

    /**
     * 处理删除权限时的标签数据
     *
     * @param permissionIds 权限数据
     */
    private void handleDeletePermissionLabels(Set<Long> permissionIds) {
        // 查询标签关系数据
        List<Long> labelRelationIds = Optional.ofNullable(this.labelRelRepository.selectByCondition(Condition.builder(LabelRel.class)
                .select(LabelRel.FIELD_LABEL_REL_ID)
                .andWhere(Sqls.custom()
                        .andEqualTo(LabelRel.FIELD_DATA_TYPE, Permission.LABEL_DATA_TYPE)
                        .andIn(LabelRel.FIELD_DATA_ID, permissionIds)
                ).build())).orElse(Collections.emptyList())
                .stream().map(LabelRel::getLabelRelId).collect(toList());
        // 删除数据
        this.labelRelRepository.batchDeleteByIds(labelRelationIds);
    }

    /**
     * 处理权限标签
     *
     * @param permission 权限对象
     */
    private void handleUpdatePermissionLabels(Permission permission) {
        // 获取权限标签视图数据
        List<Label> permissionLabels = permission.getLabels();
        if (Objects.isNull(permissionLabels)) {
            // ！！！注意：如果权限标签的数据是 null，就代表不处理角色标签
            return;
        }
        // 更新标签数据
        this.labelRelRepository.updateLabelRelationsByLabelView(Permission.LABEL_DATA_TYPE,
                permission.getId(), LabelAssignType.MANUAL, permissionLabels.stream().map(Label::getId).filter(Objects::nonNull).collect(toSet()));
    }
}
