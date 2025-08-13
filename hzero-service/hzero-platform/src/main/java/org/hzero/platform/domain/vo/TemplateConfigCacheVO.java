package org.hzero.platform.domain.vo;

/**
 * 模板配置缓存使用VO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/10 13:46
 */
public class TemplateConfigCacheVO {

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置值
     */
    private String configValue;
    /**
     * 来源类型
     */
    private String sourceType;
    /**
     * 二级域名
     */
    private String domainUrl;

    /**
     * 模板代码
     */
    private String templateCode;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getDomainUrl() {
        return domainUrl;
    }

    public void setDomainUrl(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
