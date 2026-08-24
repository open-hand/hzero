package org.hzero.platform.domain.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.PromptRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.helper.LanguageHelper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 多语言描述
 *
 * @author yunxiang.zhou01@hand-china.com 2018-06-21 16:02:18
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_prompt")
@ApiModel("多语言描述")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Prompt extends AuditDomain {

    public static final String FIELD_PROMPT_ID = "promptId";
    public static final String FIELD_PROMPT_CODE = "promptCode";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";


    /**
     * 设置租户id
     *
     * @param tenantId 租户id
     * @param promptList 多语言list
     */
    public static void injectTenantId(Long tenantId, List<Prompt> promptList) {
        if (CollectionUtils.isNotEmpty(promptList)) {
            promptList.forEach(prompt -> prompt.setTenantId(tenantId));
        }
    }

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验必输参数
     */
    public void validParams() {
        if (promptKey == null || promptCode == null || promptKey.length() > 60 || promptCode.length() > 240) {
            // 参数校验失败
            throw new CommonException(HpfmMsgCodeConstants.ERROR_PROMPT_PARAM_NOT_NULL, promptKey, promptCode);
        }
        if (MapUtils.isEmpty(promptConfigs) || !promptConfigs.containsKey(LanguageHelper.getDefaultLanguage())) {
            // 数据校验不合法
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        promptConfigs.forEach((key, value) -> {
            if (key.length() > 30 || value.length() > 750) {
                // 字段长度超长
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
        });
    }

    /**
     * 校验数据是否有效
     */
    public void checkDataLegalization(PromptRepository promptRepository) {
        Prompt prompt = promptRepository.selectByPrimaryKey(this.promptId);
        Assert.notNull(prompt, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (!Objects.equals(this.tenantId, prompt.getTenantId())) {
            // 在租户级编辑了预定义的数据，新增一条该数据到当前租户下，需要设置当前主键id以及版本号为null
            this.promptId = null;
            this.setObjectVersionNumber(null);
        }
    }

    /**
     * 删除redis
     *
     * @param redisHelper redisHelper
     * @param prompt 删除的多语言标签
     */
    public static void deleteCache(RedisHelper redisHelper, Prompt prompt) {
        String cacheKey = Prompt.generateCacheKey(prompt.getPromptKey(), prompt.getLang(), prompt.getTenantId());
        String mapKey = Prompt.generateMapKey(prompt.getPromptKey(), prompt.getPromptCode());
        redisHelper.hshDelete(cacheKey, mapKey);
    }

    /**
     * 刷新redis缓存
     *
     * @param redisHelper redisHelper
     * @param prompt 修改的多语言标签
     * @param oldPrompt 原先的多语言标签
     */
    public static void refreshCache(RedisHelper redisHelper, Prompt prompt, Prompt oldPrompt) {
        String oldCacheKey =
                        Prompt.generateCacheKey(oldPrompt.getPromptKey(), oldPrompt.getLang(), oldPrompt.getTenantId());
        String oldMapKey = Prompt.generateMapKey(oldPrompt.getPromptKey(), oldPrompt.getPromptCode());
        String cacheKey = Prompt.generateCacheKey(prompt.getPromptKey(), prompt.getLang(), prompt.getTenantId());
        String mapKey = Prompt.generateMapKey(prompt.getPromptKey(), prompt.getPromptCode());

        redisHelper.hshDelete(oldCacheKey, oldMapKey);
        redisHelper.hshPut(cacheKey, mapKey, prompt.getDescription());
    }

    /**
     * 将多语言标签初始化到redis
     *
     * @param redisHelper redisHelper
     * @param prompt 多语言标签
     */
    public static void createCache(RedisHelper redisHelper, Prompt prompt) {
        String cacheKey = Prompt.generateCacheKey(prompt.getPromptKey(), prompt.getLang(), prompt.getTenantId());
        String mapKey = Prompt.generateMapKey(prompt.getPromptKey(), prompt.getPromptCode());
        redisHelper.hshPut(cacheKey, mapKey, prompt.getDescription());
    }

    /**
     * 生成缓存key
     *
     * @param promptKey 多语言key
     * @param lang 语言
     * @return key
     */
    public static String generateCacheKey(String promptKey, String lang, Long tenantId) {
        StringBuilder sb = new StringBuilder();
        return sb.append(FndConstants.CacheKey.PROMPT_KEY).append(":").append(promptKey).append(".").append(lang)
                        .append(".").append(tenantId).toString();
    }

    /**
     * 生成多语言标签缓存map中的key
     *
     * @param promptKey 多语言key
     * @param promptCode 多语言code
     * @return map的key
     */
    public static String generateMapKey(String promptKey, String promptCode) {
        return promptKey + "." + promptCode;
    }

    /**
     * 判断当前对象是新增还是更新
     *
     * @return boolean
     */
    public boolean judgeInsert() {
        return promptId == null;
    }

    /**
     * 判断是否是租户级数据
     *
     * @return boolean
     */
    public boolean judegeTenant() {
        return tenantId != 0;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("多语言描述ID")
    @Encrypt
    private Long promptId;
    @NotBlank
    @ApiModelProperty("多语言KEY")
    @Pattern(regexp = Regexs.CODE)
    @Length(max = 60, groups = {Insert.class})
    private String promptKey;
    @NotBlank
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty("多语言CODE")
    @Length(max = 240, groups = {Insert.class})
    private String promptCode;
    @NotBlank
    @ApiModelProperty("语言")
    private String lang;
    @NotBlank
    @ApiModelProperty("描述")
    @Length(max = 750, groups = {Insert.class, Update.class})
    private String description;
    @ApiModelProperty("租户ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    @ApiModelProperty("多语言描述配置")
    private Map<String, String> promptConfigs;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Map<String, String> getPromptConfigs() {
        return promptConfigs;
    }

    public void setPromptConfigs(Map<String, String> promptConfigs) {
        this.promptConfigs = promptConfigs;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getPromptId() {
        return promptId;
    }

    public void setPromptId(Long promptId) {
        this.promptId = promptId;
    }

    /**
     * @return 多语言code
     */
    public String getPromptCode() {
        return promptCode;
    }

    public void setPromptCode(String promptCode) {
        this.promptCode = promptCode;
    }

    /**
     * @return 语言
     */
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 多语言key
     */
    public String getPromptKey() {
        return promptKey;
    }

    public void setPromptKey(String promptKey) {
        this.promptKey = promptKey;
    }

    /**
     * @return 租户id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    public Prompt() {}

    public Prompt(String promptKey, String promptCode, String lang, String description, Long tenantId) {
        this.promptKey = promptKey;
        this.promptCode = promptCode;
        this.lang = lang;
        this.description = description;
        this.tenantId = tenantId;
    }

    public Prompt(Long promptId, String promptKey, String promptCode, String lang, String description, Long tenantId) {
        this.promptId = promptId;
        this.promptKey = promptKey;
        this.promptCode = promptCode;
        this.lang = lang;
        this.description = description;
        this.tenantId = tenantId;
    }

    public interface Insert {

    }

    public interface Update {

    }

}
