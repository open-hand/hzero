package org.hzero.oauth.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 登录日志审计
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
@ApiModel("登录日志审计")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_audit_login")
public class AuditLogin extends AuditDomain {

    public static final String FIELD_AUDIT_ID = "auditId";
    public static final String FIELD_AUDIT_TYPE = "auditType";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TENANT_NAME = "tenantName";
    public static final String FIELD_LOGIN_DATE = "loginDate";
    public static final String FIELD_LOGIN_IP = "loginIp";
    public static final String FIELD_LOGIN_CLIENT = "loginClient";
    public static final String FIELD_LOGIN_PLATFORM = "loginPlatform";
    public static final String FIELD_LOGIN_OS = "loginOs";
    public static final String FIELD_LOGIN_BROWSER = "loginBrowser";
    public static final String FIELD_LOGIN_STATUS = "loginStatus";
    public static final String FIELD_LOGIN_MESSAGE = "loginMessage";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long auditId;
    @ApiModelProperty(value = "审计类型，HPFM.LOGIN_AUDIT_TYPE", required = true)
    @NotBlank
    private String auditType;
    @ApiModelProperty(value = "用户ID，IAM_USER.USER_ID", required = true)
    @NotNull
    private Long userId;
    @ApiModelProperty(value = "账号，IAM_USER.LOGIN_NAME", required = true)
    @NotBlank
    private String loginName;
    @ApiModelProperty(value = "手机号，IAM_USER.SMS")
    private String phone;
    @ApiModelProperty(value = "邮箱，IAM_USER.EMAIL")
    private String email;
    @ApiModelProperty(value = "租户ID，IAM_USER.ORGANIZATION_ID", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "登录/登出时间", required = true)
    @NotNull
    private Date loginDate;
    @ApiModelProperty(value = "登录IP")
    private String loginIp;
    @ApiModelProperty(value = "登录客户端，OAUTH_CLIENT.NAME，非显示")
    private String loginClient;
    @ApiModelProperty(value = "登录平台，OAUTH_CLIENT.APPLICATION_NAME")
    private String loginPlatform;
    @ApiModelProperty(value = "登录操作系统信息")
    private String loginOs;
    @ApiModelProperty(value = "登录浏览器信息")
    private String loginBrowser;
    @ApiModelProperty(value = "登录是否成功，1-成功，0-失败", required = true)
    @NotNull
    private Integer loginStatus;
    @ApiModelProperty(value = "失败消息")
    private String loginMessage;
    @ApiModelProperty(value = "用户token")
    private String accessToken;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getAuditId() {
        return auditId;
    }

    public AuditLogin setAuditId(Long auditId) {
        this.auditId = auditId;
        return this;
    }

    public String getAuditType() {
        return auditType;
    }

    public AuditLogin setAuditType(String auditType) {
        this.auditType = auditType;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AuditLogin setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public AuditLogin setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public AuditLogin setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AuditLogin setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public AuditLogin setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public AuditLogin setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
        return this;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public AuditLogin setLoginIp(String loginIp) {
        this.loginIp = loginIp;
        return this;
    }

    public String getLoginClient() {
        return loginClient;
    }

    public AuditLogin setLoginClient(String loginClient) {
        this.loginClient = loginClient;
        return this;
    }

    public String getLoginPlatform() {
        return loginPlatform;
    }

    public AuditLogin setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
        return this;
    }

    public String getLoginOs() {
        return loginOs;
    }

    public AuditLogin setLoginOs(String loginOs) {
        this.loginOs = loginOs;
        return this;
    }

    public String getLoginBrowser() {
        return loginBrowser;
    }

    public AuditLogin setLoginBrowser(String loginBrowser) {
        this.loginBrowser = loginBrowser;
        return this;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public AuditLogin setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
        return this;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public AuditLogin setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public AuditLogin setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
