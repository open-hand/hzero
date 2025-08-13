package org.hzero.boot.oauth.domain.entity;

import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author bojiangzhou 2019/08/07
 */
@Table(name = "oauth_client")
public class BaseClient implements Serializable {
    private static final long serialVersionUID = -7910344880213292366L;

    @Id
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
    private String timeZone;
    private Integer apiEncryptFlag;
    private Integer apiReplayFlag;

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

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getAccessRoles() {
        return accessRoles;
    }

    public BaseClient setAccessRoles(String accessRoles) {
        this.accessRoles = accessRoles;
        return this;
    }

    public Integer getPwdReplayFlag() {
        return pwdReplayFlag;
    }

    public BaseClient setPwdReplayFlag(Integer pwdReplayFlag) {
        this.pwdReplayFlag = pwdReplayFlag;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public BaseClient setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Integer getApiEncryptFlag() {
        return apiEncryptFlag;
    }

    public BaseClient setApiEncryptFlag(Integer apiEncryptFlag) {
        this.apiEncryptFlag = apiEncryptFlag;
        return this;
    }

    public Integer getApiReplayFlag() {
        return apiReplayFlag;
    }

    public BaseClient setApiReplayFlag(Integer apiReplayFlag) {
        this.apiReplayFlag = apiReplayFlag;
        return this;
    }
}
