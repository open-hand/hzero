package org.hzero.platform.domain.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import io.choerodon.mybatis.domain.AuditDomain;
import javax.validation.constraints.NotBlank;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 编码规则分配
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 13:43
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_code_rule_dist")
@ApiModel("编码规则分配")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeRuleDist extends AuditDomain {

    public static final String FIELD_RULE_DIST_ID = "ruleDistId";

    public static final String FIELD_RULE_ID = "ruleId";

    public static final String FIELD_USED_FLAG = "usedFlag";

    public static final String FIELD_LEVEL_CODE = "levelCode";

    public static final String FIELD_LEVEL_VALUE = "levelValue";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    public interface Insert{}
    /**
     * 判断当前编码规则是否使用过
     *
     * @return boolean
     */
    public boolean judgeIsUsed(){
        return BaseConstants.Flag.YES.equals(usedFlag);
    }

    /**
     * 用于组装生成编码规则是否使用过的key
     */
    public static final String USED_FLAG = "USED";

    /**
     * 判断更新还是新增
     *
     * @return boolean
     */
    public boolean judgeInsert() {
        return ruleDistId == null;
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("编码规则分配ID")
    @Encrypt
    private Long ruleDistId;

    @NotNull
    @ApiModelProperty("编码规则ID")
    @Encrypt
    private Long ruleId;

    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("应用层级")
    @Length(max = 30)
    private String levelCode;

    @NotBlank
    @ApiModelProperty("应用层级值")
    @Length(max = 30)
    private String levelValue;

    @ApiModelProperty("使用标识")
    @Range(max = 1)
    private Integer usedFlag;

    @NotNull
    @ApiModelProperty("启用标识")
    @Range(max = 1)
    private Integer enabledFlag;

    @ApiModelProperty("版本号")
    private Long objectVersionNumber;

    @ApiModelProperty("描述")
    @Length(max = 240)
    private String description;

    @Transient
    @ApiModelProperty(value = "编码规则详情",hidden = true)
    private List<CodeRuleDetail> ruleDetailList;

    @NotNull(groups = Insert.class)
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 规则分配id
     */
    public Long getRuleDistId() {
        return ruleDistId;
    }

    /**
     * @return 规则id
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * @return 应用层级
     */
    public String getLevelCode() {
        return levelCode;
    }

    /**
     * @return 应用层级值
     */
    public String getLevelValue() {
        return levelValue;
    }

    /**
     * @return 是否使用过
     */
    public Integer getUsedFlag() {
        return usedFlag;
    }

    /**
     * @return 是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 编码规则明细
     */
    public List<CodeRuleDetail> getRuleDetailList() {
        return ruleDetailList;
    }

    public CodeRuleDist setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public CodeRuleDist setDescription(String description) {
        this.description = description;
        return this;
    }

    public void setRuleDetailList(List<CodeRuleDetail> ruleDetailList) {
        this.ruleDetailList = ruleDetailList;
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

    public void setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
    }

    public CodeRuleDist setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public CodeRuleDist setLevelCode(String levelCode) {
        this.levelCode = levelCode;
        return this;
    }

    public CodeRuleDist setLevelValue(String levelValue) {
        this.levelValue = levelValue;
        return this;
    }

    public CodeRuleDist setUsedFlag(Integer usedFlag) {
        this.usedFlag = usedFlag;
        return this;
    }
}
