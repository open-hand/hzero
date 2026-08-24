package org.hzero.iam.domain.service.role;

import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.common.utils.UserUtils;

/**
 * 角色更新相关核心业务抽象类
 *
 * @author bojiangzhou 2019/01/23
 */
public class RoleUpdateService extends AbstractRoleLabelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUpdateService.class);

    @Autowired
    protected MemberRoleRepository memberRoleRepository;

    /**
     * 更新角色信息
     *
     * @param role 角色
     */
    public Role updateRole(Role role) {
        // 检查角色
        checkRole(role);

        // 更新角色信息
        persistRole(role);

        // 更新角色标签信息
        handleRoleLabels(role);

        // 更新完成后的后置处理
        postHandle(role);

        return role;
    }


    /**
     * 启用角色
     *
     * @param roleId         角色ID
     * @param validateParent 是否校验父级角色已启用
     * @param enableSubRole  是否启用子孙角色
     */
    public void enableRole(Long roleId, boolean validateParent, boolean enableSubRole) {
        Assert.notNull(roleId, "Param roleId is null.");
        Role role = this.roleRepository.selectByPrimaryKey(roleId);
        if (role == null) {
            throw new CommonException("hiam.error.role.updateRoleNotFound");
        }

        if (validateParent && !Role.ROOT_ID.equals(role.getParentRoleId())) {
            Role parentRole = roleRepository.selectRoleSimpleById(role.getParentRoleId());
            if (parentRole == null || BooleanUtils.isFalse(parentRole.getEnabled())) {
                throw new CommonException("error.role.enable.parentRole.disabled");
            }
        }

        // 批量更新状态
        roleRepository.batchUpdateEnableFlag(roleId, BaseConstants.Flag.YES, enableSubRole);
    }

    /**
     * 禁用角色
     *
     * @param roleId         角色ID
     * @param disableSubRole 是否禁用子孙角色
     */
    public void disableRole(Long roleId, boolean disableSubRole) {
        Assert.notNull(roleId, "Param roleId is null.");
        Role role = this.roleRepository.selectByPrimaryKey(roleId);
        if (role == null) {
            throw new CommonException("hiam.error.role.updateRoleNotFound");
        }

        CustomUserDetails self = UserUtils.getUserDetails();

        if (!self.isRoleMergeFlag() && Objects.equals(roleId, self.getRoleId())) {
            throw new CommonException("hiam.warn.role.cantDisableCurrentRole");
        }

        if (roleRepository.isTopAdminRole(self.getUserId(), roleId)) {
            throw new CommonException("hiam.warn.role.cantDisableTopRole");
        }

        // 查询子孙角色中分配给自己的角色
        //List<RoleVO> roles = roleRepository.selectSubAssignedRoles(roleId, self.getUserId());
        //boolean error = false;
        //if (CollectionUtils.isNotEmpty(roles)) {
        //    if (disableSubRole) {
        //        error = true;
        //    } else {
        //        error = roles.stream().anyMatch(r -> roleId.equals(r.getId()));
        //    }
        //}
        //
        //if (error) {
        //    throw new CommonException("hiam.warn.role.cantDisableAssignedRole");
        //}

        // 批量更新状态
        roleRepository.batchUpdateEnableFlag(roleId, BaseConstants.Flag.NO, disableSubRole);
    }

    protected void checkRole(Role role) {
        Role exists = roleRepository.selectByPrimaryKey(role.getId());
        if (exists == null) {
            throw new CommonException("hiam.warn.role.notFound");
        }
    }

    /**
     * 更新角色相关属性
     *
     * @param role 角色
     */
    protected void persistRole(Role role) {
        roleRepository.updateOptional(role, Role.FIELD_NAME, Role.FIELD_DESCRIPTION);
    }

    /**
     * 角色处理完成后，最后处理的一些事情
     *
     * @param role 角色信息
     */
    protected void postHandle(Role role) {
        role.setPermissionSets(null);
    }
}
