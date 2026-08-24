package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单点二级域名分配
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
@ApiModel("单点二级域名分配")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_domain_assign")
public class DomainAssign extends AuditDomain {

    public static final String FIELD_DOMAIN_ASSIGN_ID = "domainAssignId";
    public static final String FIELD_DOMAIN_ID = "domainId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_COMPANY_ID = "companyId";

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
    private Long domainAssignId;
    @ApiModelProperty(value = "域名ID，hiam_domain.domain_id", required = true)
    @NotNull
	@Encrypt
    private Long domainId;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "客户公司ID，HPFM_COMPANY.COMPANY_ID")
	@Encrypt
    private Long companyId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

	/**
	 * 租户名称
	 */
	@Transient
	private String tenantName;

	/**
	 * 公司名称
	 */
	@Transient
	private String companyName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

	public Long getDomainAssignId() {
		return domainAssignId;
	}

	public DomainAssign setDomainAssignId(Long domainAssignId) {
		this.domainAssignId = domainAssignId;
		return this;
	}

	public Long getDomainId() {
		return domainId;
	}

	public DomainAssign setDomainId(Long domainId) {
		this.domainId = domainId;
		return this;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public DomainAssign setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public DomainAssign setCompanyId(Long companyId) {
		this.companyId = companyId;
		return this;
	}

	public String getTenantName() {
		return tenantName;
	}

	public DomainAssign setTenantName(String tenantName) {
		this.tenantName = tenantName;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public DomainAssign setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
}
