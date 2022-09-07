package org.hzero.oauth.domain.entity;

import java.time.LocalDate;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 *
 * @author bojiangzhou 2018/06/30
 */
@ModifyAudit
@VersionAudit
@Table(name = "hiam_user_info")
public class UserInfo extends AuditDomain {



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
    private Integer phoneCheckFlag;
    private Integer emailCheckFlag;
    private LocalDate lockedDate;
    private String dateFormat;
    private String timeFormat;


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

}
