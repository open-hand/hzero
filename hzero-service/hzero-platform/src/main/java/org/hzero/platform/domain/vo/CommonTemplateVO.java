package org.hzero.platform.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.springframework.beans.BeanUtils;

/**
 * 通用模板缓存对象
 *
 * @author bergturing 2020/08/04 16:40
 */
public class CommonTemplateVO {
    /**
     * 缓存key模板: hpfm:common-template:{{tenantId}}:{{lang}}
     */
    private static final String CACHE_KEY_TEMPLATE = "hpfm:common-template:%s:%s";
    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板分类.HPFM.TEMPLATE_CATEGORY
     */
    private String templateCategoryCode;

    /**
     * 模板内容
     */
    private String templateContent;

    /**
     * 语言
     */
    private String lang;

    /**
     * 租户ID,hpfm_tenant.tenant_id
     */
    private Long tenantId;

    /**
     * 是否启用。1启用，0未启用
     */
    private Integer enabledFlag;

    /**
     * 初始化通用模板缓存对象
     *
     * @param commonTemplate 通用模板实体对象
     * @return 通用模板缓存的对象
     */
    public static CommonTemplateVO init(CommonTemplate commonTemplate) {
        // 缓存对象
        CommonTemplateVO commonTemplateVO = new CommonTemplateVO();

        // 拷贝属性
        BeanUtils.copyProperties(commonTemplate, commonTemplateVO);

        // 返回结果
        return commonTemplateVO;
    }

    /**
     * 生成缓存key
     *
     * @param tenantId 租户ID
     * @param lang     语言
     * @return 缓存key
     */
    public static String generateCacheKey(Long tenantId, String lang) {
        return String.format(CACHE_KEY_TEMPLATE, tenantId, lang);
    }

    /**
     * 获取缓存key
     *
     * @return 缓存key
     */
    @JsonIgnore
    public String getCacheKey() {
        return generateCacheKey(this.tenantId, this.lang);
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public String toString() {
        return "CommonTemplateCacheVO{" +
                "templateCode='" + templateCode + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateCategoryCode='" + templateCategoryCode + '\'' +
                ", templateContent='" + templateContent + '\'' +
                ", lang='" + lang + '\'' +
                ", tenantId=" + tenantId +
                ", enabledFlag=" + enabledFlag +
                '}';
    }
}
