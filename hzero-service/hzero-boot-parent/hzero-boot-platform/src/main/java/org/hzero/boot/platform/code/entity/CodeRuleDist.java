package org.hzero.boot.platform.code.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 编码规则分配
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 13:43
 */
@ApiModel("编码规则分配")
public class CodeRuleDist {

    public static final String FIELD_RULE_DIST_ID = "ruleDistId";

    public static final String FIELD_RULE_ID = "ruleId";

    public static final String FIELD_USED_FLAG = "usedFlag";

    public static final String FIELD_LEVEL_CODE = "levelCode";

    public static final String FIELD_LEVEL_VALUE = "levelValue";

    /**
     * 判断当前编码规则是否使用过
     *
     * @return boolean
     */
    public boolean judgeIsUsed() {
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

    private Date creationDate;
    private Long createdBy;
    private Date lastUpdateDate;
    private Long lastUpdatedBy;

    @Id
    @GeneratedValue
    @ApiModelProperty("编码规则分配ID")
    private Long ruleDistId;

    @NotNull
    @ApiModelProperty("编码规则ID")
    private Long ruleId;

    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("应用层级")
    private String levelCode;

    @NotBlank
    @ApiModelProperty("应用层级值")
    private String levelValue;

    @ApiModelProperty("使用标识")
    private Integer usedFlag;

    @NotNull
    @ApiModelProperty("启用标识")
    private Integer enabledFlag;

    @ApiModelProperty("版本号")
    private Long objectVersionNumber;

    @ApiModelProperty("描述")
    private String description;

    @Transient
    @ApiModelProperty(value = "编码规则详情", hidden = true)
    private List<CodeRuleDetail> ruleDetailList;


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

    public CodeRuleDist setRuleDetailList(List<CodeRuleDetail> ruleDetailList) {
        this.ruleDetailList = ruleDetailList;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @JsonIgnore
    public Date getCreationDate() {
        return this.creationDate;
    }

    @JsonIgnore
    public Long getCreatedBy() {
        return this.createdBy;
    }

    @JsonIgnore
    public Date getLastUpdateDate() {
        return this.lastUpdateDate;
    }

    @JsonIgnore
    public Long getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public CodeRuleDist setRuleDistId(Long ruleDistId) {
        this.ruleDistId = ruleDistId;
        return this;
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
