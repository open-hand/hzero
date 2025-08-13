package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.report.domain.repository.ReportTemplateRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 报表模板关系
 *
 * @author xianzhi.chen@hand-china.com 2018-11-26 10:07:24
 */
@ApiModel("报表模板关系")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_report_template")
public class ReportTemplate extends AuditDomain {

    public static final String FIELD_REPORT_TEMPLATE_ID = "reportTemplateId";
    public static final String FIELD_REPORT_ID = "reportId";
    public static final String FIELD_TEMPLATE_ID = "templateId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 报表模板重复性校验
     *
     * @param reportTemplateRepository 仓库
     * @return 是否重复
     */
    public boolean validateRepeat(ReportTemplateRepository reportTemplateRepository) {
        ReportTemplate rt = new ReportTemplate();
        rt.setTenantId(tenantId);
        rt.setReportId(reportId);
        rt.setTemplateId(templateId);
        int i = reportTemplateRepository.selectCount(rt);
        return i <= 0;
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long reportTemplateId;
    @ApiModelProperty(value = "报表ID，hrpt_report.report_id")
    @NotNull
    @Encrypt
    private Long reportId;
    @ApiModelProperty(value = "报表模板ID，hrpt_template.template_id")
    @NotNull
    @Encrypt
    private Long templateId;
    @ApiModelProperty(value = "默认标识")
    private int defaultFlag;
    @ApiModelProperty("租户ID")
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @LovValue(value = "HRPT.TEMPLATE_TYPE", meaningField = "templateTypeMeaning")
    @Transient
    private String templateTypeCode;
    @Transient
    private String templateCode;
    @Transient
    private String templateName;
    @Transient
    private String lang;
    @Transient
    private String templateTypeMeaning;
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getReportTemplateId() {
        return reportTemplateId;
    }

    public ReportTemplate setReportTemplateId(Long reportTemplateId) {
        this.reportTemplateId = reportTemplateId;
        return this;
    }

    public Long getReportId() {
        return reportId;
    }

    public ReportTemplate setReportId(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public ReportTemplate setTemplateId(Long templateId) {
        this.templateId = templateId;
        return this;
    }

    public int getDefaultFlag() {
        return defaultFlag;
    }

    public ReportTemplate setDefaultFlag(int defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ReportTemplate setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTemplateTypeCode() {
        return templateTypeCode;
    }

    public ReportTemplate setTemplateTypeCode(String templateTypeCode) {
        this.templateTypeCode = templateTypeCode;
        return this;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public ReportTemplate setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    public String getTemplateName() {
        return templateName;
    }

    public ReportTemplate setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public ReportTemplate setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public ReportTemplate setTemplateTypeMeaning(String templateTypeMeaning) {
        this.templateTypeMeaning = templateTypeMeaning;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public ReportTemplate setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }
}
