package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * HR部门数据同步
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@ApiModel("HR部门数据同步")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_hr_sync_dept")
public class HrSyncDept extends AuditDomain {

    public static final String FIELD_SYNC_DEPT_ID = "syncDeptId";
    public static final String FIELD_SYNC_TYPE_CODE = "syncTypeCode";
	public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_DEPARTMENT_ID = "departmentId";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_PARENTID = "parentid";
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
    private Long syncDeptId;
    @ApiModelProperty(value = "同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信",required = true)
    @NotBlank
    private String syncTypeCode;
	@ApiModelProperty(value = "部门ID,hpfm_unit.unit_id",required = true)
	@NotNull
	private Long unitId;
    @ApiModelProperty(value = "部门ID",required = true)
    @NotNull
    private Long departmentId;
    @ApiModelProperty(value = "名称",required = true)
    @NotBlank
    private String name;
   @ApiModelProperty(value = "序号")    
    private Long orderSeq;
    @ApiModelProperty(value = "父级部门ID",required = true)
    @NotNull
    private Long parentid;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id",required = true)
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	/**
	 * 对象的同步操作，新增：create、更新：update、删除：delete
	 */
	@Transient
	private String syncType;
	/**
	 * 父部门id，hpfm_unit.parent_unit_id
	 */
	@Transient
	private Long parentUnitId;

	@Transient
	private List<HrSyncDept> childDept;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getSyncDeptId() {
		return syncDeptId;
	}

	public void setSyncDeptId(Long syncDeptId) {
		this.syncDeptId = syncDeptId;
	}
    /**
     * @return 同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信
     */
	public String getSyncTypeCode() {
		return syncTypeCode;
	}

	public HrSyncDept setSyncTypeCode(String syncTypeCode) {
		this.syncTypeCode = syncTypeCode;
		return this;
	}
	/**
	 * @return 部门ID,hpfm_unit.unit_id
	 */
	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}
    /**
     * @return 部门ID
     */
	public Long getDepartmentId() {
		return departmentId;
	}

	public HrSyncDept setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
		return this;
	}
    /**
     * @return 名称
     */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    /**
     * @return 序号
     */
	public Long getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(Long orderSeq) {
		this.orderSeq = orderSeq;
	}
    /**
     * @return 父级部门ID
     */
	public Long getParentid() {
		return parentid;
	}

	public HrSyncDept setParentid(Long parentid) {
		this.parentid = parentid;
		return this;
	}
    /**
     * @return 租户ID,hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public HrSyncDept setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public String getSyncType() {
		return syncType;
	}

	public HrSyncDept setSyncType(String syncType) {
		this.syncType = syncType;
		return this;
	}

	public Long getParentUnitId() {
		return parentUnitId;
	}

	public HrSyncDept setParentUnitId(Long parentUnitId) {
		this.parentUnitId = parentUnitId;
		return this;
	}

	public List<HrSyncDept> getChildDept() {
		return childDept;
	}

	public HrSyncDept setChildDept(List<HrSyncDept> childDept) {
		this.childDept = childDept;
		return this;
	}
}
