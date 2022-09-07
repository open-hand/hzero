package org.hzero.oauth.domain.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户角色信息
 * </p>
 *
 * @author qingsheng.chen 2018/9/5 星期三 9:07
 */
public class UserRoleDetails {
    private Long defaultTenantId;
    private Long tenantId;
    private String tenantNum;
    private Long defaultRoleId;
    private Date accessDatetime;
    private Boolean roleMergeFlag;
    private String userLanguage;

    private List<Role> roles = new ArrayList<>();

    public Long getDefaultTenantId() {
        return defaultTenantId;
    }

    public UserRoleDetails setDefaultTenantId(Long defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public UserRoleDetails setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public UserRoleDetails setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
        return this;
    }

    public Long getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(Long defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
    }

    public Date getAccessDatetime() {
        return accessDatetime;
    }

    public UserRoleDetails setAccessDatetime(Date accessDatetime) {
        this.accessDatetime = accessDatetime;
        return this;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Boolean getRoleMergeFlag() {
        return roleMergeFlag;
    }

    public UserRoleDetails setRoleMergeFlag(Boolean roleMergeFlag) {
        this.roleMergeFlag = roleMergeFlag;
        return this;
    }

    public String getUserLanguage() {
        return userLanguage;
    }

    public UserRoleDetails setUserLanguage(String userLanguage) {
        this.userLanguage = userLanguage;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserRoleDetails that = (UserRoleDetails) o;
        return Objects.equals(defaultTenantId, that.defaultTenantId) &&
                Objects.equals(tenantId, that.tenantId) &&
                Objects.equals(tenantNum, that.tenantNum) &&
                Objects.equals(defaultRoleId, that.defaultRoleId) &&
                Objects.equals(accessDatetime, that.accessDatetime) &&
                Objects.equals(roleMergeFlag, that.roleMergeFlag) &&
                Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultTenantId, tenantId, tenantNum, defaultRoleId, accessDatetime, roleMergeFlag, roles);
    }

    @Override
    public String toString() {
        return "UserRoleDetails{" +
                "defaultTenantId=" + defaultTenantId +
                ", tenantId=" + tenantId +
                ", tenantNum='" + tenantNum + '\'' +
                ", defaultRoleId=" + defaultRoleId +
                ", accessDatetime=" + accessDatetime +
                ", roleMergeFlag=" + roleMergeFlag +
                ", roles=" + roles +
                '}';
    }
}
