package org.hzero.iam.domain.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import javax.persistence.*;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.boot.oauth.util.PasswordUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.user.UserType;
import org.hzero.core.util.CheckStrength;
import org.hzero.iam.api.dto.TenantRoleDTO;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 与UserE实体基本不变，扩展了{@link UserInfo}，与其一对一关系.
 *
 * @author bojiangzhou 2018/07/04
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ModifyAudit
@VersionAudit
@Table(name = "iam_user")
@ApiModel("用户")
public class User extends AuditDomain {

    public static final String ENCRYPT_KEY = "user";

    public static final String FIELD_ID = "id";
    public static final String FIELD_LOGIN_NAME = "loginName";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_ORGANIZATION_ID = "organizationId";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_REAL_NAME = "realName";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_INTERNATIONAL_TEL_CODE = "internationalTelCode";
    public static final String FIELD_IMAGE_URL = "imageUrl";
    public static final String FIELD_PROFILE_PHOTO = "profilePhoto";
    public static final String FIELD_LANGUAGE = "language";
    public static final String FIELD_TIME_ZONE = "timeZone";
    public static final String FIELD_LAST_PASSWORD_UPDATED_AT = "lastPasswordUpdatedAt";
    public static final String FIELD_LAST_LOGIN_AT = "lastLoginAt";
    public static final String FIELD_ENABLED = "enabled";
    public static final String FIELD_LOCKED = "locked";
    public static final String FIELD_LDAP = "ldap";
    public static final String FIELD_LOCKED_UNTIL_AT = "lockedUntilAt";
    public static final String FIELD_PASSWORD_ATTEMPT = "passwordAttempt";
    public static final String FIELD_ADMIN = "admin";
    public static final String FIELD_COMPANY_NAME = "companyName";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_COUNTRY_ID = "countryId";
    public static final String FIELD_ORGION_ID = "regionId";
    public static final String FIELD_ADDRESS_DETAIL = "addressDetail";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";
    public static final String FIELD_PHONE_CHECK_FLAG = "phoneCheckFlag";
    public static final String FIELD_EMAIL_CHECK_FLAG = "emailCheckFlag";
    public static final String FIELD_DEFAULT_ROLE_ID = "defaultRoleId";
    public static final String FIELD_USER_TYPE = "userType";

    public static final String DEFAULT_LANGUAGE = "zh_CN";
    public static final String DEFAULT_TIME_ZONE = "GMT+8";

    public static final String DEFAULT_USER_TYPE = "P";

    public void clearPassword() {
        this.password = null;
        this.anotherPassword = null;
    }

    public void unlocked() {
        this.locked = false;
        this.lockedDate = null;
        this.lockedUntilAt = null;
    }

    public void locked() {
        this.locked = true;
        this.lockedDate = new Date();
    }

    public void unfrozen() {
        this.enabled = true;
    }

    public void frozen() {
        this.enabled = false;
    }

    public User simpleCopyUser(User user) {
        User u = new User();
        u.setId(user.getId());
        u.setLoginName(user.getLoginName());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        return u;
    }

    /**
     * 加密密码的同时，检测密码安全度
     */
    public void encodePassword() {
        if (!ldapUser()) {
            this.securityLevelCode = CheckStrength.getPasswordLevel(password).name();
            this.password = PasswordUtils.encodedPassword(this.getPassword());
        } else {
            this.securityLevelCode = CheckStrength.LEVEL.EASY.name();
        }
    }

    public Boolean comparePassword(String originalPassword) {
        return PasswordUtils.checkPasswordMatch(originalPassword, this.password);
    }

    public void initUserType() {
        this.userType = UserType.ofDefault(this.userType).value();
    }

    public boolean ldapUser() {
        return this.ldap != null && this.ldap;
    }

    public boolean checkPassword() {
        return this.checkPasswordPolicy == null || this.checkPasswordPolicy;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @Where
    @GeneratedValue
    @Encrypt
    private Long id;

    @Length(max = 128)
    @ApiModelProperty("登录账号，未传则生成默认账号")
    private String loginName;

    /**
     * 子账户导入-账户导入所需字段
     */
    @Length(max = 128)
    @Email
    @ApiModelProperty("邮箱")
    private String email;

    @Where
    @ApiModelProperty(value = "所属租户ID", required = true)
    private Long organizationId;

    /**
     * 子账户导入-账户导入所需字段
     */
    @Column(name = "hash_password")
    @ApiModelProperty(value = "密码", required = true)
    @Length(max = 128)
    private String password;

    /**
     * 子账户导入-账户导入所需字段
     */
    @ApiModelProperty(value = "真实姓名", required = true)
    @Length(max = 128)
    private String realName;

    /**
     * 子账户导入-账户导入所需字段
     */
    @ApiModelProperty(value = "手机号")
    @Length(max = 32)
    private String phone;

    @ApiModelProperty(value = "国际冠码，默认+86")
    private String internationalTelCode;

    @ApiModelProperty(value = "用户头像地址")
    @Length(max = 480)
    private String imageUrl;

    @ApiModelProperty(value = "用户二进制头像")
    private String profilePhoto;

    @ApiModelProperty(value = "语言，默认 zh_CN")
    private String language;

    @ApiModelProperty(value = "时区，默认 GMT+8")
    private String timeZone;

    @ApiModelProperty(value = "密码最后一次修改时间")
    private Date lastPasswordUpdatedAt;

    @ApiModelProperty(value = "最近登录时间")
    private Date lastLoginAt;

    @Column(name = "is_enabled")
    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;
    @Transient
    private Integer enabledFlag;

    @Column(name = "is_locked")
    @ApiModelProperty(value = "是否锁定")
    private Boolean locked;
    @Transient
    private Integer lockedFlag;

    @Column(name = "is_ldap")
    @ApiModelProperty(value = "是否LDAP用户")
    private Boolean ldap;
    @Transient
    private Integer ldapFlag;

    @ApiModelProperty(value = "锁定时间")
    private Date lockedUntilAt;

    @ApiModelProperty(value = "密码尝试次数")
    private Integer passwordAttempt;

    @Column(name = "is_admin")
    @ApiModelProperty(value = "是否超级管理员")
    private Boolean admin;
    @Transient
    private Integer adminFlag;


    /**
     * 用户类型，默认平台用户
     * @see UserType
     */
    @ApiModelProperty(value = "用户类型，中台用户-P，C端用户-C")
    @LovValue(lovCode = "HIAM.USER_TYPE")
    private String userType;

    @Transient
    private List<Role> roles;

    // ===============================================================================
    // 以下字段为UserInfo的字段，User与UserInfo是一对一关系
    // ===============================================================================
    @Transient
    @ApiModelProperty(value = "公司名称")
    private String companyName;
    @Transient
    @ApiModelProperty(value = "邀请码")
    private String invitationCode;
    @Transient
    @ApiModelProperty(value = "员工ID")
    @Encrypt
    private Long employeeId;
    @Transient
    @ApiModelProperty(value = "协议ID")
    @Encrypt
    private Long textId;
    @Transient
    @ApiModelProperty(value = "密码安全等级")
    private String securityLevelCode;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @Transient
    @ApiModelProperty(value = "有效日期起，默认当前时间")
    private LocalDate startDateActive;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @Transient
    @ApiModelProperty(value = "有效日期止")
    private LocalDate endDateActive;
    @Transient
    @ApiModelProperty(value = "用户来源")
    private Integer userSource;
    @Transient
    @ApiModelProperty(value = "手机号是否验证通过")
    private Integer phoneCheckFlag;
    @Transient
    @ApiModelProperty(value = "邮箱是否验证通过")
    private Integer emailCheckFlag;
    @Transient
    @ApiModelProperty(value = "密码是否重置过")
    private Integer passwordResetFlag;
    /**
     * 传递多个默认角色信息放入集合
     */
    @Transient
    private List<TenantRoleDTO> defaultRoles;

    @Transient
    @Encrypt
    private Long defaultCompanyId;
    @Transient
    private Date lockedDate;

    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @Transient
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;
    @Transient
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @Transient
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @Transient
    @ApiModelProperty(value = "国家ID")
    @Encrypt
    private Long countryId;
    @Transient
    @ApiModelProperty(value = "区域ID")
    @Encrypt
    private Long regionId;
    @Transient
    @ApiModelProperty(value = "地址详细")
    private String addressDetail;

    //
    // 导入时默认角色用到的扩展字段
    // ------------------------------------------------------------------------------
    /**
     * 子账户导入-账户导入所需字段
     * 废弃 since 2019-11-26
     */
    @Transient
    @ApiModelProperty("角色编码")
    private String roleCode;
    @Transient
    @ApiModelProperty("角色名称")
    private String roleName;
    @Transient
    @ApiModelProperty("授权用户")
    private String includeAllFlag;

    //
    // 创建用户时用到的扩展字段
    // ------------------------------------------------------------------------------

    @Transient
    private String anotherPassword;
    @Transient
    @ApiModelProperty("用户角色列表")
    private List<MemberRole> memberRoleList;

    /**
     * Added by allen.liu on 2018/7/18
     */
    @Transient
    private String tenantName;
    @Transient
    private String tenantNum;
    @Transient
    private Long sourceId;
    @Transient
    private String assignLevel;
    @Transient
    private String assignLevelMeaning;
    @Transient
    private Long assignLevelValue;
    @Transient
    private String assignLevelValueMeaning;
    @Transient
    private String userTypeMeaning;

    /**
     * 用户是否已注册
     */
    @Transient
    private boolean registered = false;
    /**
     * 账号已注册 提示信息
     */
    @Transient
    private String registeredMessage;

    // 临时存储密码字段
    @Transient
    @JsonIgnore
    private String tmpPassword;

    @Transient
    private String uuid;
    /**
     * 是否检查密码是否符合密码策略
     */
    @Transient
    private Boolean checkPasswordPolicy;

    /**
     * 子账户导入-账户导入所需字段
     * ADD since 2019-11-26
     */
    @Transient
    private String roleLevelPath;

    /**
     * 密码是否加密了
     */
    @Transient
    @JsonIgnore
    private boolean passwordEncrypt = true;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public List<TenantRoleDTO> getDefaultRoles() {
        return defaultRoles;
    }

    public void setDefaultRoles(List<TenantRoleDTO> defaultRoles) {
        this.defaultRoles = defaultRoles;
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
        this.email = StringUtils.lowerCase(email);
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

    public void lockUtilAt(Date date) {
        this.lockedUntilAt = date;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    public String getSecurityLevelCode() {
        return securityLevelCode;
    }

    public void setSecurityLevelCode(String securityLevelCode) {
        this.securityLevelCode = securityLevelCode;
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

    public Integer getUserSource() {
        return userSource;
    }

    public void setUserSource(Integer userSource) {
        this.userSource = userSource;
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

    public Integer getPasswordResetFlag() {
        return passwordResetFlag;
    }

    public void setPasswordResetFlag(Integer passwordResetFlag) {
        this.passwordResetFlag = passwordResetFlag;
    }

    public Long getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(Long defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

    public String getAnotherPassword() {
        return anotherPassword;
    }

    public void setAnotherPassword(String anotherPassword) {
        this.anotherPassword = anotherPassword;
    }

    public List<MemberRole> getMemberRoleList() {
        return memberRoleList;
    }

    public void setMemberRoleList(List<MemberRole> memberRoleList) {
        this.memberRoleList = memberRoleList;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getAssignLevel() {
        return assignLevel;
    }

    public void setAssignLevel(String assignLevel) {
        this.assignLevel = assignLevel;
    }

    public String getAssignLevelMeaning() {
        return assignLevelMeaning;
    }

    public void setAssignLevelMeaning(String assignLevelMeaning) {
        this.assignLevelMeaning = assignLevelMeaning;
    }

    public Long getAssignLevelValue() {
        return assignLevelValue;
    }

    public void setAssignLevelValue(Long assignLevelValue) {
        this.assignLevelValue = assignLevelValue;
    }

    public String getAssignLevelValueMeaning() {
        return assignLevelValueMeaning;
    }

    public void setAssignLevelValueMeaning(String assignLevelValueMeaning) {
        this.assignLevelValueMeaning = assignLevelValueMeaning;
    }

    public String getUserTypeMeaning() {
        return userTypeMeaning;
    }

    public void setUserTypeMeaning(String userTypeMeaning) {
        this.userTypeMeaning = userTypeMeaning;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Integer getLockedFlag() {
        return lockedFlag;
    }

    public void setLockedFlag(Integer lockedFlag) {
        this.lockedFlag = lockedFlag;
    }

    public Integer getLdapFlag() {
        return ldapFlag;
    }

    public void setLdapFlag(Integer ldapFlag) {
        this.ldapFlag = ldapFlag;
    }

    public Integer getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Integer adminFlag) {
        this.adminFlag = adminFlag;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getIncludeAllFlag() {
        return includeAllFlag;
    }

    public void setIncludeAllFlag(String includeAllFlag) {
        this.includeAllFlag = includeAllFlag;
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

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public String getRegisteredMessage() {
        return registeredMessage;
    }

    public void setRegisteredMessage(String registeredMessage) {
        this.registeredMessage = registeredMessage;
    }


    public String getTmpPassword() {
        return tmpPassword;
    }

    public void setTmpPassword(String tmpPassword) {
        this.tmpPassword = tmpPassword;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getCheckPasswordPolicy() {
        return checkPasswordPolicy;
    }

    public void setCheckPasswordPolicy(Boolean checkPasswordPolicy) {
        this.checkPasswordPolicy = checkPasswordPolicy;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("loginName='" + loginName + "'")
                .add("email='" + email + "'")
                .add("organizationId=" + organizationId)
                .add("realName='" + realName + "'")
                .add("phone='" + phone + "'")
                .add("internationalTelCode='" + internationalTelCode + "'")
                .add("language='" + language + "'")
                .add("timeZone='" + timeZone + "'")
                .add("enabled=" + enabled)
                .add("locked=" + locked)
                .add("ldap=" + ldap)
                .add("admin=" + admin)
                .add("userType='" + userType + "'")
                .add("companyName='" + companyName + "'")
                .add("invitationCode='" + invitationCode + "'")
                .add("employeeId=" + employeeId)
                .add("textId=" + textId)
                .add("startDateActive=" + startDateActive)
                .add("endDateActive=" + endDateActive)
                .add("userSource=" + userSource)
                .add("phoneCheckFlag=" + phoneCheckFlag)
                .add("emailCheckFlag=" + emailCheckFlag)
                .add("passwordResetFlag=" + passwordResetFlag)
                .add("lockedDate=" + lockedDate)
                .add("birthday=" + birthday)
                .add("nickname='" + nickname + "'")
                .add("gender=" + gender)
                .add("countryId=" + countryId)
                .add("regionId=" + regionId)
                .add("addressDetail='" + addressDetail + "'")
                .add("memberRoleList=" + memberRoleList)
                .toString();
    }

    public String getRoleLevelPath() {
        return roleLevelPath;
    }

    public void setRoleLevelPath(String roleLevelPath) {
        this.roleLevelPath = roleLevelPath;
    }

    public boolean isPasswordEncrypt() {
        return passwordEncrypt;
    }

    public void setPasswordEncrypt(boolean passwordEncrypt) {
        this.passwordEncrypt = passwordEncrypt;
    }
}
