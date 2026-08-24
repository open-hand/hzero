package org.hzero.platform.api.dto.commontemplate;

import java.util.Map;

/**
 * 渲染参数DTO
 *
 * @author bergturing 2020/08/05 16:21
 */
public class RenderParameterDTO {
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 通用模板编码
     */
    private String templateCode;

    /**
     * 语言
     */
    private String lang;

    /**
     * 通用模板参数
     */
    private Map<String, Object> args;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RenderParameterDTO{" +
                "tenantId=" + tenantId +
                ", templateCode='" + templateCode + '\'' +
                ", lang='" + lang + '\'' +
                ", args=" + args +
                '}';
    }
}
