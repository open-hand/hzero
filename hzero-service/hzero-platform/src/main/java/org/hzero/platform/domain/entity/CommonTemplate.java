package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 通用模板
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
@ApiModel("通用模板")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_common_template")
public class CommonTemplate extends AuditDomain {

	public static final String FIELD_TEMPLATE_ID = "templateId";
	public static final String FIELD_TEMPLATE_CODE = "templateCode";
	public static final String FIELD_TEMPLATE_NAME = "templateName";
	public static final String FIELD_TEMPLATE_CATEGORY_CODE = "templateCategoryCode";
	public static final String FIELD_TEMPLATE_CONTENT = "templateContent";
	public static final String FIELD_LANG = "lang";
	public static final String FIELD_TENANT_ID = "tenantId";
	public static final String FIELD_ENABLED_FLAG = "enabledFlag";

	//
	// 业务方法(按public protected private顺序排列)
	// ------------------------------------------------------------------------------

	//
	// 数据库字段
	// ------------------------------------------------------------------------------


	@ApiModelProperty("表ID，主键，供其他表做外键")
	@Id
	@GeneratedValue
	private Long templateId;
	@ApiModelProperty(value = "模板编码", required = true)
	@NotBlank
	private String templateCode;
	@ApiModelProperty(value = "模板名称", required = true)
	@NotBlank
	private String templateName;
	@ApiModelProperty(value = "模板分类.HPFM.TEMPLATE_CATEGORY", required = true)
	@NotBlank
	private String templateCategoryCode;
	@ApiModelProperty(value = "模板内容", required = true)
	@NotBlank
	private String templateContent;
	@ApiModelProperty(value = "语言", required = true)
	@NotBlank
	private String lang;
	@ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
	@NotNull
	private Long tenantId;
	@ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
	@NotNull
	private Integer enabledFlag;

	//
	// 非数据库字段
	// ------------------------------------------------------------------------------

	//
	// getter/setter
	// ------------------------------------------------------------------------------

	/**
	 * @return 表ID，主键，供其他表做外键
	 */
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return 模板编码
	 */
	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	/**
	 * @return 模板名称
	 */
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return 模板分类.HPFM.TEMPLATE_CATEGORY
	 */
	public String getTemplateCategoryCode() {
		return templateCategoryCode;
	}

	public void setTemplateCategoryCode(String templateCategoryCode) {
		this.templateCategoryCode = templateCategoryCode;
	}

	/**
	 * @return 模板内容
	 */
	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	/**
	 * @return 语言
	 */
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * @return 租户ID, hpfm_tenant.tenant_id
	 */
	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	/**
	 * @return 是否启用。1启用，0未启用
	 */
	public Integer getEnabledFlag() {
		return enabledFlag;
	}

	public void setEnabledFlag(Integer enabledFlag) {
		this.enabledFlag = enabledFlag;
	}

	/**
	 * 启用
	 */
	public void enabled() {
		this.enabledFlag = BaseConstants.Flag.YES;
	}

	/**
	 * 禁用
	 */
	public void disabled() {
		this.enabledFlag = BaseConstants.Flag.NO;
	}
}
