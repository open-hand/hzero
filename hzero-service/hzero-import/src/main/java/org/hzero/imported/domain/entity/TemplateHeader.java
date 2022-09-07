package org.hzero.imported.domain.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.common.query.Comparison;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;

/**
 * 通用模板头
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
@ApiModel("通用模板头")
@VersionAudit
@ModifyAudit
@Table(name = "himp_template_header")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class TemplateHeader extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TEMPLATE_TYPE = "templateType";
    public static final String FIELD_PREFIX_PATCH = "prefixPatch";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TEMPLATE_URL = "templateUrl";
    public static final String FIELD_FRAGMENT_FLAG = "fragmentFlag";
    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long id;
    @NotBlank
    @Where(comparison = Comparison.LIKE)
    @Pattern(regexp = Regexs.CODE)
    private String templateCode;
    @NotBlank
    @Where(comparison = Comparison.LIKE)
    @MultiLanguageField
    private String templateName;
    @NotNull
    private Integer enabledFlag;
    @NotBlank
    @LovValue(lovCode = "HIMP.TEMPLATE.TEMPLATETYPE")
    private String templateType;
    private String prefixPatch;
    private String description;
    private String templateUrl;
    @NotNull
    @Where
    @MultiLanguageField
    private Long tenantId;
    private Integer fragmentFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    /**
     * 模板目标
     */
    @Transient
    @Valid
    private List<TemplateTarget> templateTargetList;
    @Transient
    private String templateTypeMeaning;
    /**
     * 自定义模板文件名称
     */
    @Transient
    private String templateFileName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getId() {
        return id;
    }

    public TemplateHeader setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板代码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public TemplateHeader setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 模板名
     */
    public String getTemplateName() {
        return templateName;
    }

    public TemplateHeader setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public TemplateHeader setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 模板类型
     */
    public String getTemplateType() {
        return templateType;
    }

    public TemplateHeader setTemplateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    /**
     * @return 客户端路径前缀
     */
    public String getPrefixPatch() {
        return prefixPatch;
    }

    public TemplateHeader setPrefixPatch(String prefixPatch) {
        this.prefixPatch = prefixPatch;
        return this;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public TemplateHeader setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 自定义模板地址
     */
    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public TemplateHeader setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public List<TemplateTarget> getTemplateTargetList() {
        return templateTargetList;
    }

    public TemplateHeader setTemplateTargetList(List<TemplateTarget> templateTargetList) {
        this.templateTargetList = templateTargetList;
        return this;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public TemplateHeader setTemplateTypeMeaning(String templateTypeMeaning) {
        this.templateTypeMeaning = templateTypeMeaning;
        return this;
    }

    @JsonIgnore
    @Override
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @JsonIgnore
    @Override
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @JsonIgnore
    @Override
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @JsonIgnore
    @Override
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTemplateFileName() {
        return templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public Integer getFragmentFlag() {
        return fragmentFlag;
    }

    public TemplateHeader setFragmentFlag(Integer fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
        return this;
    }

    /**
     * 服务端导入
     */
    public interface ServerImport {
    }
}
