package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 用户二次校验数据查询DTO
 *
 * @author bergturing 2020/08/25 14:07
 */
public class UserSecCheckSearchDTO {
    @ApiModelProperty(value = "租户ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户名")
    private String loginName;

    @ApiModelProperty(value = "用户真实名")
    private String realName;

    @ApiModelProperty(value = "二次校验验证码是否发送给手机")
    private Boolean secCheckPhoneFlag;

    @ApiModelProperty(value = "二次校验验证码是否发送给邮箱")
    private Boolean secCheckEmailFlag;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
        return "UserSecCheckSearchDTO{" +
                "loginName='" + loginName + '\'' +
                ", realName='" + realName + '\'' +
                ", secCheckPhoneFlag=" + secCheckPhoneFlag +
                ", secCheckEmailFlag=" + secCheckEmailFlag +
                '}';
    }
}