package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表权限
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
@ApiModel("报表权限")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_report_permission")
public class ReportPermission extends AuditDomain {

    public static final String FIELD_PERMISSION_ID = "permissionId";
    public static final String FIELD_REPORT_ID = "reportId";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ROLE_ID = "roleId";
    public static final String FIELD_REMARK = "remark";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long permissionId;
    @ApiModelProperty(value = "报表类别，hrpt_report.report_id")
    @NotNull
    @Unique
    @Encrypt
    private Long reportId;
    @ApiModelProperty(value = "有效期从")
    private LocalDate startDate;
    @ApiModelProperty(value = "有效期至")
    private LocalDate endDate;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    @Unique
    private Long tenantId;
    @ApiModelProperty(value = "角色ID,iam_role.role_id")
    @Unique
    @Encrypt
    private Long roleId;
    @ApiModelProperty(value = "备注说明")
    private String remark;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String roleName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getPermissionId() {
        return permissionId;
    }

    public ReportPermission setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    public Long getReportId() {
        return reportId;
    }

    public ReportPermission setReportId(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public ReportPermission setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ReportPermission setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReportPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public ReportPermission setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ReportPermission setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ReportPermission setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRoleName() {
        return roleName;
    }

    public ReportPermission setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }
}
