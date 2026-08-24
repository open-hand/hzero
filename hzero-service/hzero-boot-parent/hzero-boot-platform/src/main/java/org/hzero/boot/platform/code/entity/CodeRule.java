package org.hzero.boot.platform.code.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.util.Regexs;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 编码规则头
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 14:06
 */
@ApiModel
public class CodeRule {
    private Date creationDate;
    private Long createdBy;
    private Date lastUpdateDate;
    private Long lastUpdatedBy;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @ApiModelProperty("规则ID")
    private Long ruleId;

    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("编码规则CODE")
    @Length(max = 30)
    private String ruleCode;

    @NotBlank
    @ApiModelProperty("编码规则名")
    private String ruleName;

    @NotBlank
    @ApiModelProperty("应用维度")
    private String ruleLevel;

    @NotNull
    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("描述")
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

    public CodeRule setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public CodeRule setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public CodeRule setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public CodeRule setRuleLevel(String ruleLevel) {
        this.ruleLevel = ruleLevel;
        return this;
    }

    public CodeRule setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public CodeRule setDescription(String description) {
        this.description = description;
        return this;
    }

    public CodeRule setRuleDistList(List<CodeRuleDist> ruleDistList) {
        this.ruleDistList = ruleDistList;
        return this;
    }

    public Integer getIgnoreEmptyLine() {
        return ignoreEmptyLine;
    }

    public CodeRule setIgnoreEmptyLine(Integer ignoreEmptyLine) {
        this.ignoreEmptyLine = ignoreEmptyLine;
        return this;
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
