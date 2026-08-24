package org.hzero.boot.oauth.domain.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author bojiangzhou 2019/08/07
 */
@Table(name = "oauth_ldap")
public class BaseLdap implements Serializable {
    private static final long serialVersionUID = 6535627207225352894L;

    public static final String FIELD_ENABLED = "enabled";
    public static final String GET_LOGIN_NAME_FIELD = "getLoginNameField";
    public static final String GET_REAL_NAME_FIELD = "getRealNameField";
    public static final String GET_EMAIL_FIELD = "getEmailField";
    public static final String GET_PHONE_FIELD = "getPhoneField";
    public static final String GET_UUID_FIELD = "getUuidField";

    @Id
    private Long id;
    private String name;
    private Long organizationId;
    private String serverAddress;
    private String port;
    private String account;
    private String ldapPassword;
    @Column(name = "use_ssl")
    private Boolean useSSL;
    @Column(name = "is_enabled")
    private Boolean enabled;
    private String baseDn;
    private String directoryType;
    private String objectClass;
    private String loginNameField;
    private String realNameField;
    private String emailField;
    private String phoneField;
    private String customFilter;
    private Integer sagaBatchSize;
    private Integer connectionTimeout;
    private String uuidField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

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

    public Boolean getUseSSL() {
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public String getDirectoryType() {
        return directoryType;
    }

    public void setDirectoryType(String directoryType) {
        this.directoryType = directoryType;
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

    public String getEmailField() {
        return emailField;
    }

    public void setEmailField(String emailField) {
        this.emailField = emailField;
    }

    public String getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(String phoneField) {
        this.phoneField = phoneField;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getCustomFilter() {
        return customFilter;
    }

    public void setCustomFilter(String customFilter) {
        this.customFilter = customFilter;
    }

    public Integer getSagaBatchSize() {
        return sagaBatchSize;
    }

    public void setSagaBatchSize(Integer sagaBatchSize) {
        this.sagaBatchSize = sagaBatchSize;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getUuidField() {
        return uuidField;
    }

    public void setUuidField(String uuidField) {
        this.uuidField = uuidField;
    }
}
