package org.hzero.platform.domain.entity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.boot.platform.rule.entity.RuleScriptVO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.RuleScriptRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 规则脚本
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-27 16:22:55
 */
@ApiModel("规则脚本")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_rule_script")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleScript extends AuditDomain {

    public static final String FIELD_RULE_SCRIPT_ID = "ruleScriptId";
    public static final String FIELD_SERVER_NAME = "serverName";
    public static final String FIELD_SCRIPT_CODE = "scriptCode";
    public static final String FIELD_SCRIPT_DESCRIPTION = "scriptDescription";
    public static final String FIELD_SCRIPT_TYPE_CODE = "scriptTypeCode";
    public static final String FIELD_SCRIPT_CONTENT = "scriptContent";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     *
     * @param repository 仓库
     */
    public void validateRepeat(RuleScriptRepository repository) {
        RuleScript ruleScript = new RuleScript();
        ruleScript.setTenantId(tenantId);
        ruleScript.setScriptCode(scriptCode);
        Assert.isTrue(repository.selectCount(ruleScript) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
    }

    /**
     * 校验数据是否有效
     */
    public void checkDataLegalization(RuleScriptRepository ruleScriptRepository) {
        RuleScript ruleScript = ruleScriptRepository.selectByPrimaryKey(this.ruleScriptId);
        Assert.notNull(ruleScript, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (!Objects.equals(ruleScript.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_RULE_SCRIPT_TENANT_NOT_MATCH);
        }
    }

    //缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId   租户Id
     * @param scriptCode 脚本编码
     * @return key
     */
    private static String getCacheKey(Long tenantId, String scriptCode) {
        return HZeroService.Platform.CODE + ":ruleScripts:" + scriptCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper  redis
     * @param tenantId     租户Id
     * @param scriptCode   脚本编码
     * @param ruleScriptVO 规则脚本DTO
     */
    public static void refreshCache(RedisHelper redisHelper, Long tenantId, String scriptCode, RuleScriptVO ruleScriptVO) {
        clearRedisCache(redisHelper, tenantId, scriptCode);
        if (ruleScriptVO != null) {
            redisHelper.strSet(getCacheKey(tenantId, scriptCode), redisHelper.toJson(ruleScriptVO));
        } else {
            redisHelper.strSet(getCacheKey(tenantId, scriptCode), "error");
        }
        redisHelper.setExpire(getCacheKey(tenantId, scriptCode), 30, TimeUnit.DAYS);
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param scriptCode  脚本编码
     * @param tenantId    租户Id
     */
    public static RuleScript getCache(RedisHelper redisHelper, Long tenantId, String scriptCode) {
        return redisHelper.fromJson(redisHelper.strGet(getCacheKey(tenantId, scriptCode)), RuleScript.class);
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param scriptCode  脚本编码
     */
    public static void clearRedisCache(RedisHelper redisHelper, Long tenantId, String scriptCode) {
        redisHelper.delKey(getCacheKey(tenantId, scriptCode));
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long ruleScriptId;
    @ApiModelProperty(value = "服务名称 LOV：HPFM.RULE_SCRIPT.SERVICE_ROUTE")
    @NotBlank
    @Length(max = 60)
    private String serverName;
    @ApiModelProperty(value = "脚本编码")
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    private String scriptCode;
    @Length(max = 240)
    @ApiModelProperty(value = "脚本描述")
    @NotBlank
    @MultiLanguageField
    private String scriptDescription;
    @ApiModelProperty(value = "脚本类型，HPFM.RULE_SCRIPT_TYPE")
    @NotBlank
    @LovValue(lovCode = "HPFM.RULE_SCRIPT_TYPE")
    @Length(max = 20)
    private String scriptTypeCode;
    @ApiModelProperty(value = "脚本内容")
    @NotBlank
    private String scriptContent;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "是否启用。1启用，0未启用")
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "脚本分类")
    @Length(max = 30)
    @LovValue(lovCode = "HPFM.RULE_SCRIPT_CATEGORY")
    private String category;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String scriptTypeMeaning;
    @Transient
    private String categoryMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRuleScriptId() {
        return ruleScriptId;
    }

    public void setRuleScriptId(Long ruleScriptId) {
        this.ruleScriptId = ruleScriptId;
    }

    /**
     * @return 服务名称
     */
    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return 脚本编码
     */
    public String getScriptCode() {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode) {
        this.scriptCode = scriptCode;
    }

    /**
     * @return 脚本描述
     */
    public String getScriptDescription() {
        return scriptDescription;
    }

    public void setScriptDescription(String scriptDescription) {
        this.scriptDescription = scriptDescription;
    }

    /**
     * @return 脚本类型，HPFM.RULE_SCRIPT_TYPE
     */
    public String getScriptTypeCode() {
        return scriptTypeCode;
    }

    public void setScriptTypeCode(String scriptTypeCode) {
        this.scriptTypeCode = scriptTypeCode;
    }

    /**
     * @return 脚本内容
     */
    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 是否启用。1启用，0未启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getScriptTypeMeaning() {
        return scriptTypeMeaning;
    }

    public void setScriptTypeMeaning(String scriptTypeMeaning) {
        this.scriptTypeMeaning = scriptTypeMeaning;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryMeaning() {
        return categoryMeaning;
    }

    public void setCategoryMeaning(String categoryMeaning) {
        this.categoryMeaning = categoryMeaning;
    }
}
