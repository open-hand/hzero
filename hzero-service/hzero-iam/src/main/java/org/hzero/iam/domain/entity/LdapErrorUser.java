package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel("LdapErrorUser")
@VersionAudit
@ModifyAudit
@Table(name = "oauth_ldap_error_user")
public class LdapErrorUser extends AuditDomain {

    public static final String ENCRYPT_KEY = "oauth_ldap_error_user";

    public static final String FIELD_ID = "id";
    public static final String FIELD_LDAP_HISTORY_ID = "ldapHistoryId";
    public static final String FIELD_UUID = "uuid";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_REAL_NAME = "realName";
    public static final String FIELD_PHONE= "phone";
    public static final String FIELD_CAUSE= "cause";

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "主键")
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "外键，ldap history id")
    @Where
    @Encrypt
    private Long ldapHistoryId;
    @ApiModelProperty(value = "ldap server 端对象的唯一标识，可以根据该字段在ldap server中查询该对象")
    @Where
    private String uuid;
    @ApiModelProperty(value = "登录名")
    @Where
    private String loginName;
    @ApiModelProperty(value = "邮箱")
    @Where
    private String email;
    @ApiModelProperty(value = "真实姓名")
    @Where
    private String realName;
    @ApiModelProperty(value = "手机号")
    @Where
    private String phone;
    @ApiModelProperty(value = "同步失败原因")
    private String cause;
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public LdapErrorUser setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public LdapErrorUser setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getLdapHistoryId() {
        return ldapHistoryId;
    }

    public LdapErrorUser setLdapHistoryId(Long ldapHistoryId) {
        this.ldapHistoryId = ldapHistoryId;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public LdapErrorUser setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public LdapErrorUser setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public LdapErrorUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public LdapErrorUser setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public LdapErrorUser setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCause() {
        return cause;
    }

    public LdapErrorUser setCause(String cause) {
        this.cause = cause;
        return this;
    }
}
