package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.core.exception.CommonException;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import org.hzero.core.util.Regexs;
import org.hzero.platform.domain.repository.PermissionRuleRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 屏蔽规则
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@ApiModel("屏蔽规则")
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_permission_rule")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRule extends AuditDomain {

    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_RULE_CODE = "ruleCode";
    public static final String FIELD_RULE_NAME = "ruleName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SQL_VALUE = "sqlValue";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_RULE_TYPE_CODE = "ruleTypeCode";
    public static final String FIELD_RULE_TYPE_CODE_MEANING = "ruleTypeCodeMeaning";
    public static final String FIELD_EDITABLE_FLAG = "editableFlag";


    /**
     * 校验租户数据操作合法性
     *
     * @param ruleRepository 数据屏蔽规则资源层
     */
    public void judgeDataLegality(PermissionRuleRepository ruleRepository) {
        if (ruleId == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------
    /**
     * 校验数据是否有效
     */
    public void checkDataLegalization(PermissionRuleRepository ruleRepository) {
        PermissionRule permissionRule = ruleRepository.selectByPrimaryKey(this.ruleId);
        Assert.notNull(permissionRule, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (!Objects.equals(permissionRule.getTenantId(), this.tenantId)) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_PERMISSION_RULE_TENANT_NOT_MATCH);
        }

        if (!Objects.equals(permissionRule.getEditableFlag(), BaseConstants.Flag.NO)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("规则ID")
    @Encrypt
    private Long ruleId;
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @ApiModelProperty("规则代码")
    @Length(max = 30)
    private String ruleCode;
    @NotBlank
    @MultiLanguageField
    @ApiModelProperty("规则名称")
    @Length(max = 120)
    private String ruleName;
    @ApiModelProperty("规则描述")
    @Length(max = 480)
    private String description;
    @ApiModelProperty("屏蔽SQL值")
    private String sqlValue;
    @NotNull
    @ApiModelProperty("租户ID")
    @MultiLanguageField
    private Long tenantId;
    @NotNull
    @ApiModelProperty("启用标识")
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty("规则类型代码")
    @LovValue(lovCode = "HPFM.PERMISSION_RULE_TYPE",meaningField = FIELD_RULE_TYPE_CODE_MEANING)
    @Length(max = 30)
    private String ruleTypeCode;
    @ApiModelProperty("编辑标识")
    @Range(max = 1)
    private Integer editableFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String ruleTypeCodeMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * @return 屏蔽规则code
     */
    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    /**
     * @return 屏蔽规则名称
     */
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public PermissionRule setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * @return 屏蔽sql值
     */
    public String getSqlValue() {
        return sqlValue;
    }

    public PermissionRule setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
        return this;
    }

    /**
     * @return 租户id，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public PermissionRule setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getRuleTypeCode() {
        return ruleTypeCode;
    }

    public void setRuleTypeCode(String ruleTypeCode) {
        this.ruleTypeCode = ruleTypeCode;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public PermissionRule setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }

    public String getRuleTypeCodeMeaning() {
        return ruleTypeCodeMeaning;
    }

    public void setRuleTypeCodeMeaning(String ruleTypeCodeMeaning) {
        this.ruleTypeCodeMeaning = ruleTypeCodeMeaning;
    }
}
