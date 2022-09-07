package org.hzero.oauth.domain.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class ClientRoleDetails {
    private Long tenantId;
    private List<Role> roles = new ArrayList<>();

    public Long getTenantId() {
        return tenantId;
    }

    public ClientRoleDetails setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public ClientRoleDetails setRoles(List<Role> roles) {
        this.roles = roles;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRoleDetails that = (ClientRoleDetails) o;
        return Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, roles);
    }

    @Override
    public String toString() {
        return "ClientRoleDetails{" +
                "tenantId=" + tenantId +
                ", roles=" + roles +
                '}';
    }
}
