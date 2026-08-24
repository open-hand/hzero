package org.hzero.iam.api.dto;

import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class RolePermissionWithDTO {
    @Encrypt
    private Long roleId;
    private String roleCode;
    private Integer hasPermission;
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public RolePermissionWithDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public RolePermissionWithDTO setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public RolePermissionWithDTO setRoleCode(String roleCode) {
        this.roleCode = roleCode;
        return this;
    }

    public Integer getHasPermission() {
        return hasPermission;
    }

    public RolePermissionWithDTO setHasPermission(Integer hasPermission) {
        this.hasPermission = hasPermission;
        return this;
    }

    public boolean hasPermission() {
        return BaseConstants.Flag.YES.equals(hasPermission);
    }
}
