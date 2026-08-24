package org.hzero.iam.api.dto;

import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
public class LdapAccountDTO {

    @ApiModelProperty(value = "LDAP测试账号/必填")
    @NotEmpty(message = "error.ldap.account.empty")
    private String account;
    @ApiModelProperty(value = "LDAP测试账号密码/必填")
    @NotEmpty(message = "error.ldap.password.empty")
    private String ldapPassword;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getLdapPassword() {
        return ldapPassword;
    }

    public void setLdapPassword(String ldapPassword) {
        this.ldapPassword = ldapPassword;
    }
}
