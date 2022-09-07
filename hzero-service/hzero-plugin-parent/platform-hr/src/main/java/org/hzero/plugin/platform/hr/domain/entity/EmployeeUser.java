package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hzero.core.base.BaseConstants;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeRepository;
import org.hzero.plugin.platform.hr.domain.repository.EmployeeUserRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 员工用户关系
 *
 * @author like.zhang@hand-china.com 2018-09-17 11:20:55
 */
@ApiModel("员工用户关系")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_employee_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeUser extends AuditDomain {

    public static final String FIELD_EMPLOYEE_USER_ID = "employeeUserId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EMPLOYEE_ID = "employeeId";
    public static final String FIELD_USER_ID = "userId";
    public static final String ENCRYPT = "hpfm_employee_user";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 验证员工是否存在
     *
     * @param employeeRepository EmployeeRepository
     */
    public void validateEmployee(EmployeeRepository employeeRepository) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        if (employeeRepository.selectCount(employee) == BaseConstants.Digital.ZERO) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
    }


    /**
     * 校验同一个用户在同一个租户下只能有一条关系记录
     *
     * @param employeeUserRepository EmployeeUserRepository
     */
    public void validate(EmployeeUserRepository employeeUserRepository) {
        EmployeeUser employeeUser = new EmployeeUser();
        employeeUser.setTenantId(tenantId);
        employeeUser.setUserId(userId);
        if (employeeUserRepository.selectCount(employeeUser) != BaseConstants.Digital.ZERO) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }

    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long employeeUserId;
    @ApiModelProperty(value = "租户id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "员工ID", required = true)
    @NotNull
    @Encrypt
    private Long employeeId;
    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull
    @Encrypt
    private Long userId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getEmployeeUserId() {
        return employeeUserId;
    }

    public void setEmployeeUserId(Long employeeUserId) {
        this.employeeUserId = employeeUserId;
    }

    /**
     * @return 租户id, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 员工ID, hpfm_employee.employee_id
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * @return 用户ID, iam_user.id
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
