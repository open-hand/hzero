package org.hzero.plugin.platform.hr.api.dto;

/**
 * 员工用户关联导入DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/04/24 15:42
 */
public class EmployeeUserImportDTO {
    private String employeeNum;
    private String loginName;

    public String getEmployeeNum() {
        return employeeNum;
    }

    public void setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "EmployeeUserImportDTO{" + "employeeNum='" + employeeNum + '\'' + ", loginName='" + loginName + '\''
                        + '}';
    }
}
