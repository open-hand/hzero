package org.hzero.boot.message.entity;

import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 消息发送基础参数
 *
 * @author shuangfei.zhu@hand-china.com 2019/11/18 17:03
 */
public class BaseSender {

    /**
     * 租户ID
     */
    @NotNull
    protected Long tenantId;
    /**
     * 消息发送配置代码/消息模板代码
     */
    @NotBlank
    protected String messageCode;
    /**
     * 语言
     */
    protected String lang;

    /**
     * 存放自定义参数
     */
    protected Map<String, Object> additionalInformation;

    public Long getTenantId() {
        return tenantId;
    }

    public BaseSender setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public BaseSender setMessageCode(String messageCode) {
        this.messageCode = messageCode;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public BaseSender setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

    public BaseSender setAdditionalInformation(Map<String, Object> additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }
}
