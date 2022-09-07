package org.hzero.plugin.platform.hr.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 员工岗位分配表
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:44
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_employee_assign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeAssign extends AuditDomain {

    public static final String FIELD_EMPLOYEE_ASSIGN_ID = "employeeAssignId";
    public static final String FIELD_EMPLOYEE_ID = "employeeId";
    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_POSITION_ID = "positionId";
    public static final String FIELD_PRIMARY_POSITION_FLAG = "primaryPositionFlag";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String ENCRYPT = "hpfm_employee_assign";


    /**
     * 编写具体业务方法
     */

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long employeeAssignId;
    @NotNull
    @Encrypt
    private Long employeeId;
    @Encrypt
    private Long unitCompanyId;
    @NotNull
    @Encrypt
    private Long unitId;
    @NotNull
    @Encrypt
    private Long positionId;
    @NotNull
    private Integer enabledFlag;
    @NotNull
    private Integer primaryPositionFlag;
    @NotNull
    private Long tenantId;

    public EmployeeAssign() {

    }

    public EmployeeAssign(Long employeeId, Long tenantId) {
        this.employeeId = employeeId;
        this.tenantId = tenantId;
    }

    public EmployeeAssign(Long employeeId, Long tenantId, Integer primaryPositionFlag, Integer enabledFlag) {
        this.employeeId = employeeId;
        this.tenantId = tenantId;
        this.primaryPositionFlag = primaryPositionFlag;
        this.enabledFlag = enabledFlag;
    }

    public EmployeeAssign(Long employeeId, Long unitCompanyId, Long unitId, Long positionId, Integer enabledFlag, Integer primaryPositionFlag, Long tenantId) {
        this.employeeId = employeeId;
        this.unitCompanyId = unitCompanyId;
        this.unitId = unitId;
        this.positionId = positionId;
        this.enabledFlag = enabledFlag;
        this.primaryPositionFlag = primaryPositionFlag;
        this.tenantId = tenantId;
    }

    /**
     * @return 员工岗位分配ID
     */
    public Long getEmployeeAssignId() {
        return employeeAssignId;
    }

    public void setEmployeeAssignId(Long employeeAssignId) {
        this.employeeAssignId = employeeAssignId;
    }

    /**
     * @return 员工ID
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * @return 公司ID
     */
    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public void setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
    }

    /**
     * @return 组织ID
     */
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    /**
     * @return 岗位ID
     */
    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    /**
     * @return 主岗位标示
     */
    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public void setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long teantId) {
        this.tenantId = teantId;
    }

    /**
     * @return 启用状态
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }
}


