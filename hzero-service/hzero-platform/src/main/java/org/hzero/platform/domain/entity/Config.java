package org.hzero.platform.domain.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.common.HZeroConstant;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.vo.TitleConfigCacheVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 系统配置
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/19 11:32
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_config")
@ApiModel("系统配置")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Config extends AuditDomain {

    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CONFIG_VALUE = "configValue";
    /*用于处理系统配置标题多语言内容*/
    public static final String FIELD_DB_CONFIG_VALUE = "config_value";
    public static final String FIELD_DB_LANG= "lang";


    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     */
    public void refreshCache(RedisHelper redisHelper){
        String key = Config.generateCacheKey(configCode, tenantId);
        if (Constants.CONFIG_CODE_TITLE.equals(configCode)) {
            Map<String, String> resultMap = new HashMap<>(languageValue.size());
            languageValue.forEach(map ->{
                String lang = map.getLang();
                String dbConfigValue = map.getConfigValue();
                resultMap.put(lang, dbConfigValue);
            });
            // 系统标题类型，需缓存多语言内容
            if (MapUtils.isNotEmpty(resultMap)) {
                String cacheValue = redisHelper.toJson(resultMap);
                // 缓存标题
                redisHelper.strSet(key, cacheValue);
            }
        } else {
            // 其它类型自行缓存即可
            redisHelper.strSet(key, configValue);
        }
    }

    /**
     * 注入租户id
     *
     * @param configList 系统配置list
     * @param tenantId 租户id
     */
    public static void injectTenantId(List<Config> configList, Long tenantId) {
        if (CollectionUtils.isNotEmpty(configList)) {
            configList.forEach(config -> config.setTenantId(tenantId));
        }
    }

    /**
     * 生成系统配置的redis缓存key
     *
     * @param configCode 系统配置code
     * @return key
     */
    public static String generateCacheKey(String configCode, Long tenantId) {
        StringBuilder sb = new StringBuilder();
        return sb.append(FndConstants.CacheKey.CONFIG_KEY).append(":").append(configCode).append(".").append(tenantId)
                        .toString();
    }

    // ===============================================================================
    // getter setter
    // ===============================================================================

    @Id
    @GeneratedValue
    @ApiModelProperty("系统配置ID")
    @Encrypt
    private Long configId;
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("系统配置CODE")
    @Length(max = 30)
    private String configCode;
    @ApiModelProperty("系统配置值")
    @Length(max = 240)
    @MultiLanguageField
    private String configValue;
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty("分类")
    @Length(max = 30)
    private String category;
    @ApiModelProperty("版本号")
    private Long objectVersionNumber;
    @Transient
    private String fileName;
    @Transient
    @JsonIgnore
    private List<TitleConfigCacheVO> languageValue;

    public List<TitleConfigCacheVO> getLanguageValue() {
        return languageValue;
    }

    public void setLanguageValue(List<TitleConfigCacheVO> languageValue) {
        this.languageValue = languageValue;
    }

    /**
     * @return 主键id
     */
    public Long getConfigId() {
        return configId;
    }

    /**
     * @return 配置code
     */
    public String getConfigCode() {
        return configCode;
    }

    /**
     * @return 配置值
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * @return 配置类别
     */
    public String getCategory() {
        return category;
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
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
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

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getFileName() {
        if(HZeroConstant.Config.CONFIG_CODE_LOGO.equals(configCode)
                || Constants.CONFIG_CODE_FAVICON.equals(configCode)){
            return FileUtils.getFileName(configValue);
        }
        return null;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
