package org.hzero.iam.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 密码策略
 *
 * @author bojiangzhou 2019/08/05
 */
public class PasswordPolicyVO implements Serializable {
    private static final long serialVersionUID = 294437235538711407L;

    @ApiModelProperty(value = "最小密码长度/非必填")
    private Integer minLength;
    @ApiModelProperty(value = "最大密码长度/非必填")
    private Integer maxLength;
    @ApiModelProperty(value = "最少数字数/非必填")
    private Integer digitsCount;
    @ApiModelProperty(value = "最少小写字母数/非必填")
    private Integer lowercaseCount;
    @ApiModelProperty(value = "最少大写字母数/非必填")
    private Integer uppercaseCount;
    @ApiModelProperty(value = "最少特殊字符数/非必填")
    private Integer specialCharCount;
    @ApiModelProperty(value = "是否允许与登录名相同/非必填")
    private Boolean notUsername;
    @ApiModelProperty("修改密码成功后自动退出")
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

    public Boolean getLoginAgain() {
        return loginAgain;
    }

    public PasswordPolicyVO setLoginAgain(Boolean loginAgain) {
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

    public PasswordPolicyVO setEnableThreeRole(Boolean enableThreeRole) {
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
