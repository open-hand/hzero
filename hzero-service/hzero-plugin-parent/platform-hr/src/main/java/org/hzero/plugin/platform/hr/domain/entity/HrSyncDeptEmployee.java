package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * HR数据同步员工分配岗位
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@ApiModel("HR数据同步员工分配岗位")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_hr_sync_dept_employee")
public class HrSyncDeptEmployee extends AuditDomain {

    public static final String FIELD_SYNC_ASSIGN_ID = "syncAssignId";
    public static final String FIELD_SYNC_DEPT_ID = "syncDeptId";
    public static final String FIELD_SYNC_EMPLOYEE_ID = "syncEmployeeId";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long syncAssignId;
    @ApiModelProperty(value = "同步数据部门ID",required = true)
    @NotNull
    private Long syncDeptId;
    @ApiModelProperty(value = "同步数据员工ID",required = true)
    @NotNull
    private Long syncEmployeeId;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id",required = true)
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getSyncAssignId() {
		return syncAssignId;
	}

	public HrSyncDeptEmployee setSyncAssignId(Long syncAssignId) {
		this.syncAssignId = syncAssignId;
		return this;
	}
    /**
     * @return 同步数据部门ID
     */
	public Long getSyncDeptId() {
		return syncDeptId;
	}

	public HrSyncDeptEmployee setSyncDeptId(Long syncDeptId) {
		this.syncDeptId = syncDeptId;
		return this;
	}
    /**
     * @return 同步数据员工ID
     */
	public Long getSyncEmployeeId() {
		return syncEmployeeId;
	}

	public HrSyncDeptEmployee setSyncEmployeeId(Long syncEmployeeId) {
		this.syncEmployeeId = syncEmployeeId;
		return this;
	}
    /**
     * @return 租户ID,hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public HrSyncDeptEmployee setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

}
