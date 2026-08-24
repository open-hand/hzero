package org.hzero.iam.domain.entity;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 实体：客户端
 *
 * @author bojiangzhou 2019/08/02
 */
@ApiModel("客户端")
@VersionAudit
@ModifyAudit
@Table(name = "oauth_client")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client extends AuditDomain {

    public static final String ENCRYPT_KEY = "client";

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_RESOURCE_IDS = "resourceIds";
    public static final String FIELD_SECRET = "secret";
    public static final String FIELD_SCOPE = "scope";
    public static final String FIELD_AUTHORIZED_GRANT_TYPES = "authorizedGrantTypes";
    public static final String FIELD_WEB_SERVER_REDIRECT_URI = "webServerRedirectUri";
    public static final String FIELD_ACCESS_TOKEN_VALIDITY = "accessTokenValidity";
    public static final String FIELD_REFRESH_TOKEN_VALIDITY = "refreshTokenValidity";
    public static final String FIELD_ADDITIONAL_INFORMATION = "additionalInformation";
    public static final String FIELD_AUTO_APPROVE = "autoApprove";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_ACCESS_ROLES = "accessRoles";
    public static final String FIELD_PWD_REPLAY_FLAG = "pwdReplayFlag";
    public static final String FIELD_TIME_ZONE = "timeZone";
    public static final String FIELD_API_ENCRYPT_FLAG = "apiEncryptFlag";
    public static final String FIELD_API_REPLAY_FLAG = "apiReplayFlag";

    public static final String AUTH_GRANT_TYPE_LOV = "HIAM.GRANT_TYPE";

    @Id
    @GeneratedValue
    @Where
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "客户端名称/必填")
    @Size(min = 1, max = 30)
    @NotNull
    @Pattern(regexp = Regexs.CODE)
    @Where
    private String name;
    @ApiModelProperty(value = "组织ID/必填")
    @Where
    private Long organizationId;
    @ApiModelProperty(value = "客户端资源/非必填/默认：default")
    private String resourceIds;
    @ApiModelProperty(value = "客户端秘钥/必填")
    @Size(min = 6, max = 240)
    @NotNull
    private String secret;
    @ApiModelProperty(value = "作用域/非必填")
    private String scope;
    @ApiModelProperty(value = "授权类型/必填")
    @NotNull
    private String authorizedGrantTypes;
    @ApiModelProperty(value = "重定向地址/非必填")
    private String webServerRedirectUri;
    @ApiModelProperty(value = "访问授权超时时间/必填")
    private Long accessTokenValidity;
    @ApiModelProperty(value = "授权超时时间/必填")
    private Long refreshTokenValidity;
    @ApiModelProperty(value = "附加信息/非必填")
    private String additionalInformation;
    @ApiModelProperty(value = "自动授权域/非必填")
    private String autoApprove;
    @ApiModelProperty(value = "启用标识")
    private Integer enabledFlag;
    @ApiModelProperty(value = "客户端可访问角色")
    private String accessRoles;
    @ApiModelProperty(value = "密码防重放标识")
    @NotNull
    private Integer pwdReplayFlag;
    @ApiModelProperty(value = "时区，默认 GMT+8")
    @LovValue(lovCode = "HIAM.TIME_ZONE")
    private String timeZone;
    @ApiModelProperty(value = "接口加密标识")
    private Integer apiEncryptFlag;
    @ApiModelProperty(value = "api防重放标识")
    private Integer apiReplayFlag;


    @Transient
    private String tenantNum;
    @Transient
    private String tenantName;
    @Transient
    @LovValue("HIAM.RESOURCE_LEVEL")
    private String assignLevel;
    @Transient
    private String assignLevelMeaning;
    @Transient
    private Long assignLevelValue;
    @Transient
    private String assignLevelValueMeaning;
    @Transient
    @Encrypt
    private Long memberRoleId;
    @Transient
    @ApiModelProperty("客户端角色列表")
    private List<MemberRole> memberRoleList;
    @Transient
    private String timeZoneMeaning;
    @Transient
    @ApiModelProperty("授权类型meanings")
    private String authGrantTypeMeanings;
    @Transient
    private LocalDate startDateActive;
    @Transient
    private LocalDate endDateActive;


    public String getAuthGrantTypeMeanings() {
        return authGrantTypeMeanings;
    }

    public void setAuthGrantTypeMeanings(String authGrantTypeMeanings) {
        this.authGrantTypeMeanings = authGrantTypeMeanings;
    }

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

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getAssignLevel() {
        return assignLevel;
    }

    public void setAssignLevel(String assignLevel) {
        this.assignLevel = assignLevel;
    }

    public String getAssignLevelMeaning() {
        return assignLevelMeaning;
    }

    public void setAssignLevelMeaning(String assignLevelMeaning) {
        this.assignLevelMeaning = assignLevelMeaning;
    }

    public Long getAssignLevelValue() {
        return assignLevelValue;
    }

    public void setAssignLevelValue(Long assignLevelValue) {
        this.assignLevelValue = assignLevelValue;
    }

    public String getAssignLevelValueMeaning() {
        return assignLevelValueMeaning;
    }

    public void setAssignLevelValueMeaning(String assignLevelValueMeaning) {
        this.assignLevelValueMeaning = assignLevelValueMeaning;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }

    public List<MemberRole> getMemberRoleList() {
        return memberRoleList;
    }

    public Client setMemberRoleList(List<MemberRole> memberRoleList) {
        this.memberRoleList = memberRoleList;
        return this;
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

    public void setPwdReplayFlag(Integer pwdReplayFlag) {
        this.pwdReplayFlag = pwdReplayFlag;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


    public String getTimeZoneMeaning() {
        return timeZoneMeaning;
    }

    public Client setTimeZoneMeaning(String timeZoneMeaning) {
        this.timeZoneMeaning = timeZoneMeaning;
        return this;
    }

    public Integer getApiEncryptFlag() {
        return apiEncryptFlag;
    }

    public Client setApiEncryptFlag(Integer apiEncryptFlag) {
        this.apiEncryptFlag = apiEncryptFlag;
        return this;
    }

    public Integer getApiReplayFlag() {
        return apiReplayFlag;
    }

    public Client setApiReplayFlag(Integer apiReplayFlag) {
        this.apiReplayFlag = apiReplayFlag;
        return this;
    }

    public LocalDate getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
    }

    public LocalDate getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
    }
}
