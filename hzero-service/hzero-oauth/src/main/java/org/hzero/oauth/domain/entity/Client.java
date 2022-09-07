package org.hzero.oauth.domain.entity;

import org.hzero.oauth.domain.vo.Role;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 客户端
 *
 * @author bojiangzhou 2019/05/28
 */
@ModifyAudit
@VersionAudit
@Table(name = "oauth_client")
public class Client extends AuditDomain implements Serializable {
    private static final long serialVersionUID = -8770569494296714270L;

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long organizationId;
    private String resourceIds;
    private String secret;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private Long accessTokenValidity;
    private Long refreshTokenValidity;
    private String additionalInformation;
    private String autoApprove;
    private Integer enabledFlag;
    private String accessRoles;
    private Integer pwdReplayFlag;
    @Column(name = "time_zone")
    private String clientTimeZone;

    //
    // user info
    // ------------------------------------------------------------------------------
    @Transient
    private Long userId;
    @Transient
    private String realName;
    @Transient
    private String email;
    @Transient
    private String timeZone;
    @Transient
    private String language;
    @Transient
    private Boolean isAdmin;
    @Transient
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    public Long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(Long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public Long getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public void setRefreshTokenValidity(Long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAutoApprove() {
        return autoApprove;
    }

    public void setAutoApprove(String autoApprove) {
        this.autoApprove = autoApprove;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getAccessRoles() {
        return accessRoles;
    }

    public Client setAccessRoles(String accessRoles) {
        this.accessRoles = accessRoles;
        return this;
    }

    public Integer getPwdReplayFlag() {
        return pwdReplayFlag;
    }

    public Client setPwdReplayFlag(Integer pwdReplayFlag) {
        this.pwdReplayFlag = pwdReplayFlag;
        return this;
    }

    public String getClientTimeZone() {
        return clientTimeZone;
    }

    public Client setClientTimeZone(String clientTimeZone) {
        this.clientTimeZone = clientTimeZone;
        return this;
    }
}
