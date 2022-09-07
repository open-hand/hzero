package org.hzero.iam.domain.entity;


import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

/**
 * @author wuguokai
 */
public class PasswordPolicyDTO {
    @ApiModelProperty(value = "主键ID/非必填")
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

    @ApiModelProperty(value = "锁定时长/非必填")
    private Integer lockedExpireTime;

    @ApiModelProperty(value = "是否开启验证码/非必填")
    private Boolean enableCaptcha;

    @ApiModelProperty(value = "输错多少次后开启验证码/非必填")
    private Integer maxCheckCaptcha;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
