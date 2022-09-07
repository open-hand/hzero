package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.common.query.Where;
import org.hzero.report.domain.repository.TemplateDtlRepository;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表模板明细
 *
 * @author xianzhi.chen@hand-china.com 2018-12-05 08:58:44
 */
@ApiModel("报表模板明细")
@VersionAudit
@ModifyAudit
@Table(name = "hrpt_template_dtl")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TemplateDtl extends AuditDomain {

    public static final String FIELD_TEMPLATE_DTL_ID = "templateDtlId";
    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TEMPLATE_URL = "templateUrl";
    public static final String FIELD_TEMPLATE_CONTENT = "templateContent";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 验证模板明细唯一性
     *
     * @param templateDtlRepository 仓库
     */
    public void validateRepeat(TemplateDtlRepository templateDtlRepository) {
        TemplateDtl record = new TemplateDtl();
        record.setTemplateId(templateId);
        record.setLang(lang);
        TemplateDtl dtl = templateDtlRepository.selectOneOptional(record, new Criteria().select(FIELD_TEMPLATE_DTL_ID));
        if (dtl != null && !Objects.equals(templateDtlId, dtl.templateDtlId)) {
            throw new CommonException(HrptMessageConstants.ERROR_TEMPLATE_LANG_REPEAT);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long templateDtlId;
    @ApiModelProperty(value = "报表模板ID,hrpt_template.template_id")
    @NotNull
    @Where
    @Encrypt
    private Long templateId;
    @ApiModelProperty(value = "模板路径")
    @Size(max = 480)
    private String templateUrl;
    @ApiModelProperty(value = "模板内容")
    private String templateContent;
    @ApiModelProperty(value = "模板语言")
    @NotBlank
    @Where
    private String lang;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String templateFileName;
    @Transient
    private String templateCode;
    @Transient
    private String templateTypeCode;
    @Transient
    private int defaultFlag;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getTemplateDtlId() {
        return templateDtlId;
    }

    public TemplateDtl setTemplateDtlId(Long templateDtlId) {
        this.templateDtlId = templateDtlId;
        return this;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public TemplateDtl setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public TemplateDtl setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
        return this;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public TemplateDtl setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TemplateDtl setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public TemplateDtl setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public TemplateDtl setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public TemplateDtl setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public TemplateDtl setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
        return this;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public TemplateDtl setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }
}
