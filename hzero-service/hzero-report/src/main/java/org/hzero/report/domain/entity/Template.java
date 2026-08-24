package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表模板
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
@ApiModel("报表模板")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_template")
@MultiLanguage
public class Template extends AuditDomain {

    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TEMPLATE_TYPE_CODE = "templateTypeCode";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_REMARK = "remark";
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
    private Long templateId;
    @ApiModelProperty(value = "模板类型，值集:HRPT.TEMPLATE_TYPE")
    @NotBlank
    @LovValue(value = "HRPT.TEMPLATE_TYPE", meaningField = "templateTypeMeaning")
    private String templateTypeCode;
    @ApiModelProperty(value = "模板编码")
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String templateCode;
    @ApiModelProperty(value = "模板名称")
    @NotBlank
    @MultiLanguageField
    private String templateName;
    @ApiModelProperty(value = "启用标识")
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String templateTypeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getTemplateId() {
        return templateId;
    }

    public Template setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public Template setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public Template setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Template setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Template setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Template setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Template setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Template setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public Template setTemplateTypeMeaning(String templateTypeMeaning) {
        this.templateTypeMeaning = templateTypeMeaning;
        return this;
    }
}
