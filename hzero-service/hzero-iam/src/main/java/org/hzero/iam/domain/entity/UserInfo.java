package org.hzero.iam.domain.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 用户信息，与 {@link User} 是一对一关系
 *
 * @author bojiangzhou 2018/06/30
 */
@ModifyAudit
@VersionAudit
@Table(name = "hiam_user_info")
public class UserInfo extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_user_info";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_BIRTHDAY = "birthday";
    public static final String FIELD_NICKNAME = "nickname";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_COUNTRY_ID = "countryId";
    public static final String FIELD_ORGION_ID = "regionId";
    public static final String FIELD_ADDRESS_DETAIL = "addressDetail";
    public static final String FIELD_START_DATE_ACTIVE = "startDateActive";
    public static final String FIELD_END_DATE_ACTIVE = "endDateActive";
    public static final String FILED_TENANT_ID = "tenantId";
    public static final String FIELD_SEC_CHECK_PHONE_FLAG = "secCheckPhoneFlag";
    public static final String FIELD_SEC_CHECK_EMAIL_FLAG = "secCheckEmailFlag";


    //
    // getter/setter
    // ------------------------------------------------------------------------------
    @Id
    private Long userId;
    private String companyName;
    private String invitationCode;
    private Long employeeId;
    private Long textId;
    private String securityLevelCode;
    private LocalDate startDateActive;
    private LocalDate endDateActive;
    private Integer userSource;
    private Integer phoneCheckFlag;
    private Integer emailCheckFlag;
    private Integer passwordResetFlag;
    private LocalDate lockedDate;
    private String dateFormat;
    private String timeFormat;
    private Long defaultTenantId;

    private LocalDate birthday;
    private String nickname;
    private Integer gender;
    private Long countryId;
    private Long regionId;
    private String addressDetail;
    private Long tenantId;
    private Boolean secCheckPhoneFlag;
    private Boolean secCheckEmailFlag;

    public Long getTenantId() {
        return tenantId;
    }

    public UserInfo setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 用户ID，同 {@link User#id}
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return 企业名称
     */
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return 邀请码
     */
    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    /**
     * @return 员工id
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * @return 协议id
     */
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    /**
     * @return 密码安全等级，值集：HIAM.SECURITY_LEVEL
     */
    public String getSecurityLevelCode() {
        return securityLevelCode;
    }

    public void setSecurityLevelCode(String securityLevelCode) {
        this.securityLevelCode = securityLevelCode;
    }

    /**
     * @return 有效日期从
     */
    public LocalDate getStartDateActive() {
        return startDateActive;
    }

    public void setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
    }

    /**
     * @return 有效日期至
     */
    public LocalDate getEndDateActive() {
        return endDateActive;
    }

    public void setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
    }

    /**
     * @return 用户来源：0-由管理员创建，1-门户注册
     * @see org.hzero.common.UserSource
     */
    public Integer getUserSource() {
        return userSource;
    }

    public void setUserSource(Integer userSource) {
        this.userSource = userSource;
    }

    /**
     * @return 电话是否已验证
     */
    public Integer getPhoneCheckFlag() {
        return phoneCheckFlag;
    }

    public void setPhoneCheckFlag(Integer phoneCheckFlag) {
        this.phoneCheckFlag = phoneCheckFlag;
    }

    /**
     * @return 邮箱是否已验证
     */
    public Integer getEmailCheckFlag() {
        return emailCheckFlag;
    }

    public void setEmailCheckFlag(Integer emailCheckFlag) {
        this.emailCheckFlag = emailCheckFlag;
    }

    /**
     * @return 密码是否重置
     */
    public Integer getPasswordResetFlag() {
        return passwordResetFlag;
    }

    public void setPasswordResetFlag(Integer passwordResetFlag) {
        this.passwordResetFlag = passwordResetFlag;
    }

    /**
     * @return 账户锁定日期
     */
    public LocalDate getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(LocalDate lockedDate) {
        this.lockedDate = lockedDate;
    }

    /**
     * @return 日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    public UserInfo setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    /**
     * @return 时间格式
     */
    public String getTimeFormat() {
        return timeFormat;
    }

    public UserInfo setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        return this;
    }

    /**
     * @return 默认租户ID
     */
    public Long getDefaultTenantId() {
        return defaultTenantId;
    }

    public UserInfo setDefaultTenantId(Long defaultTenantId) {
        this.defaultTenantId = defaultTenantId;
        return this;
    }

    /**
     * @return 出生日期
     */
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * @return 昵称
     */
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return 性别
     */
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * @return 国家
     */
    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    /**
     * @return 地区id
     */
    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    /**
     * @return 详细地址
     */
    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
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
}
