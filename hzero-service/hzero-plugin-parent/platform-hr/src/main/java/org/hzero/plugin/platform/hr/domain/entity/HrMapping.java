package org.hzero.plugin.platform.hr.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * HR主数据映射表
 *
 * @author zifeng.ding@hand-china.com 2019-11-19 15:31:08
 */
@ApiModel("HR主数据映射表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_hr_mapping")
public class HrMapping extends AuditDomain {

    public static final String FIELD_HR_MAPPING_ID = "hrMappingId";
    public static final String FIELD_SYNC_TYPE_CODE = "syncTypeCode";
    public static final String FIELD_HR_TYPE = "hrType";
    public static final String FIELD_HR_KEY = "hrKey";
    public static final String FIELD_EXT_KEY = "extKey";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CHANGE_FLAG = "changeFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    private Long hrMappingId;
    @ApiModelProperty(value = "同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信",required = true)
    @NotBlank
    private String syncTypeCode;
    @ApiModelProperty(value = "HR主数据类型 E:员工 D:部门",required = true)
    @NotBlank
    private String hrType;
   	@ApiModelProperty(value = "HR主数据KEY")
    private String hrKey;
    @ApiModelProperty(value = "外部数据KEY",required = true)
    @NotBlank
    private String extKey;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id",required = true)
    @NotNull
    private Long tenantId;
	@ApiModelProperty(value = "变更标识,hpfm_tenant.change_flag",required = true)
	@NotNull
    private Integer changeFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getHrMappingId() {
		return hrMappingId;
	}

	public HrMapping setHrMappingId(Long hrMappingId) {
		this.hrMappingId = hrMappingId;
		return this;
	}
    /**
     * @return 同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信
     */
	public String getSyncTypeCode() {
		return syncTypeCode;
	}

	public HrMapping setSyncTypeCode(String syncTypeCode) {
		this.syncTypeCode = syncTypeCode;
		return this;
	}
    /**
     * @return HR主数据类型 E:员工 D:部门
     */
	public String getHrType() {
		return hrType;
	}

	public HrMapping setHrType(String hrType) {
		this.hrType = hrType;
		return this;
	}
    /**
     * @return HR主数据KEY
     */
	public String getHrKey() {
		return hrKey;
	}

	public HrMapping setHrKey(String hrKey) {
		this.hrKey = hrKey;
		return this;
	}
    /**
     * @return 外部数据KEY
     */
	public String getExtKey() {
		return extKey;
	}

	public HrMapping setExtKey(String extKey) {
		this.extKey = extKey;
		return this;
	}
    /**
     * @return 租户ID,hpfm_tenant.tenant_id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public HrMapping setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	/**
	 * @return 变更标识,hpfm_tenant.change_flag
	 */
	public Integer getChangeFlag() {
		return changeFlag;
	}

	public HrMapping setChangeFlag(Integer changeFlag) {
		this.changeFlag = changeFlag;
		return this;
	}
}
