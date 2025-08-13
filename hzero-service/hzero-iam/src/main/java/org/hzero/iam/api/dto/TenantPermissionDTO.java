package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.iam.domain.entity.Permission;

/**
 * 租户权限关系
 * 
 * @author bojiangzhou 2019/12/06
 */
public class TenantPermissionDTO {

    private Long[] tenantIds;
    private List<Permission> permissions;

    public Long[] getTenantIds() {
        return tenantIds;
    }

    public void setTenantIds(Long[] tenantIds) {
        this.tenantIds = tenantIds;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
