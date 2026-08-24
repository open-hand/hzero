package org.hzero.boot.platform.common.infra.feign.vo;

import java.util.Map;

/**
 * 渲染参数对象
 *
 * @author bergturing 2020/08/05 16:21
 */
public class RenderParameterVO {
    /**
     * 租户ID
     */
    private final Long tenantId;

    /**
     * 通用模板编码
     */
    private final String templateCode;

    /**
     * 语言
     */
    private final String lang;

    /**
     * 通用模板参数
     */
    private final Map<String, Object> args;

    public RenderParameterVO(Long tenantId, String templateCode, String lang, Map<String, Object> args) {
        this.tenantId = tenantId;
        this.templateCode = templateCode;
        this.lang = lang;
        this.args = args;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getLang() {
        return lang;
    }

    public Map<String, Object> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return "RenderParameterVO{" +
                "tenantId=" + tenantId +
                ", templateCode='" + templateCode + '\'' +
                ", lang='" + lang + '\'' +
                ", args=" + args +
                '}';
    }
}
