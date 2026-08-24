package org.hzero.boot.scheduler.api.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.hzero.core.base.BaseConstants;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/22 14:29
 */
public class UserInfo implements Serializable {

    private Long userId;
    private String realName;
    private String email;
    private String timeZone;
    private String language;
    private Long roleId;
    private List<Long> roleIds;
    private Long tenantId;
    private List<Long> tenantIds;
    private Long organizationId;
    private Boolean isAdmin;
    private Long clientId;
    private String clientName;

    public UserInfo() {
        this.userId = BaseConstants.ANONYMOUS_USER_ID;
        this.realName = "匿名用户";
        this.email = "anonymous@hzero.com";
        this.timeZone = "GMT+8";
        this.language = "zh_CN";
        this.roleId = -1L;
        this.roleIds = Collections.singletonList(-1L);
        this.tenantId = 0L;
        this.tenantIds = Collections.singletonList(0L);
        this.organizationId = 0L;
        this.isAdmin = false;
    }

    public UserInfo(CustomUserDetails customUserDetails) {
        this.userId = customUserDetails.getUserId();
        this.realName = customUserDetails.getRealName();
        this.email = customUserDetails.getEmail();
        this.timeZone = customUserDetails.getTimeZone();
        this.language = customUserDetails.getLanguage();
        this.roleId = customUserDetails.getRoleId();
        this.roleIds = customUserDetails.getRoleIds();
        this.tenantId = customUserDetails.getTenantId();
        this.tenantIds = customUserDetails.getTenantIds();
        this.organizationId = customUserDetails.getOrganizationId();
        this.isAdmin = customUserDetails.getAdmin();
        this.clientId = customUserDetails.getClientId();
        this.clientName = customUserDetails.getClientName();
    }

    public CustomUserDetails buildCustomUserDetails() {
        CustomUserDetails userDetails = new CustomUserDetails("default", "default");
        userDetails.setUserId(this.userId);
        userDetails.setRealName(this.realName);
        userDetails.setEmail(this.email);
        userDetails.setTimeZone(this.timeZone);
        userDetails.setLanguage(this.language);
        userDetails.setRoleId(this.roleId);
        userDetails.setRoleIds(this.roleIds);
        userDetails.setTenantId(this.tenantId);
        userDetails.setTenantIds(this.tenantIds);
        userDetails.setOrganizationId(this.organizationId);
        userDetails.setAdmin(this.isAdmin);
        userDetails.setClientId(this.clientId);
        userDetails.setClientName(this.clientName);
        return userDetails;
    }

    public Long getUserId() {
        return userId;
    }

    public UserInfo setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public UserInfo setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public UserInfo setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public UserInfo setLanguage(String language) {
        this.language = language;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public UserInfo setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public UserInfo setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public UserInfo setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public List<Long> getTenantIds() {
        return tenantIds;
    }

    public UserInfo setTenantIds(List<Long> tenantIds) {
        this.tenantIds = tenantIds;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public UserInfo setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public UserInfo setAdmin(Boolean admin) {
        isAdmin = admin;
        return this;
    }

    public Long getClientId() {
        return clientId;
    }

    public UserInfo setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientName() {
        return clientName;
    }

    public UserInfo setClientName(String clientName) {
        this.clientName = clientName;
        return this;
    }
}
