package org.hzero.platform.api.dto;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.Prompt;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 多语言标签DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/10 9:24
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromptDTO extends Prompt {

    /**
     * 生成一个表示当前DTO唯一的key
     *
     * @return key
     */
    public String generateKey(){
        return promptCode + promptKey + lang;
    }

    /**
     * 注入租户id
     *
     * @param promptDTOList
     * @param tenantId
     */
    public static void injectTenantId(List<PromptDTO> promptDTOList, Long tenantId) {
        if (CollectionUtils.isNotEmpty(promptDTOList)) {
            promptDTOList.forEach(promptDTO -> promptDTO.setTenantId(tenantId));
        }
    }

    /**
     * 校验多语言描述
     *
     * @param promptDTOList 多语言描述list
     */
    public static void validate(List<PromptDTO> promptDTOList) {
        if (CollectionUtils.isNotEmpty(promptDTOList)) {
            long tenantId = BaseConstants.DEFAULT_TENANT_ID;
            boolean isPromptTenant = true;
            for (PromptDTO promptDTO : promptDTOList) {
                // 判断要么都存在租户id，要么都不存在
                if (promptDTO.getTenantId() == null) {
                    isPromptTenant = false;
                }
                assert promptDTO.judgeTenantPrompt() || !isPromptTenant : BaseConstants.ErrorCode.DATA_INVALID;
                // 判断如果存在租户id，租户id是否都相等
                if (tenantId == BaseConstants.DEFAULT_TENANT_ID && promptDTO.judgeTenantPrompt()) {
                    tenantId = promptDTO.getTenantId();
                } else if (promptDTO.judgeTenantPrompt() && tenantId != promptDTO.getTenantId().longValue()) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
            }
        }
    }

    /**
     * 判断是否是租户级多语言描述
     *
     * @return boolean
     */
    public boolean judgeTenantPrompt() {
        return tenantId != null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId);
    }

    @Encrypt
    private Long promptId;
    private Long tenantId;
    private String promptKey;
    private String promptCode;
    private String lang;
    private String description;
    private String langDescription;
    private Long objectVersionNumber;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public Long getPromptId() {
        return promptId;
    }

    @Override
    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getPromptKey() {
        return promptKey;
    }

    @Override
    public void setPromptKey(String promptKey) {
        this.promptKey = promptKey;
    }

    @Override
    public String getPromptCode() {
        return promptCode;
    }

    @Override
    public void setPromptCode(String promptCode) {
        this.promptCode = promptCode;
    }

    @Override
    public String getLang() {
        return lang;
    }

    @Override
    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLangDescription() {
        return langDescription;
    }

    public void setLangDescription(String langDescription) {
        this.langDescription = langDescription;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PromptDTO [promptTenantId=");
        builder.append(", promptId=");
        builder.append(promptId);
        builder.append(", tenantId=");
        builder.append(tenantId);
        builder.append(", promptKey=");
        builder.append(promptKey);
        builder.append(", promptCode=");
        builder.append(promptCode);
        builder.append(", lang=");
        builder.append(lang);
        builder.append(", description=");
        builder.append(description);
        builder.append(", langDescription=");
        builder.append(langDescription);
        builder.append(", objectVersionNumber=");
        builder.append(objectVersionNumber);
        builder.append("]");
        return builder.toString();
    }
}
