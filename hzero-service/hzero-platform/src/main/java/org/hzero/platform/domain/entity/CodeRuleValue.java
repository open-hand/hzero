package org.hzero.platform.domain.entity;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 编码规则明细值
 *
 * @author zhiying.dong@hand-china.com 2019-01-08 19:12:55
 */
@ApiModel("编码规则明细值")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_code_rule_value")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeRuleValue extends AuditDomain {

    public static final String FIELD_RULE_VALUE_ID = "ruleValueId";
    public static final String FIELD_RULE_DETAIL_ID = "ruleDetailId";
    public static final String FIELD_LEVEL_VALUE = "levelValue";
    public static final String FIELD_CURRENT_VALUE = "currentValue";
    public static final String FIELD_RULE_LEVEL = "ruleLevel";
	public static final String FIELD_RESET_DATE = "resetDate";

	public interface Insert{}

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
	@Encrypt
    private Long ruleValueId;
    @ApiModelProperty(value = "hpfm_code_rule_detail.rule_detail_id",required = true)
    @NotNull
    private Long ruleDetailId;
    @NotBlank
    private String ruleLevel;
    @ApiModelProperty(value = "应用层级值",required = true)
    @NotBlank
    private String levelValue;
    @ApiModelProperty(value = "当前值",required = true)
    @NotNull
    private Long currentValue;
    private Date resetDate;
	@NotNull(groups = Insert.class)
	private Long tenantId;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

	public Long getTenantId() {
		return tenantId;
	}

	public CodeRuleValue setTenantId(Long tenantId) {
		this.tenantId = tenantId;
		return this;
	}

	/**
     * @return 表ID，主键，供其他表做外键
     */
	public Long getRuleValueId() {
		return ruleValueId;
	}

	public CodeRuleValue setRuleValueId(Long ruleValueId) {
		this.ruleValueId = ruleValueId;
		return this;
	}

	/**
     * @return hpfm_code_rule_detail.rule_detail_id
     */
	public Long getRuleDetailId() {
		return ruleDetailId;
	}

	public CodeRuleValue setRuleDetailId(Long ruleDetailId) {
		this.ruleDetailId = ruleDetailId;
		return this;
	}

	public String getRuleLevel() {
		return ruleLevel;
	}

	public CodeRuleValue setRuleLevel(String ruleLevel) {
		this.ruleLevel = ruleLevel;
		return this;
	}

	/**
     * @return 应用层级值
     */
	public String getLevelValue() {
		return levelValue;
	}

	public CodeRuleValue setLevelValue(String levelValue) {
		this.levelValue = levelValue;
		return this;
	}

	/**
     * @return 当前值
     */
	public Long getCurrentValue() {
		return currentValue;
	}

	public CodeRuleValue setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
		return this;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public CodeRuleValue setResetDate(Date resetDate) {
		this.resetDate = resetDate;
		return this;
	}
}
