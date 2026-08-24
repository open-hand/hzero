package org.hzero.platform.api.dto.commontemplate;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 通用模板创建数据传输对象
 *
 * @author bergturing 2020/08/04 11:16
 */
public class CommonTemplateCreationDTO {
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

    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;

    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;

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

    public String getTemplateCategoryCode() {
        return templateCategoryCode;
    }

    public void setTemplateCategoryCode(String templateCategoryCode) {
        this.templateCategoryCode = templateCategoryCode;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * 初始化通用模板创建实体对象
     *
     * @return 待创建的通用模板实体对象
     */
    public CommonTemplate initCreation() {
        // 待创建的通用模板实体对象
        CommonTemplate commonTemplate = new CommonTemplate();

        // 拷贝属性
        BeanUtils.copyProperties(this, commonTemplate);
        if (Objects.isNull(commonTemplate.getEnabledFlag())) {
            commonTemplate.setEnabledFlag(BaseConstants.Flag.YES);
        }
        if (Objects.isNull(commonTemplate.getTenantId())) {
            commonTemplate.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }

        // 返回实体对象
        return commonTemplate;
    }

    @Override
    public String toString() {
        return "CommonTemplateCreationDTO{" +
                "templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateCategoryCode='" + templateCategoryCode + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", lang='" + lang + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                '}';
    }
}
