package org.hzero.platform.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 登录日志审计
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
@ApiModel("登录日志审计")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_audit_login")
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @Encrypt
    private Long auditId;
    @ApiModelProperty(value = "审计类型，HPFM.LOGIN_AUDIT_TYPE")
    @NotBlank
    @LovValue(lovCode = "HPFM.LOGIN_AUDIT_TYPE", meaningField = "auditTypeMeaning")
    private String auditType;
    @ApiModelProperty(value = "用户ID，IAM_USER.USER_ID")
    @NotNull
    @Encrypt
    private Long userId;
    @ApiModelProperty(value = "账号，IAM_USER.LOGIN_NAME")
    @NotBlank
    private String loginName;
    @ApiModelProperty(value = "手机号，IAM_USER.PHONE")
    private String phone;
    @ApiModelProperty(value = "邮箱，IAM_USER.EMAIL")
    private String email;
    @ApiModelProperty(value = "租户ID，IAM_USER.ORGANIZATION_ID")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "登录/登出时间")
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
    @ApiModelProperty(value = "登录是否成功，1-成功，0-失败")
    @NotNull
    private Integer loginStatus;
    @ApiModelProperty(value = "失败消息")
    private String loginMessage;
    @ApiModelProperty(value = "用户Token")
    private String accessToken;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    @ApiModelProperty(value = "登录时间从")
    private Date loginDateAfter;
    @Transient
    @ApiModelProperty(value = "登录时间至")
    private Date loginDateBefore;

    @Transient
    @ApiModelProperty(value = "登录名称，IAM_USER.REAL_NAME")
    private String userName;
    @Transient
    @ApiModelProperty(value = "登录名称，IAM_USER.REAL_NAME")
    private String loginDevice;
    @Transient
    @ApiModelProperty(value = "审计类型")
    private String auditTypeMeaning;
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * 审计类型
     */
    public String getAuditTypeMeaning() {
        return auditTypeMeaning;
    }

    public void setAuditTypeMeaning(String auditTypeMeaning) {
        this.auditTypeMeaning = auditTypeMeaning;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    /**
     * @return 审计类型，HPFM.LOGIN_AUDIT_TYPE
     */
    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    /**
     * @return 用户ID，IAM_USER.USER_ID
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 账号，IAM_USER.LOGIN_NAME
     */
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return 手机号，IAM_USER.PHONE
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return 邮箱，IAM_USER.EMAIL
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return 租户ID，IAM_USER.ORGANIZATION_ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 登录/登出时间
     */
    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    /**
     * @return 登录IP
     */
    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    /**
     * @return 登录客户端，OAUTH_CLIENT.NAME，非显示
     */
    public String getLoginClient() {
        return loginClient;
    }

    public void setLoginClient(String loginClient) {
        this.loginClient = loginClient;
    }

    /**
     * @return 登录平台，OAUTH_CLIENT.APPLICATION_NAME
     */
    public String getLoginPlatform() {
        return loginPlatform;
    }

    public void setLoginPlatform(String loginPlatform) {
        this.loginPlatform = loginPlatform;
    }

    /**
     * @return 登录操作系统信息
     */
    public String getLoginOs() {
        return loginOs;
    }

    public void setLoginOs(String loginOs) {
        this.loginOs = loginOs;
    }

    /**
     * @return 登录浏览器信息
     */
    public String getLoginBrowser() {
        return loginBrowser;
    }

    public void setLoginBrowser(String loginBrowser) {
        this.loginBrowser = loginBrowser;
    }

    /**
     * @return 登录是否成功，1-成功，0-失败
     */
    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    /**
     * @return 失败消息
     */
    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getLoginDateBefore() {
        return loginDateBefore;
    }

    public void setLoginDateBefore(Date loginDateBefore) {
        this.loginDateBefore = loginDateBefore;
    }

    public Date getLoginDateAfter() {
        return loginDateAfter;
    }

    public void setLoginDateAfter(Date loginDateAfter) {
        this.loginDateAfter = loginDateAfter;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }

    public String getTenantName() {
        return tenantName;
    }

    public AuditLogin setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }
}
