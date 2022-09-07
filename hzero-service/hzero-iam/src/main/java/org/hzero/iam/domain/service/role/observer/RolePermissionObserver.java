package org.hzero.iam.domain.service.role.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.core.observer.Observer;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RolePermissionRepository;
import org.hzero.iam.domain.service.role.validator.RoleCreateValidator;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.constant.RolePermissionType;

/**
 * 角色权限
 *
 * @author bojiangzhou 2020/07/08
 */
@Component
public class RolePermissionObserver implements Observer<Role> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionObserver.class);

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public int order() {
        return 20;
    }

    @Override
    public void update(@Nonnull Role role, Object... args) {
        User adminUser = (User) args[0];
        boolean inherited = (boolean) args[1];
        boolean duplicate = (boolean) args[2];
        RoleCreateValidator validator = (RoleCreateValidator) args[3];

        List<RolePermission> rolePermissionList = new ArrayList<>();

        // 检查继承的角色
        if (inherited) {
            rolePermissionList = handleInheritRolePermission(role, adminUser, validator);
            LOGGER.info("Inherit role permission, roleId: {}, inheritRoleId: {} permissionSize: {}", role.getId(), role.getInheritRoleId(), rolePermissionList.size());
        }
        // 检查复制的角色
        else if (duplicate) {
            rolePermissionList = handleDuplicateRolePermission(role, adminUser, validator);
            LOGGER.info("Copy role permission, roleId: {}, copyRoleId: {} permissionSize: {}", role.getId(), role.getCopyFromRoleId(), rolePermissionList.size());
        } else {
            LOGGER.info("Direct create role, empty permission.");
        }

        // 维护 iam_role_permission 表
        if (CollectionUtils.isNotEmpty(rolePermissionList)) {
            rolePermissionRepository.batchInsertBySql(rolePermissionList);
        }
    }

    /**
     * 复制继承的权限集，并打上继承标识
     */
    protected List<RolePermission> handleInheritRolePermission(Role role, User adminUser, RoleCreateValidator validator) {
        validator.checkRoleIsSubOfParentRole(role.getParentRoleId(), role.getInheritRoleId());

        // 查询出继承的权限集
        List<RolePermission> rolePermissionList = selectRolePermission(role.getLevel(), role.getInheritRoleId());

        Long roleId = role.getId();
        Long tenantId = role.getTenantId();

        return rolePermissionList.stream().map(rp -> new RolePermission(
                roleId, rp.getPermissionSetId(),
                Constants.YesNoFlag.YES,
                Constants.YesNoFlag.NO,
                RolePermissionType.PS.name(),
                tenantId)
        ).collect(Collectors.toList());
    }

    /**
     * 复制角色的权限集，并打上创建标识
     */
    protected List<RolePermission> handleDuplicateRolePermission(Role role, User adminUser, RoleCreateValidator validator) {
        validator.checkRoleIsSubOfParentRole(role.getParentRoleId(), role.getCopyFromRoleId());

        // 查询出继承的权限集
        List<RolePermission> rolePermissionList = selectRolePermission(role.getLevel(), role.getCopyFromRoleId());

        Long roleId = role.getId();
        Long tenantId = role.getTenantId();

        return rolePermissionList.stream().map(rp -> new RolePermission(
                roleId, rp.getPermissionSetId(),
                Constants.YesNoFlag.NO,
                Constants.YesNoFlag.YES,
                RolePermissionType.PS.name(),
                tenantId)
        ).collect(Collectors.toList());
    }

    private List<RolePermission> selectRolePermission(String roleLevel, Long targetRoleId) {
        RolePermission params = new RolePermission();
        params.setRoleId(targetRoleId).setLevel(roleLevel);

        List<RolePermission> list = rolePermissionRepository.selectRolePermissionSets(params);

        // 角色中有权限的才能被复制或继承
        return list.stream().filter(item -> StringUtils.equalsAny(Constants.YesNoFlag.YES,
                item.getCreateFlag(), item.getInheritFlag())).collect(Collectors.toList());
    }

}
