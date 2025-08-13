package org.hzero.plugin.platform.hr.api.dto;

import java.util.Date;
import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 17:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO extends AuditDomain {
    @Encrypt
    private Long employeeId;

    /**
     * 员工编码
     */
    private String employeeCode;

    /**
     * 员工姓名
     */
    private String name;
    /**
     * 员工英文姓名
     */
    private String nameEn;

    /**
     * 出生日期
     */
    private Date bornDate;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 移动电话
     */
    private String mobile;

    /**
     * 入职日期
     */
    private Date joinDate;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * ID
     */
    private String certificateId;

    /**
     * 状态
     */
    private String status;

    private String statusName;

    /**
     * 启用状态
     */
    private String enabledFlag;

    /**
     * 部门Id
     */
    @Encrypt
    private Long unitId;
    /**
     * 岗位id
     */
    @Encrypt
    private Long positionId;

    /**
     * 部门名称
     */
    private String unitName;

    /**
     * 岗位名称
     */
    private String positionName;
    /**
     * 证件类型
     */
    private String certificateType;
    /**
     * 有效日期从
     */
    private Date effectiveStartDate;
    /**
     * 有效日期至
     */
    private Date effectiveEndDate;
    /**
     * 拼音
     */
    private String phoneticize;
    /**
     * 快速索引
     */
    private String quickIndex;

    /**
     * 员工Id列表
     */
    @Encrypt
    private List<Long> idList;
    /**
     * 主岗标记 1主岗 0 兼职
     */
    private Integer primaryPositionFlag;
    /**
     * 部门岗位分配列表
     */
    private List<UnitAssignDTO> list;
    /**
     * 版本号
     */
    private Long objectVersionNumber;
    /**
     * 所属公司名称
     */
    private String companyName;

    public Long getEmployeeId() {
        return employeeId;
    }

    public EmployeeDTO setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public EmployeeDTO setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public EmployeeDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Date getBornDate() {
        return bornDate;
    }

    public EmployeeDTO setBornDate(Date bornDate) {
        this.bornDate = bornDate;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public EmployeeDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public EmployeeDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public EmployeeDTO setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
        return this;
    }

    public String getNameEn() {
        return nameEn;
    }

    public EmployeeDTO setNameEn(String nameEn) {
        this.nameEn = nameEn;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public EmployeeDTO setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getPhoneticize() {
        return phoneticize;
    }

    public EmployeeDTO setPhoneticize(String phoneticize) {
        this.phoneticize = phoneticize;
        return this;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public EmployeeDTO setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public EmployeeDTO setCertificateId(String certificateId) {
        this.certificateId = certificateId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EmployeeDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getStatusName() {
        return statusName;
    }

    public EmployeeDTO setStatusName(String statusName) {
        this.statusName = statusName;
        return this;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public EmployeeDTO setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public EmployeeDTO setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public Long getPositionId() {
        return positionId;
    }

    public EmployeeDTO setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    public String getUnitName() {
        return unitName;
    }

    public EmployeeDTO setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public String getPositionName() {
        return positionName;
    }

    public EmployeeDTO setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public EmployeeDTO setCertificateType(String certificateType) {
        this.certificateType = certificateType;
        return this;
    }

    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public EmployeeDTO setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
        return this;
    }

    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public EmployeeDTO setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
        return this;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public EmployeeDTO setIdList(List<Long> idList) {
        this.idList = idList;
        return this;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public void setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
    }

    public List<UnitAssignDTO> getList() {
        return list;
    }

    public void setList(List<UnitAssignDTO> list) {
        this.list = list;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "employeeId=" + employeeId +
                ", employeeCode='" + employeeCode + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", bornDate=" + bornDate +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", joinDate=" + joinDate +
                ", gender=" + gender +
                ", certificateId='" + certificateId + '\'' +
                ", status='" + status + '\'' +
                ", statusName='" + statusName + '\'' +
                ", enabledFlag='" + enabledFlag + '\'' +
                ", unitId=" + unitId +
                ", positionId=" + positionId +
                ", unitName='" + unitName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", certificateType='" + certificateType + '\'' +
                ", effectiveStartDate=" + effectiveStartDate +
                ", effectiveEndDate=" + effectiveEndDate +
                ", phoneticize='" + phoneticize + '\'' +
                ", quickIndex='" + quickIndex + '\'' +
                ", idList=" + idList +
                ", primaryPositionFlag=" + primaryPositionFlag +
                ", list=" + list +
                ", objectVersionNumber=" + objectVersionNumber +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
