package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 用户二次校验数据传输对象
 *
 * @author bergturing 2020/08/20 17:53
 */
public class UserSecCheckDTO {
    @ApiModelProperty("用户ID")
    @Encrypt
    private Long id;

    @ApiModelProperty("用户名")
    private String loginName;

    @ApiModelProperty("用户真实姓名")
    private String realName;

    @ApiModelProperty("二次校验验证码是否发送给手机")
    private Boolean secCheckPhoneFlag;

    @ApiModelProperty("二次校验验证码是否发送给邮箱")
    private Boolean secCheckEmailFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getSecCheckPhoneFlag() {
        return secCheckPhoneFlag;
    }

    public void setSecCheckPhoneFlag(Boolean secCheckPhoneFlag) {
        this.secCheckPhoneFlag = secCheckPhoneFlag;
    }

    public Boolean getSecCheckEmailFlag() {
        return secCheckEmailFlag;
    }

    public void setSecCheckEmailFlag(Boolean secCheckEmailFlag) {
        this.secCheckEmailFlag = secCheckEmailFlag;
    }

    @Override
    public String toString() {
        return "UserSecCheckDTO{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", realName='" + realName + '\'' +
                ", secCheckPhoneFlag=" + secCheckPhoneFlag +
                ", secCheckEmailFlag=" + secCheckEmailFlag +
                '}';
    }
}
