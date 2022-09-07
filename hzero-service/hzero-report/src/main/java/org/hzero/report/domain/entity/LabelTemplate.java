package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.report.infra.meta.form.FormElement;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 标签模板
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@ApiModel("标签模板")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_label_template")
@MultiLanguage
public class LabelTemplate extends AuditDomain {

    public static final String FIELD_LABEL_TEMPLATE_ID = "labelTemplateId";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_TEMPLATE_WIDTH = "templateWidth";
    public static final String FIELD_TEMPLATE_HIGH = "templateHigh";
    public static final String FIELD_DATASET_ID = "datasetId";
    public static final String FIELD_TEMPLATE_CONTENT = "templateContent";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
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
    @Encrypt
    private Long labelTemplateId;
    @ApiModelProperty(value = "模板代码", required = true)
    @NotBlank
    @Unique
    private String templateCode;
    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    @MultiLanguageField
    private String templateName;
    @ApiModelProperty(value = "模板宽度", required = true)
    @NotNull
    private Integer templateWidth;
    @ApiModelProperty(value = "模板高度", required = true)
    @NotNull
    private Integer templateHigh;
    @ApiModelProperty(value = "数据集，hrpt_dataset.dataset_id", required = true)
    @NotNull
    @Encrypt
    private Long datasetId;
    @ApiModelProperty(value = "标签模板内容")
    private String templateContent;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Unique
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String datasetName;
    @Transient
    private List<LabelParameter> labelParameterList;
    @ApiModelProperty(value = "标签参数对象集合")
    @Transient
    private List<FormElement> formElements;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getLabelTemplateId() {
        return labelTemplateId;
    }

    public LabelTemplate setLabelTemplateId(Long labelTemplateId) {
        this.labelTemplateId = labelTemplateId;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public LabelTemplate setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public LabelTemplate setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public Integer getTemplateWidth() {
        return templateWidth;
    }

    public LabelTemplate setTemplateWidth(Integer templateWidth) {
        this.templateWidth = templateWidth;
        return this;
    }

    public Integer getTemplateHigh() {
        return templateHigh;
    }

    public LabelTemplate setTemplateHigh(Integer templateHigh) {
        this.templateHigh = templateHigh;
        return this;
    }

    public Long getDatasetId() {
        return datasetId;
    }

    public LabelTemplate setDatasetId(Long datasetId) {
        this.datasetId = datasetId;
        return this;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public LabelTemplate setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public LabelTemplate setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LabelTemplate setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public LabelTemplate setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public LabelTemplate setDatasetName(String datasetName) {
        this.datasetName = datasetName;
        return this;
    }

    public List<LabelParameter> getLabelParameterList() {
        return labelParameterList;
    }

    public LabelTemplate setLabelParameterList(List<LabelParameter> labelParameterList) {
        this.labelParameterList = labelParameterList;
        return this;
    }

    public List<FormElement> getFormElements() {
        return formElements;
    }

    public LabelTemplate setFormElements(List<FormElement> formElements) {
        this.formElements = formElements;
        return this;
    }
}
