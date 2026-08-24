package org.hzero.oauth.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.oauth.domain.vo.Role;
import org.hzero.oauth.security.constant.LoginField;

@ModifyAudit
@VersionAudit
@Table(name = "iam_user")
public class User extends AuditDomain implements Serializable {
    private static final long serialVersionUID = 1280333995775842225L;

    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_LOCKED = "locked";
    public static final String FIELD_LOCKED_DATE = "locked_date";
    public static final String FIELD_LOCKED_UNTIL_AT = "lockedUntilAt";
    public static final String FIELD_LAST_PASSWORD_UPDATED_AT = "lastPasswordUpdatedAt";
    public static final String FIELD_ENABLE_SECONDARY_CHECK = "enableSecondaryCheck";
    public static final String FIELD_PHONE = "phone";

    /**
     * 线程安全的，放心用
     */
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码的同时，检测密码安全度
     */
    public void encodePassword() {
        this.password = ENCODER.encode(this.getPassword());
    }

    public void unlocked() {
        this.locked = false;
        this.lockedDate = null;
        this.lockedUntilAt = null;
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

    private String internationalTelCode;

    private String imageUrl;

    private String profilePhoto;

    private String language;

    private String timeZone;

    private Date lastPasswordUpdatedAt;

    private Date lastLoginAt;

    @Column(name = "is_enabled")
    private Boolean enabled;

    @Column(name = "is_locked")
    private Boolean locked;

    @Column(name = "is_ldap")
    private Boolean ldap;

    private Date lockedUntilAt;

    private Integer passwordAttempt;

    @Column(name = "is_admin")
    private Boolean admin;

    private String userType;

    // ===============================================================================
    // 以下字段为UserInfo的字段，User与UserInfo是一对一关系
    // ===============================================================================

    @Transient
    private Date lockedDate;
    @Transient
    private Long employeeId;
    @Transient
    private LocalDate startDateActive;
    @Transient
    private LocalDate endDateActive;
    @Transient
    private Integer phoneCheckFlag;
    @Transient
    private Integer emailCheckFlag;
    @Transient
    private Long defaultCompanyId;
    @Transient
    private LocalDate birthday;
    @Transient
    private String nickname;
    @Transient
    private Integer gender;
    @Transient
    private Long countryId;
    @Transient
    private Long regionId;
    @Transient
    private Integer passwordResetFlag;
    @Transient
    private Boolean secCheckPhoneFlag;
    @Transient
    private Boolean secCheckEmailFlag;

    @Transient
    @JsonIgnore
    private transient List<Role> roles;

    //
    // 额外查询字段
    // ------------------------------------------------------------------------------

    @Transient
    @JsonIgnore
    private Long tenantId;
    @Transient
    private String tenantName;
    @Transient
    @JsonIgnore
    private Integer tenantEnabledFlag;
    /**
     * 使用哪个字段登录的
     */
    @Transient
    @JsonIgnore
    private transient LoginField loginField;


    public User() {

    }

    public User(Long id) {
        this.id = id;
    }

    public User(Long id, Long organizationId) {
        this.id = id;
        this.organizationId = organizationId;
    }

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

    public String getInternationalTelCode() {
        return internationalTelCode;
    }

    public void setInternationalTelCode(String internationalTelCode) {
        this.internationalTelCode = internationalTelCode;
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

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Boolean getLdap() {
        return ldap;
    }

    public void setLdap(Boolean ldap) {
        this.ldap = ldap;
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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
    }

    public LocalDate getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
    }

    public Integer getPhoneCheckFlag() {
        return phoneCheckFlag;
    }

    public void setPhoneCheckFlag(Integer phoneCheckFlag) {
        this.phoneCheckFlag = phoneCheckFlag;
    }

    public Integer getEmailCheckFlag() {
        return emailCheckFlag;
    }

    public void setEmailCheckFlag(Integer emailCheckFlag) {
        this.emailCheckFlag = emailCheckFlag;
    }

    public Long getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(Long defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Integer getTenantEnabledFlag() {
        return tenantEnabledFlag;
    }

    public void setTenantEnabledFlag(Integer tenantEnabledFlag) {
        this.tenantEnabledFlag = tenantEnabledFlag;
    }

    public LoginField getLoginField() {
        return loginField;
    }

    public void setLoginField(LoginField loginField) {
        this.loginField = loginField;
    }

    public Integer getPasswordResetFlag() {
        return passwordResetFlag;
    }

    public void setPasswordResetFlag(Integer passwordResetFlag) {
        this.passwordResetFlag = passwordResetFlag;
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    /**
     * 判断当前用户是否开启了二次校验
     *
     * @return true 开启了二次校验 false 未开启二次校验
     */
    public boolean isOpenSecCheck() {
        // 只要二次校验手机标识和二次校验邮箱校验开启了一个，就认为开启了二次校验
        return BooleanUtils.isTrue(this.secCheckPhoneFlag) || BooleanUtils.isTrue(this.secCheckEmailFlag);
    }
}
