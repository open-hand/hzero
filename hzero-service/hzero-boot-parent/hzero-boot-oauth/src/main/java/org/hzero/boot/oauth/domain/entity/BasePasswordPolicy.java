package org.hzero.boot.oauth.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "oauth_password_policy")
public class BasePasswordPolicy implements Serializable {
    private static final long serialVersionUID = 2072175268806098308L;

    @Id
    @GeneratedValue
    private Long id;
    private String code;
    private String name;
    private Long organizationId;
    private String originalPassword;
    private Integer minLength;
    private Integer maxLength;
    private Integer maxErrorTime;
    private Integer digitsCount;
    private Integer lowercaseCount;
    private Integer uppercaseCount;
    private Integer specialCharCount;
    private Boolean notUsername;
    private String regularExpression;
    private Integer notRecentCount;
    private Boolean enablePassword;
    private Boolean enableSecurity;
    private Boolean enableLock;
    private Integer lockedExpireTime;
    private Boolean enableCaptcha;
    private Integer maxCheckCaptcha;
    private Boolean enableWebMultipleLogin;
    private Boolean enableAppMultipleLogin;
    private Integer passwordUpdateRate;
    private Integer passwordReminderPeriod;
    private Boolean loginAgain;
    private Boolean enableThreeRole;
    /**
     * 是否强制修改初始密码 true 是 false 否
     */
    private Boolean forceModifyPassword;
    /**
     * 强制验证码校验(密码相关操作)
     */
    private Boolean forceCodeVerify;

    // 角色模块配置
    private Boolean enableRoleInherit;
    private Boolean enableRoleAllocate;
    private Boolean enableRolePermission;

    public Boolean getForceModifyPassword() {
        return forceModifyPassword;
    }

    public void setForceModifyPassword(Boolean forceModifyPassword) {
        this.forceModifyPassword = forceModifyPassword;
    }

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

    public Boolean getLoginAgain() {
        return loginAgain;
    }

    public BasePasswordPolicy setLoginAgain(Boolean loginAgain) {
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

    public BasePasswordPolicy setEnableThreeRole(Boolean enableThreeRole) {
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
