package org.hzero.plugin.platform.hr.api.dto;

/**
 * 员工岗位关联导入DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 13:31
 */
public class EmployeeAssignImportDTO {
    /**
     * 员工编码
     */
    private String employeeNum;

    /**
     * 岗位编码
     */
    private String positionCode;

    /**
     * 是否主岗
     */
    private String primaryPositionFlag;

    public String getEmployeeNum() {
        return employeeNum;
    }

    public EmployeeAssignImportDTO setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
        return this;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public EmployeeAssignImportDTO setPositionCode(String positionCode) {
        this.positionCode = positionCode;
        return this;
    }

    public String getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public EmployeeAssignImportDTO setPrimaryPositionFlag(String primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
        return this;
    }

    @Override
    public String toString() {
        return "EmployeeAssignImportDTO{" + "employeeNum='" + employeeNum + '\'' + ", positionCode='" + positionCode
                        + '\'' + ", primaryPositionFlag='" + primaryPositionFlag + '\'' + '}';
    }
}
