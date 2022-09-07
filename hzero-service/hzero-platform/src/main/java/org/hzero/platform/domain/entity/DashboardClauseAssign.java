package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * 工作台条目分配租户
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
@ApiModel("工作台条目分配租户")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_dbd_clause_assign")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardClauseAssign extends AuditDomain {

    public static final String FIELD_CLAUSE_ASSIGN_ID = "clauseAssignId";
    public static final String FIELD_CLAUSE_ID = "clauseId";
    public static final String FIELD_TENANT_ID = "tenantId";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 校验起始时间是否小于截止时间
	 */
	public void checkTimeValidity() {
		// 起止时间都存在时进行校验
		if (beginDate != null && endDate != null) {
			long result = beginDate.getTime() - endDate.getTime();
			if (result > 0L) {
				// 起始时间大于截止时间
				throw new CommonException(HpfmMsgCodeConstants.ERROR_DATE_IRREGULARITY);
			}
		}
	}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID")
    @Id
    @GeneratedValue
	@Encrypt
    private Long clauseAssignId;
    @ApiModelProperty(value = "条目ID")
    @NotNull
	@Encrypt
    private Long clauseId;
    @ApiModelProperty(value = "租户ID")
    @NotNull
    private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

	@ApiModelProperty(value = "租户名称")
	@Transient
	private String tenantName;
	@ApiModelProperty(value = "集团编码")
	@Transient
	private String tenantNum;/**
	 * 查询条件-起始时间
	 */
	@Transient
	private Date beginDate;
	@Transient
	private Date endDate;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
     * @return 表ID
     */
	public Long getClauseAssignId() {
		return clauseAssignId;
	}

	public void setClauseAssignId(Long clauseAssignId) {
		this.clauseAssignId = clauseAssignId;
	}
    /**
     * @return 条目ID
     */
	public Long getClauseId() {
		return clauseId;
	}

	public void setClauseId(Long clauseId) {
		this.clauseId = clauseId;
	}
    /**
     * @return 租户ID
     */
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

	public String getTenantNum() {
		return tenantNum;
	}

	public void setTenantNum(String tenantNum) {
		this.tenantNum = tenantNum;
	}
}
