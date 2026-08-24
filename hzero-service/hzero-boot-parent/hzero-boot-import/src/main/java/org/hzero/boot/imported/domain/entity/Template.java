package org.hzero.boot.imported.domain.entity;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.collections4.CollectionUtils;

import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 通用模板头
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_TEMPLATE_CODE = "templateCode";
    public static final String FIELD_TEMPLATE_NAME = "templateName";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TEMPLATE_TYPE = "templateType";
    public static final String FIELD_PREFIX_PATCH = "prefixPatch";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void setColumnNameByLang() {
        String lang = DetailsHelper.getUserDetails().getLanguage();
        if (CollectionUtils.isEmpty(templatePageList)) {
            return;
        }
        for (TemplatePage sheet : templatePageList) {
            if (CollectionUtils.isEmpty(sheet.getTemplateColumnList())) {
                continue;
            }
            for (TemplateColumn column : sheet.getTemplateColumnList()) {
                List<TemplateColumnTl> columnTls = column.getTemplateColumnTls();
                if (CollectionUtils.isEmpty(columnTls)) {
                    continue;
                }
                columnTls.forEach(columnTl -> {
                    if (lang.equals(columnTl.getLang())) {
                        column.setColumnName(columnTl.getColumnName());
                    }
                });
            }
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    private Long id;
    @NotBlank
    private String templateCode;
    @NotBlank
    private String templateName;
    @NotNull
    private Integer enabledFlag;
    @NotBlank
    private String templateType;
    private String prefixPatch;
    private String description;
    private String templateUrl;
    @NotNull
    private Long tenantId;
    private String tenantName;
    private Integer fragmentFlag;

    /**
     * 模板目标
     */
    @Valid
    @JsonProperty("templateTargetList")
    private List<TemplatePage> templatePageList;
    private String templateTypeMeaning;

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getId() {
        return id;
    }

    public Template setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * @return 模板代码
     */
    public String getTemplateCode() {
        return templateCode;
    }

    public Template setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
        return this;
    }

    /**
     * @return 模板名
     */
    public String getTemplateName() {
        return templateName;
    }

    public Template setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Template setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 模板类型
     */
    public String getTemplateType() {
        return templateType;
    }

    public Template setTemplateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    /**
     * @return 客户端路径前缀
     */
    public String getPrefixPatch() {
        return prefixPatch;
    }

    public Template setPrefixPatch(String prefixPatch) {
        this.prefixPatch = prefixPatch;
        return this;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public Template setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public Template setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
        return this;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
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

    public Integer getFragmentFlag() {
        return fragmentFlag;
    }

    public Template setFragmentFlag(Integer fragmentFlag) {
        this.fragmentFlag = fragmentFlag;
        return this;
    }

    public List<TemplatePage> getTemplatePageList() {
        return templatePageList;
    }

    public Template setTemplatePageList(List<TemplatePage> templatePageList) {
        this.templatePageList = templatePageList;
        return this;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public Template setTemplateTypeMeaning(String templateTypeMeaning) {
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
}
