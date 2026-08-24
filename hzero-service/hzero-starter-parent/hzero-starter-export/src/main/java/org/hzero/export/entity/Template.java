package org.hzero.export.entity;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 通用模板头
 *
 * @author qingsheng.chen@hand-china.com 2019-01-24 16:04:37
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Template {

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void setColumnNameByLang() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            return;
        }
        String lang = customUserDetails.getLanguage();
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
    @ApiModelProperty("批量校验标识")
    private Integer batchCheckFlag;

    /**
     * 模板目标
     */
    @Valid
    @JsonProperty("templateTargetList")
    private List<TemplatePage> templatePageList;
    private String templateTypeMeaning;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getPrefixPatch() {
        return prefixPatch;
    }

    public void setPrefixPatch(String prefixPatch) {
        this.prefixPatch = prefixPatch;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

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

    public Integer getBatchCheckFlag() {
        return batchCheckFlag;
    }

    public void setBatchCheckFlag(Integer batchCheckFlag) {
        this.batchCheckFlag = batchCheckFlag;
    }

    public List<TemplatePage> getTemplatePageList() {
        return templatePageList;
    }

    public void setTemplatePageList(List<TemplatePage> templatePageList) {
        this.templatePageList = templatePageList;
    }

    public String getTemplateTypeMeaning() {
        return templateTypeMeaning;
    }

    public void setTemplateTypeMeaning(String templateTypeMeaning) {
        this.templateTypeMeaning = templateTypeMeaning;
    }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", templateType='" + templateType + '\'' +
                ", prefixPatch='" + prefixPatch + '\'' +
                ", description='" + description + '\'' +
                ", templateUrl='" + templateUrl + '\'' +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", batchCheckFlag=" + batchCheckFlag +
                ", templatePageList=" + templatePageList +
                ", templateTypeMeaning='" + templateTypeMeaning + '\'' +
                '}';
    }
}
