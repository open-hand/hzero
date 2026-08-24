package org.hzero.plugin.platform.hr.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 员工岗位分配DTO
 * </p>
 *
 * @author qingsheng.chen 2018/8/1 星期三 10:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAssignDTO extends AuditDomain {
    @Encrypt
    private Long employeeAssignId;
    private Long tenantId;
    @Encrypt
    private Long employeeId;
    @Encrypt
    private Long unitCompanyId;
    private String unitCompanyName;
    @Encrypt
    private Long unitId;
    private String unitName;
    @Encrypt
    private Long positionId;
    private String positionName;
    private Integer enabledFlag;
    private Integer primaryPositionFlag;
    /**
     * 员工编码
     */
    private String employeeNum;
    /**
     * 岗位编码
     */
    private String positionCode;

    public Long getEmployeeAssignId() {
        return employeeAssignId;
    }

    public EmployeeAssignDTO setEmployeeAssignId(Long employeeAssignId) {
        this.employeeAssignId = employeeAssignId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public EmployeeAssignDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public EmployeeAssignDTO setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public EmployeeAssignDTO setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
        return this;
    }

    public String getUnitCompanyName() {
        return unitCompanyName;
    }

    public EmployeeAssignDTO setUnitCompanyName(String unitCompanyName) {
        this.unitCompanyName = unitCompanyName;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public EmployeeAssignDTO setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public String getUnitName() {
        return unitName;
    }

    public EmployeeAssignDTO setUnitName(String unitName) {
        this.unitName = unitName;
        return this;
    }

    public Long getPositionId() {
        return positionId;
    }

    public EmployeeAssignDTO setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    public String getPositionName() {
        return positionName;
    }

    public EmployeeAssignDTO setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public EmployeeAssignDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public EmployeeAssignDTO setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
        return this;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public EmployeeAssignDTO setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
        return this;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public EmployeeAssignDTO setPositionCode(String positionCode) {
        this.positionCode = positionCode;
        return this;
    }

    @Override
    public String toString() {
        return "EmployeeAssignDTO{" +
                "employeeAssignId=" + employeeAssignId +
                ", tenantId=" + tenantId +
                ", employeeId=" + employeeId +
                ", unitCompanyId=" + unitCompanyId +
                ", unitCompanyName=" + unitCompanyName +
                ", unitId=" + unitId +
                ", unitName=" + unitName +
                ", positionId=" + positionId +
                ", positionName=" + positionName +
                ", enabledFlag=" + enabledFlag +
                ", primaryPositionFlag=" + primaryPositionFlag +
                ", employeeNum=" + employeeNum +
                ", positionCode=" + positionCode +
                '}';
    }
}
