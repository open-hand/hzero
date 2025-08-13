package org.hzero.iam.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 手机验证码校验参数值对象
 *
 * @author bergturing 2020/08/19 14:48
 */
public class MobileCaptchaVerifyVO {
    @ApiModelProperty(value = "验证码Key")
    @NotBlank
    private String captchaKey;
    @ApiModelProperty(value = "用户输入的验证码")
    @NotBlank
    private String captcha;
    @ApiModelProperty(value = "用户手机号")
    @NotBlank
    private String phone;
    @ApiModelProperty(value = "用户类型")
    private String userType;
    @ApiModelProperty(value = "验证码业务范围")
    private String businessScope;

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    @Override
    public String toString() {
        return "MobileCaptchaVerifyVO{" +
                "captchaKey='" + captchaKey + '\'' +
                ", captcha='" + captcha + '\'' +
                ", phone='" + phone + '\'' +
                ", userType='" + userType + '\'' +
                ", businessScope='" + businessScope + '\'' +
                '}';
    }
}
