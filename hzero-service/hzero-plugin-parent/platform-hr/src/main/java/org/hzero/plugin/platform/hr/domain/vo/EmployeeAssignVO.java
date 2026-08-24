package org.hzero.plugin.platform.hr.domain.vo;

/**
 * 员工分配缓存类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/11/13 10:16
 */
public class EmployeeAssignVO {

    /**
     * 员工Id
     */
    private Long employeeId;
    /**
     * 公司/集团Id
     */
    private Long unitCompanyId;
    /**
     * 部门Id
     */
    private Long unitId;
    /**
     * 岗位Id
     */
    private Long positionId;
    /**
     * 主管岗位
     */
    private Integer primaryPositionFlag;
    /**
     * 租户Id
     */
    private Long tenantId;

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public void setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public void setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "EmployeeAssignVO{" + "employeeId=" + employeeId + ", unitCompanyId=" + unitCompanyId + ", unitId="
                        + unitId + ", positionId=" + positionId + ", primaryPositionFlag=" + primaryPositionFlag
                        + ", tenantId=" + tenantId + '}';
    }
}
