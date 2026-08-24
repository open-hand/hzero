package org.hzero.oauth.domain.vo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.oauth.domain.entity.User;

/**
 * UserVO
 *
 * @author bojiangzhou 2018/07/01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO implements SecurityToken {

    /**
     * 生成系统配置的redis缓存key
     *
     * @param configCode 系统配置code
     * @return key
     */
    public static String generateCacheKey(String configCode, Long tenantId) {
        return HZeroService.Platform.CODE + ":config" + ":" + configCode + "." + tenantId;
    }
    private String _token;

    private Long id;
    private String loginName;
    private String email;
    private Long organizationId;
    private String realName;
    private String phone;
    private String internationalTelCode;
    private String internationalTelMeaning;
    private String imageUrl;
    private String profilePhoto;
    private String language;
    private String languageName;
    private String timeZone;
    private String timeZoneMeaning;
    private Date lastPasswordUpdatedAt;
    private Date lastLoginAt;
    private Boolean enabled;
    private Boolean locked;
    private Boolean ldap;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date lockedUntilAt;
    private Integer passwordAttempt;
    private Boolean admin;
    private String userType;
    private String companyName;

    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate birthday;
    private String nickname;
    private Integer gender;
    private Long countryId;
    private String countryName;
    private Long regionId;
    private String regionName;
    private String addressDetail;

    private String invitationCode;
    private Long employeeId;
    private Long textId;
    private String securityLevelCode;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate startDateActive;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate endDateActive;
    private Integer userSource;
    private Integer phoneCheckFlag;
    private Integer emailCheckFlag;
    private Integer passwordResetFlag;
    private Long defaultRoleId;
    private String defaultRoleName;
    private Long defaultCompanyId;
    private String defaultCompanyName;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date lockedDate;

    private String tenantName;
    private String tenantNum;
    private String groupName;
    private String groupNum;
    private String securityLevelMeaning;

    private String dateFormat;
    private String dateFormatMeaning;
    private String timeFormat;
    private String timeFormatMeaning;
    private String dateTimeFormat;

    /**
     * 系统标题
     */
    private String title;

    /**
     * 系统logo路径
     */
    private String logo;

    /**
     * menu layout
     */
    private String menuLayout;

    /**
     * menu layout theme
     */
    private String menuLayoutTheme;

    // 当前切换的租户，organizationId用户所属租户
    private Long tenantId;
    // 当前角色ID
    private Long currentRoleId;
    // 当前角色编码
    private String currentRoleCode;
    // 当前角色名称
    private String currentRoleName;
    // 当前角色层级
    private String currentRoleLevel;
    /**
     * 页面角标
     */
    private String favicon;

    //
    // 查询字段
    // ------------------------------------------------------------------------------
    private Integer defaultTenant; // 是否默认租户 即 0
    private String userCondition; // 组合条件：账号、描述、邮箱、手机
    private String tenantCondition; // 租户组合条件：租户名称、编号
    private Long objectVersionNumber; // 版本号

    /**
     * 排除用户ID列表
     * 排除用户Login Name列表
     * @since 2018/12/12
     * @author mingwei.liu@hand-china.com
     */
    private List<Long> excludeUserIds;
    private List<String> excludeUserLoginNames;

    public String getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(String menuLayout) {
        this.menuLayout = menuLayout;
    }

    public String getMenuLayoutTheme() {
        return menuLayoutTheme;
    }

    public void setMenuLayoutTheme(String menuLayoutTheme) {
        this.menuLayoutTheme = menuLayoutTheme;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getInternationalTelMeaning() {
        return internationalTelMeaning;
    }

    public void setInternationalTelMeaning(String internationalTelMeaning) {
        this.internationalTelMeaning = internationalTelMeaning;
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

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public Long getDefaultRoleId() {
        return defaultRoleId;
    }

    public void setDefaultRoleId(Long defaultRoleId) {
        this.defaultRoleId = defaultRoleId;
    }

    public String getDefaultRoleName() {
        return defaultRoleName;
    }

    public void setDefaultRoleName(String defaultRoleName) {
        this.defaultRoleName = defaultRoleName;
    }

    public Long getDefaultCompanyId() {
        return defaultCompanyId;
    }

    public void setDefaultCompanyId(Long defaultCompanyId) {
        this.defaultCompanyId = defaultCompanyId;
    }

    public String getDefaultCompanyName() {
        return defaultCompanyName;
    }

    public void setDefaultCompanyName(String defaultCompanyName) {
        this.defaultCompanyName = defaultCompanyName;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupNum() {
        return groupNum;
    }

    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }

    public String getSecurityLevelMeaning() {
        return securityLevelMeaning;
    }

    public void setSecurityLevelMeaning(String securityLevelMeaning) {
        this.securityLevelMeaning = securityLevelMeaning;
    }

    public Integer getDefaultTenant() {
        return defaultTenant;
    }

    public void setDefaultTenant(Integer defaultTenant) {
        this.defaultTenant = defaultTenant;
    }

    public String getUserCondition() {
        return userCondition;
    }

    public void setUserCondition(String userCondition) {
        this.userCondition = userCondition;
    }

    public String getTenantCondition() {
        return tenantCondition;
    }

    public void setTenantCondition(String tenantCondition) {
        this.tenantCondition = tenantCondition;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getTimeZoneMeaning() {
        return timeZoneMeaning;
    }

    public void setTimeZoneMeaning(String timeZoneMeaning) {
        this.timeZoneMeaning = timeZoneMeaning;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getDateFormatMeaning() {
        return dateFormatMeaning;
    }

    public void setDateFormatMeaning(String dateFormatMeaning) {
        this.dateFormatMeaning = dateFormatMeaning;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeFormatMeaning() {
        return timeFormatMeaning;
    }

    public void setTimeFormatMeaning(String timeFormatMeaning) {
        this.timeFormatMeaning = timeFormatMeaning;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCurrentRoleId() {
        return currentRoleId;
    }

    public void setCurrentRoleId(Long currentRoleId) {
        this.currentRoleId = currentRoleId;
    }

    public String getCurrentRoleCode() {
        return currentRoleCode;
    }

    public void setCurrentRoleCode(String currentRoleCode) {
        this.currentRoleCode = currentRoleCode;
    }

    public String getCurrentRoleName() {
        return currentRoleName;
    }

    public void setCurrentRoleName(String currentRoleName) {
        this.currentRoleName = currentRoleName;
    }

    public String getCurrentRoleLevel() {
        return currentRoleLevel;
    }

    public void setCurrentRoleLevel(String currentRoleLevel) {
        this.currentRoleLevel = currentRoleLevel;
    }

    public List<Long> getExcludeUserIds() {
        return excludeUserIds;
    }

    public void setExcludeUserIds(List<Long> excludeUserIds) {
        this.excludeUserIds = excludeUserIds;
    }

    public List<String> getExcludeUserLoginNames() {
        return excludeUserLoginNames;
    }

    public void setExcludeUserLoginNames(List<String> excludeUserLoginNames) {
        this.excludeUserLoginNames = excludeUserLoginNames;
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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    @Override
    public String get_token() {
        return this._token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return User.class;
    }

    public String getFavicon() {
        return favicon;
    }

    public UserVO setFavicon(String favicon) {
        this.favicon = favicon;
        return this;
    }
}
