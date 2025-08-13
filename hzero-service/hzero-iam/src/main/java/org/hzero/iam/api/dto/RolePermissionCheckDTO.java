package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

/**
 * 角色权限集检查参数DTO
 *
 * @author allen.liu
 * @date 2019/9/5
 */
public class RolePermissionCheckDTO {
    /**
     * 待检查权限集
     */
    @Encrypt
    private Long permissionSetId;
    /**
     * 待检查权限集类型
     */
    private String type;
    /**
     * 检查范围
     * user-用户
     * roles-角色集合
     * client-客户端
     */
    private String checkScope;
    private Long userId;
    private Long clientId;
    private List<Long> roleIds;

    /**
     * 校验数据有效性
     */
    public void validate() {
        Assert.notNull(permissionSetId, "permission set id cannot be null.");
        Assert.hasText(type, "type cannot be empty.");
        if (CheckScope.USER.equals(checkScope)) {
            Assert.notNull(userId, "when user scope, user id cannot be null.");
        } else if (CheckScope.CLIENT.equals(checkScope)) {
            Assert.notNull(clientId, "when client scope, client id cannot be null.");
        } else if (CheckScope.ROLES.equals(checkScope)) {
            Assert.notEmpty(roleIds, "when roles scope, client id cannot be empty.");
        } else {
            throw new UnsupportedOperationException("invalid check scope: " + checkScope);
        }
    }

    public Long getPermissionSetId() {
        return permissionSetId;
    }

    public RolePermissionCheckDTO setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
        return this;
    }

    public String getType() {
        return type;
    }

    public RolePermissionCheckDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getCheckScope() {
        return checkScope;
    }

    public RolePermissionCheckDTO setCheckScope(String checkScope) {
        this.checkScope = checkScope;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RolePermissionCheckDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getClientId() {
        return clientId;
    }

    public RolePermissionCheckDTO setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public RolePermissionCheckDTO setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    public static final class CheckScope {
        public static final String  USER = "user";
        public static final String  CLIENT = "client";
        public static final String  ROLES = "roles";
    }
}
