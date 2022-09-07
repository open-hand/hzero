package org.hzero.platform.domain.entity;

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
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 分配模板
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-03 10:57:26
 */
@ApiModel("分配模板")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hpfm_template_assign")
public class TemplateAssign extends AuditDomain {

    public static final String FIELD_TEMPLATE_ASSIGN_ID = "templateAssignId";
    public static final String FIELD_SOURCE_TYPE = "sourceType";
    public static final String FIELD_SOURCE_KEY = "sourceKey";
    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	public TemplateAssign(@NotBlank String sourceType, @NotBlank String sourceKey, @NotNull Long tenantId,
			@NotNull Integer defaultFlag) {
		this.sourceType = sourceType;
		this.sourceKey = sourceKey;
		this.tenantId = tenantId;
		this.defaultFlag = defaultFlag;
	}

	public TemplateAssign() {
	}

	//
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
	@Encrypt
    private Long templateAssignId;
    @ApiModelProperty(value = "关联来源类型，DOMAIN")
    @NotBlank
    private String sourceType;
    @ApiModelProperty(value = "关联模板的来源KEY")
    @NotBlank
	@Encrypt
    private String sourceKey;
    @ApiModelProperty(value = "模板Id，hpfm_template.template_id")
    @NotNull
	@Encrypt
    private Long templateId;
    @ApiModelProperty(value = "租户Id")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "默认标识 1:默认 0:非默认")
    @NotNull
    private Integer defaultFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 
     */
	public Long getTemplateAssignId() {
		return templateAssignId;
	}

	public void setTemplateAssignId(Long templateAssignId) {
		this.templateAssignId = templateAssignId;
	}
    /**
     * @return 关联来源类型，DOMAIN
     */
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
    /**
     * @return 关联模板的来源KEY
     */
	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
    /**
     * @return 模板Id，hpfm_template.template_id
     */
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
    /**
     * @return 租户Id
     */
	public Long getTenantId() {
		return tenantId;
	}

	public TemplateAssign setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}
    /**
     * @return 默认标识 1:默认 0:非默认
     */
	public Integer getDefaultFlag() {
		return defaultFlag;
	}

	public TemplateAssign setDefaultFlag(Integer defaultFlag) {
		this.defaultFlag = defaultFlag;
		return this;
	}

}
