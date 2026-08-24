package org.hzero.platform.api.dto.commontemplate;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.platform.domain.entity.CommonTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * 通用模板数据更新数据传输对象
 *
 * @author bergturing 2020/08/04 11:19
 */
public class CommonTemplateUpdateDTO {
    @ApiModelProperty(value = "模板名称", required = true)
    @NotBlank
    private String templateName;

    @ApiModelProperty(value = "模板内容", required = true)
    @NotBlank
    private String templateContent;

    @ApiModelProperty(value = "语言", required = true)
    @NotBlank
    private String lang;

    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    private Integer enabledFlag;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    /**
     * 初始化更新数据对象
     *
     * @return 更新数据对象
     */
    public CommonTemplate initUpdate(CommonTemplate commonTemplate) {
        if (StringUtils.isNotBlank(this.templateName)) {
            commonTemplate.setTemplateName(this.templateName);
        }
        if (StringUtils.isNotBlank(this.templateContent)) {
            commonTemplate.setTemplateContent(this.templateContent);
        }
        if (StringUtils.isNotBlank(this.lang)) {
            commonTemplate.setLang(this.lang);
        }
        if (Objects.nonNull(this.enabledFlag)) {
            commonTemplate.setEnabledFlag(this.enabledFlag);
        }

        // 返回结果
        return commonTemplate;
    }

    @Override
    public String toString() {
        return "CommonTemplateUpdateDTO{" +
                "templateName='" + templateName + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", lang='" + lang + '\'' +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
