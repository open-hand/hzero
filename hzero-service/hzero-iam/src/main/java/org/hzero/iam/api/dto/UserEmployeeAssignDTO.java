package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 用户员工分配DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/05/07 20:03
 */
@ApiModel("用户员工分配DTO")
public class UserEmployeeAssignDTO {
    @ApiModelProperty("用户ID")
    @Encrypt
    private Long userId;
    @ApiModelProperty("员工ID")
    private Long employeeId;
    @ApiModelProperty("员工编码")
    private String employeeNum;
    @ApiModelProperty("员工名称")
    private String employeeName;
    @ApiModelProperty("查询参数租户ID")
    private Long tenantId;
    @ApiModelProperty("租户名称")
    private String tenantName;
    @ApiModelProperty("租户ID")
    private Long organizationId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "UserEmployeeAssignDTO{" +
                "userId=" + userId +
                ", employeeId=" + employeeId +
                ", employeeNum='" + employeeNum + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", organizationId=" + organizationId +
                '}';
    }
}
