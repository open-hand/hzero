package org.hzero.iam.api.dto;


import io.swagger.annotations.ApiModelProperty;

import org.hzero.iam.domain.entity.Ldap;

/**
 * @author superlee
 */
public class LdapConnectionDTO {

    @ApiModelProperty(value = "基础连接是否成功")
    private Boolean canConnectServer;
    @ApiModelProperty(value = "LDAP登录是否成功")
    private Boolean canLogin;
    @ApiModelProperty(value = "用户属性校验是否成功")
    private Boolean matchAttribute;
    @ApiModelProperty(value = "登录名属性是否成功")
    private String loginNameField;
    @ApiModelProperty(value = "用户名属性是否成功")
    private String realNameField;
    @ApiModelProperty(value = "手机号属性是否成功")
    private String phoneField;
    @ApiModelProperty(value = "邮箱属性是否成功")
    private String emailField;
    @ApiModelProperty(value = "uuid属性校验是否成功")
    private String uuidField;

    public String getUuidField() {
        return uuidField;
    }

    public void setUuidField(String uuidField) {
        this.uuidField = uuidField;
    }

    public Boolean getCanConnectServer() {
        return canConnectServer;
    }

    public void setCanConnectServer(Boolean canConnectServer) {
        this.canConnectServer = canConnectServer;
    }

    public Boolean getCanLogin() {
        return canLogin;
    }

    public void setCanLogin(Boolean canLogin) {
        this.canLogin = canLogin;
    }

    public Boolean getMatchAttribute() {
        return matchAttribute;
    }

    public void setMatchAttribute(Boolean matchAttribute) {
        this.matchAttribute = matchAttribute;
    }

    public String getLoginNameField() {
        return loginNameField;
    }

    public void setLoginNameField(String loginNameField) {
        this.loginNameField = loginNameField;
    }

    public String getRealNameField() {
        return realNameField;
    }

    public void setRealNameField(String realNameField) {
        this.realNameField = realNameField;
    }

    public String getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(String phoneField) {
        this.phoneField = phoneField;
    }

    public String getEmailField() {
        return emailField;
    }

    public void setEmailField(String emailField) {
        this.emailField = emailField;
    }

    public void fullFields(String key, String value) {
        if (Ldap.GET_LOGIN_NAME_FIELD.equals(key)) {
            this.setLoginNameField(value);
        }
        if (Ldap.GET_REAL_NAME_FIELD.equals(key)) {
            this.setRealNameField(value);
        }
        if (Ldap.GET_EMAIL_FIELD.equals(key)) {
            this.setEmailField(value);
        }
        if (Ldap.GET_PHONE_FIELD.equals(key)) {
            this.setPhoneField(value);
        }
        if (Ldap.GET_UUID_FIELD.equals(key)) {
            this.setUuidField(value);
        }
    }
}
