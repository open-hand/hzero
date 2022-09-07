package org.hzero.platform.domain.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.CodeRuleRepository;
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
 * 编码规则头
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 14:06
 */
@ApiModel
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_code_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class CodeRule extends AuditDomain {

    public static final String RULE_ID = "ruleId";

    public static final String RULE_CODE = "ruleCode";

    public static final String TENANT_ID = "tenantId";

    /**
     * 注入租户id
     *
     * @param tenantId     租户id
     * @param codeRuleList 编码规则list
     */
    public static void injectTenantId(Long tenantId, List<CodeRule> codeRuleList) {
        if (CollectionUtils.isNotEmpty(codeRuleList)) {
            codeRuleList.forEach(codeRule -> codeRule.setTenantId(tenantId));
        }
    }


    /**
     * 判断数据的合法性，就是判断当前操作的数据是否是属于当前租户的
     *
     * @param codeRuleRepository 编码规则资源
     * @param tenantId           租户id
     * @param ruleId             编码规则id
     * @return boolean
     */
    public static boolean judgeDataLegality(CodeRuleRepository codeRuleRepository, Long tenantId, Long ruleId) {
        // 平台级的不用判断数据合法性
        if (tenantId == null || tenantId.equals(BaseConstants.DEFAULT_TENANT_ID)) {
            return true;
        }
        CodeRule codeRule = new CodeRule();
        codeRule.setTenantId(tenantId);
        codeRule.setRuleId(ruleId);
        return CollectionUtils.isNotEmpty(codeRuleRepository.select(codeRule));
    }

    /**
     * 判断更新还是新增
     *
     * @return boolean
     */
    public boolean judgeInsert() {
        return ruleId == null;
    }


    /**
     * 生成redis缓存的key值
     *
     * @param level       应用维度
     * @param tenantId    租户id
     * @param ruleCode    编码规则
     * @param levelCode   应用层级
     * @param levelValue  应用层级值
     * @return key值
     */
    public static String generateCacheKey(String level, Long tenantId, String ruleCode, String levelCode,
                                          String levelValue) {
        return CodeConstants.CacheKey.CODE_RULE_KEY + ":" + level + "." + tenantId +
                "." + ruleCode + "." + levelCode + "." +
                levelValue;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("规则ID")
    @Encrypt
    private Long ruleId;

    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("编码规则CODE")
    @Length(max = 30)
    private String ruleCode;

    @NotBlank
    @ApiModelProperty("编码规则名")
    @Length(max = 60)
    @MultiLanguageField
    private String ruleName;

    @NotBlank
    @ApiModelProperty("应用维度")
    @Length(max = 30)
    private String ruleLevel;

    @NotNull
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;

    @ApiModelProperty("描述")
    @Length(max = 240)
    private String description;

    @ApiModelProperty("版本号")
    private Long objectVersionNumber;

    @Transient
    @ApiModelProperty(value = "编码规则分配", hidden = true)
    private List<CodeRuleDist> ruleDistList;

    @Transient
    @JsonIgnore
    @Range(max = 1)
    private Integer ignoreEmptyLine;

    /**
     * @return 编码规则id
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * @return 编码规则code
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * @return 编码规则名
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * @return 应用维度，有全局级和租户级
     */
    public String getRuleLevel() {
        return ruleLevel;
    }

    /**
     * @return 当应用层级为租户级时，此字段必输，值为租户id
     */
    public Long getTenantId() {
        return tenantId;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 编码规则分配list
     */
    public List<CodeRuleDist> getRuleDistList() {
        return ruleDistList;
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

    public void setRuleDistList(List<CodeRuleDist> ruleDistList) {
        this.ruleDistList = ruleDistList;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public CodeRule setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public void setRuleLevel(String ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public CodeRule setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIgnoreEmptyLine() {
        return ignoreEmptyLine;
    }

    public void setIgnoreEmptyLine(Integer ignoreEmptyLine) {
        this.ignoreEmptyLine = ignoreEmptyLine;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


}
