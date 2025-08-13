package org.hzero.iam.domain.entity;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 密码策略
 *
 * @author bojiangzhou 2019/08/05
 */
@ApiModel("密码策略")
@ModifyAudit
@VersionAudit
@Table(name = "oauth_password_policy")
public class PasswordPolicy extends AuditDomain {

    public static final String ENCRYPT_KEY = "oauth_password_policy";

    public static final String FIELD_NAME = "name";

    public void validate() {
        int allLeastRequiredLength = this.getDigitsCount() +
                this.getSpecialCharCount() +
                this.getLowercaseCount() +
                this.getUppercaseCount();
        if (allLeastRequiredLength > this.getMaxLength()) {
            throw new CommonException("hiam.warn.pwdPolicy.allLeastRequiredLength.greaterThan.maxLength");
        }

        if (this.getMinLength() > this.getMaxLength()) {
            throw new CommonException("hiam.warn.pwdPolicy.maxLength.lessThan.minLength");
        }

        this.passwordUpdateRate = passwordUpdateRate == null || passwordUpdateRate < 0 ? Integer.valueOf(0) : passwordUpdateRate;
        this.passwordReminderPeriod = passwordReminderPeriod == null || passwordReminderPeriod < 0 ? Integer.valueOf(0) : passwordReminderPeriod;
        // 校验提醒日期是否小于修改频率
        if (passwordReminderPeriod > 0 && passwordUpdateRate < passwordReminderPeriod) {
            throw new CommonException("hiam.warn.pwdPolicy.reminderGreaterUpdate");
        }
    }



    @Id
    @GeneratedValue
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "密码策略编码/必填")
    @NotNull(message = "error.passwordPolicy.code.null")
    private String code;

    @ApiModelProperty(value = "密码策略名称/必填")
    @NotNull(message = "error.passwordPolicy.name.null")
    private String name;

    @ApiModelProperty(value = "组织ID/必填")
    @NotNull(message = "error.passwordPolicy.organizationId.null")
    private Long organizationId;

    @ApiModelProperty(value = "新用户默认密码/非必填")
    private String originalPassword;

    @ApiModelProperty(value = "最小密码长度/非必填")
    @Range(min = 0, message = "error.minLength")
    private Integer minLength;

    @ApiModelProperty(value = "最大密码长度/非必填")
    @Range(min = 0, message = "error.maxLength")
    private Integer maxLength;

    @ApiModelProperty(value = "输错多少次后开启锁定/非必填")
    private Integer maxErrorTime;

    @ApiModelProperty(value = "最少数字数/非必填")
    @Range(min = 0, message = "error.digitsCount")
    private Integer digitsCount;

    @ApiModelProperty(value = "最少小写字母数/非必填")
    @Range(min = 0, message = "error.lowercaseCount")
    private Integer lowercaseCount;

    @ApiModelProperty(value = "最少大写字母数/非必填")
    @Range(min = 0, message = "error.uppercaseCount")
    private Integer uppercaseCount;

    @ApiModelProperty(value = "最少特殊字符数/非必填")
    @Range(min = 0, message = "error.specialCharCount")
    private Integer specialCharCount;

    @ApiModelProperty(value = "是否允许与登录名相同/非必填")
    private Boolean notUsername;

    @ApiModelProperty(value = "密码正则/非必填")
    private String regularExpression;

    @ApiModelProperty(value = "最大近期密码数/非必填")
    @Range(min = 0, message = "error.notRecentCount")
    private Integer notRecentCount;

    @ApiModelProperty(value = "是否开启密码安全策略/非必填")
    private Boolean enablePassword;

    @ApiModelProperty(value = "是否开启登录安全策略/非必填")
    private Boolean enableSecurity;

    @ApiModelProperty(value = "是否开启锁定/非必填")
    private Boolean enableLock;

    @ApiModelProperty(value = "锁定时长(秒)/非必填")
    private Integer lockedExpireTime;

    @ApiModelProperty(value = "是否开启验证码/非必填")
    private Boolean enableCaptcha;

    @ApiModelProperty(value = "输错多少次后开启验证码/非必填")
    private Integer maxCheckCaptcha;
    @ApiModelProperty(value = "Web端允许多处登录")
    private Boolean enableWebMultipleLogin;
    @ApiModelProperty(value = "移动端允许多处登录")
    private Boolean enableAppMultipleLogin;
    @ApiModelProperty(value = "密码更新频率，单位：天")
    private Integer passwordUpdateRate;
    @ApiModelProperty(value = "密码更新提醒期，单位：天")
    private Integer passwordReminderPeriod;
    @ApiModelProperty(value = "是否强制修改初始密码")
    private Boolean forceModifyPassword;
    @ApiModelProperty(value = "修改密码后需要重新登入")
    private Boolean loginAgain;
    @ApiModelProperty("强制手机验证码校验")
    private Boolean forceCodeVerify;
    @ApiModelProperty(value = "启用三员管理")
    private Boolean enableThreeRole;

    // 角色模块配置
    @ApiModelProperty(value = "是否开启角色继承功能")
    private Boolean enableRoleInherit;
    @ApiModelProperty(value = "分配给自己的角色是否允许分配给其它用户")
    private Boolean enableRoleAllocate;
    @ApiModelProperty(value = "子角色是否能操作数据权限等功能")
    private Boolean enableRolePermission;

    public Integer getPasswordReminderPeriod() {
        return passwordReminderPeriod;
    }

    public void setPasswordReminderPeriod(Integer passwordReminderPeriod) {
        this.passwordReminderPeriod = passwordReminderPeriod;
    }

    public Boolean getEnableWebMultipleLogin() {
        return enableWebMultipleLogin;
    }

    public void setEnableWebMultipleLogin(Boolean enableWebMultipleLogin) {
        this.enableWebMultipleLogin = enableWebMultipleLogin;
    }

    public Boolean getEnableAppMultipleLogin() {
        return enableAppMultipleLogin;
    }

    public void setEnableAppMultipleLogin(Boolean enableAppMultipleLogin) {
        this.enableAppMultipleLogin = enableAppMultipleLogin;
    }

    public Integer getPasswordUpdateRate() {
        return passwordUpdateRate;
    }

    public void setPasswordUpdateRate(Integer passwordUpdateRate) {
        this.passwordUpdateRate = passwordUpdateRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMaxErrorTime() {
        return maxErrorTime;
    }

    public void setMaxErrorTime(Integer maxErrorTime) {
        this.maxErrorTime = maxErrorTime;
    }

    public Integer getDigitsCount() {
        return digitsCount;
    }

    public void setDigitsCount(Integer digitsCount) {
        this.digitsCount = digitsCount;
    }

    public Integer getLowercaseCount() {
        return lowercaseCount;
    }

    public void setLowercaseCount(Integer lowercaseCount) {
        this.lowercaseCount = lowercaseCount;
    }

    public Integer getUppercaseCount() {
        return uppercaseCount;
    }

    public void setUppercaseCount(Integer uppercaseCount) {
        this.uppercaseCount = uppercaseCount;
    }

    public Integer getSpecialCharCount() {
        return specialCharCount;
    }

    public void setSpecialCharCount(Integer specialCharCount) {
        this.specialCharCount = specialCharCount;
    }

    public Boolean getNotUsername() {
        return notUsername;
    }

    public void setNotUsername(Boolean notUsername) {
        this.notUsername = notUsername;
    }

    public String getRegularExpression() {
        return regularExpression;
    }

    public void setRegularExpression(String regularExpression) {
        this.regularExpression = regularExpression;
    }

    public Integer getNotRecentCount() {
        return notRecentCount;
    }

    public void setNotRecentCount(Integer notRecentCount) {
        this.notRecentCount = notRecentCount;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }

    public Boolean getEnablePassword() {
        return enablePassword;
    }

    public void setEnablePassword(Boolean enablePassword) {
        this.enablePassword = enablePassword;
    }

    public Boolean getEnableSecurity() {
        return enableSecurity;
    }

    public void setEnableSecurity(Boolean enableSecurity) {
        this.enableSecurity = enableSecurity;
    }

    public Boolean getEnableLock() {
        return enableLock;
    }

    public void setEnableLock(Boolean enableLock) {
        this.enableLock = enableLock;
    }

    public Integer getLockedExpireTime() {
        return lockedExpireTime;
    }

    public void setLockedExpireTime(Integer lockedExpireTime) {
        this.lockedExpireTime = lockedExpireTime;
    }

    public Boolean getEnableCaptcha() {
        return enableCaptcha;
    }

    public void setEnableCaptcha(Boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public Integer getMaxCheckCaptcha() {
        return maxCheckCaptcha;
    }

    public void setMaxCheckCaptcha(Integer maxCheckCaptcha) {
        this.maxCheckCaptcha = maxCheckCaptcha;
    }

    public Boolean getForceModifyPassword() {
        return forceModifyPassword;
    }

    public void setForceModifyPassword(Boolean forceModifyPassword) {
        this.forceModifyPassword = forceModifyPassword;
    }

    public Boolean getLoginAgain() {
        return loginAgain;
    }

    public PasswordPolicy setLoginAgain(Boolean loginAgain) {
        this.loginAgain = loginAgain;
        return this;
    }

    public Boolean getForceCodeVerify() {
        return forceCodeVerify;
    }

    public void setForceCodeVerify(Boolean forceCodeVerify) {
        this.forceCodeVerify = forceCodeVerify;
    }

    public Boolean getEnableThreeRole() {
        return enableThreeRole;
    }

    public PasswordPolicy setEnableThreeRole(Boolean enableThreeRole) {
        this.enableThreeRole = enableThreeRole;
        return this;
    }

    public Boolean getEnableRoleInherit() {
        return enableRoleInherit;
    }

    public void setEnableRoleInherit(Boolean enableRoleInherit) {
        this.enableRoleInherit = enableRoleInherit;
    }

    public Boolean getEnableRoleAllocate() {
        return enableRoleAllocate;
    }

    public void setEnableRoleAllocate(Boolean enableRoleAllocate) {
        this.enableRoleAllocate = enableRoleAllocate;
    }

    public Boolean getEnableRolePermission() {
        return enableRolePermission;
    }

    public void setEnableRolePermission(Boolean enableRolePermission) {
        this.enableRolePermission = enableRolePermission;
    }
}
