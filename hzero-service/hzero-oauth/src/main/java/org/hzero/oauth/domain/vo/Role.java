package org.hzero.oauth.domain.vo;

import java.util.Objects;

/**
 * <p>
 * 角色ID
 * </p>
 *
 * @author qingsheng.chen 2018/9/5 星期三 9:50
 */
public class Role {
    private Long id;
    private Long tenantId;
    private String level;
    private String assignLevel;
    private Long assignValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getLevel() {
        return level;
    }

    public Role setLevel(String level) {
        this.level = level;
        return this;
    }

    public String getAssignLevel() {
        return assignLevel;
    }

    public Role setAssignLevel(String assignLevel) {
        this.assignLevel = assignLevel;
        return this;
    }

    public Long getAssignValue() {
        return assignValue;
    }

    public Role setAssignValue(Long assignValue) {
        this.assignValue = assignValue;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) &&
                Objects.equals(tenantId, role.tenantId) &&
                Objects.equals(level, role.level) &&
                Objects.equals(assignLevel, role.assignLevel) &&
                Objects.equals(assignValue, role.assignValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, level, assignLevel, assignValue);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", level='" + level + '\'' +
                ", assignLevel='" + assignLevel + '\'' +
                ", assignValue=" + assignValue +
                '}';
    }
}
