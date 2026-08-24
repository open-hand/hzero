package org.hzero.boot.oauth.domain.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "iam_user")
public class BaseUser {

    public static final String FIELD_ID = "id";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_REAL_NAME = "realName";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_INTERNATIONAL_TEL_CODE = "internationalTelCode";
    public static final String FIELD_LAST_PASSWORD_UPDATED_AT = "lastPasswordUpdatedAt";
    public static final String FIELD_LAST_LOGIN_AT = "lastLoginAt";
    public static final String FIELD_ENABLED = "isEnabled";
    public static final String FIELD_LOCKED = "isLocked";
    public static final String FIELD_LDAP = "isLdap";
    public static final String FIELD_LOCKED_UNTIL_AT = "lockedUntilAt";
    public static final String FIELD_PASSWORD_ATTEMPT = "passwordAttempt";

    public BaseUser() {}

    /**
     * 密码策略校验时需要用到如下三个参数化
     * 
     * @param userId 用户ID，可为空
     * @param loginName 登录账号，可为空
     * @param tenantId 租户ID，不可为空
     */
    public BaseUser(Long userId, String loginName, Long tenantId) {
        this.id = userId;
        this.loginName = loginName;
        this.organizationId = tenantId;
    }

    public BaseUser(Long userId, String loginName, Long tenantId, Boolean isLocked) {
        this.id = userId;
        this.loginName = loginName;
        this.organizationId = tenantId;
        this.isLocked = isLocked;
    }

    /**
     *
     * @param userId 用户ID，可为空
     * @param tenantId 租户ID，不可为空
     */
    public BaseUser(Long userId, Long tenantId) {
        this.id = userId;
        this.organizationId = tenantId;
    }

    @Id
    @GeneratedValue
    private Long id;
    private String loginName;
    private String email;
    private Long organizationId;
    @Column(name = "hash_password")
    private String password;
    private String realName;
    private String phone;
    private String imageUrl;
    private String profilePhoto;
    private Boolean isEnabled;
    private Boolean isLdap;
    private String language;
    private String timeZone;
    private Date lastPasswordUpdatedAt;
    private Date lastLoginAt;
    private Boolean isLocked; // 连续登录错误次数超出规定次数后是否锁定账户
    private Date lockedUntilAt;
    private Integer passwordAttempt;
    private String userType;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getLdap() {
        return isLdap;
    }

    public void setLdap(Boolean ldap) {
        isLdap = ldap;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Date getLastPasswordUpdatedAt() {
        return lastPasswordUpdatedAt;
    }

    public void setLastPasswordUpdatedAt(Date lastPasswordUpdatedAt) {
        this.lastPasswordUpdatedAt = lastPasswordUpdatedAt;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public Date getLockedUntilAt() {
        return lockedUntilAt;
    }

    public void setLockedUntilAt(Date lockedUntilAt) {
        this.lockedUntilAt = lockedUntilAt;
    }

    public Integer getPasswordAttempt() {
        return passwordAttempt;
    }

    public void setPasswordAttempt(Integer passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
