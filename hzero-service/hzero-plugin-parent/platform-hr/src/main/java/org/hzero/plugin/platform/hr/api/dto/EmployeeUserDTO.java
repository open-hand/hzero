package org.hzero.plugin.platform.hr.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author cyan 2018/09/17 13:02
 */
@ApiModel("员工用户关系DTO")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EmployeeUserDTO extends AuditDomain {
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Encrypt
    private Long employeeUserId;
    private Long tenantId;
    @ApiModelProperty(value = "员工ID", required = true)
    @Encrypt
    private Long employeeId;
    @Encrypt
    private Long userId;

    private String loginName;
    private String realName;
    private String employeeNum;
    private String employeeName;
    private Integer enabledFlag;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户手机号前缀
     */
    private String internationalTelCode;
    /**
     * 用户组织Id
     */
    private Long organizationId;

    /**
     * 语言
     */
    private String language;
    private String nameEn;
    private Integer gender;
    private String cid;
    private String quickIndex;
    private String phoneticize;
    private String status;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }


    public Long getEmployeeUserId() {
        return employeeUserId;
    }

    public void setEmployeeUserId(Long employeeUserId) {
        this.employeeUserId = employeeUserId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public EmployeeUserDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public EmployeeUserDTO setNameEn(String nameEn) {
        this.nameEn = nameEn;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public EmployeeUserDTO setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getCid() {
        return cid;
    }

    public EmployeeUserDTO setCid(String cid) {
        this.cid = cid;
        return this;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public EmployeeUserDTO setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    public String getPhoneticize() {
        return phoneticize;
    }

    public EmployeeUserDTO setPhoneticize(String phoneticize) {
        this.phoneticize = phoneticize;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EmployeeUserDTO setStatus(String status) {
        this.status = status;
        return this;
    }
}
