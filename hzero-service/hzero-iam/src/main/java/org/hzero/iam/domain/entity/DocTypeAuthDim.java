package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单据类型权限维度定义
 *
 * @author min.wang01@hand-china.com 2018-08-08 19:55:53
 */
@ApiModel("单据类型权限维度定义")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_doc_type_auth_dim")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocTypeAuthDim extends AuditDomain {
    public static final String ENCRYPT_KEY = "hiam_doc_type_auth_dim";
    public static final String FIELD_AUTH_DIM_ID = "authDimId";
    public static final String FIELD_DOC_TYPE_ID = "docTypeId";
    public static final String FIELD_AUTH_TYPE_CODE = "authTypeCode";
    public static final String FIELD_RULE_TYPE = "ruleType";
    public static final String FIELD_SOURCE_MATCH_TABLE = "sourceMatchTable";
    public static final String FIELD_SOURCE_MATCH_FIELD = "sourceMatchField";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public interface Insert{}

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long authDimId;
    @ApiModelProperty(value = "单据类型ID")
    @Encrypt
    private Long docTypeId;
    @ApiModelProperty(value = "权限类型代码，HIAM.AUTHORITY_TYPE_CODE")
    @NotBlank
    @Size(max = 30)
    private String authTypeCode;
    @ApiModelProperty(value = "规则类型")
    @NotBlank
    @Size(max = 30)
    @LovValue(lovCode = "HIAM.DOC.RULE_TYPE")
    private String ruleType;
    @ApiModelProperty(value = "来源匹配表")
    @NotBlank
    @Size(max = 30)
    private String sourceMatchTable;
    @ApiModelProperty(value = "来源匹配字段，匹配Mapper中的相关字段")
    @NotBlank
    @Size(max = 1200)
    private String sourceMatchField;
    @NotNull(groups = Insert.class)
    @ApiModelProperty("租户ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String authTypeMeaning;
    @Transient
    private String ruleTypeMeaning;
    @Transient
    private Integer actionType;
    @Transient
    private String dimensionName;
    @Transient
    private String dimensionType;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getTenantId() {
        return tenantId;
    }

    public DocTypeAuthDim setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 表ID
     */
    public Long getAuthDimId() {
        return authDimId;
    }

    /**
     * @return 单据类型ID
     */
    public Long getDocTypeId() {
        return docTypeId;
    }

    /**
     * @return 权限类型代码，HIAM.AUTHORITY_TYPE_CODE
     */
    public String getAuthTypeCode() {
        return authTypeCode;
    }

    public String getRuleType() {
        return ruleType;
    }

    public DocTypeAuthDim setRuleType(String ruleType) {
        this.ruleType = ruleType;
        return this;
    }

    /**
     * @return 来源匹配表名
     */
    public String getSourceMatchTable() {
        return sourceMatchTable;
    }

    /**
     * @return 来源匹配字段，匹配Mapper中的相关字段
     */
    public String getSourceMatchField() {
        return sourceMatchField;
    }

    /**
     * @return 权限类型描述
     */
    public String getAuthTypeMeaning() {
        return authTypeMeaning;
    }

    public String getRuleTypeMeaning() {
        return ruleTypeMeaning;
    }

    public DocTypeAuthDim setRuleTypeMeaning(String ruleTypeMeaning) {
        this.ruleTypeMeaning = ruleTypeMeaning;
        return this;
    }

    /**
     * @return 操作类型，勾选为1，去除勾选为0
     */
    public Integer getActionType() {
        return actionType;
    }

    /**
     * @return 维度名称
     */
    public String getDimensionName() {
        return dimensionName;
    }

    /**
     * @return 维度类型
     */
    public String getDimensionType() {
        return dimensionType;
    }

    public DocTypeAuthDim setAuthDimId(Long authDimId) {
        this.authDimId = authDimId;
        return this;
    }

    public DocTypeAuthDim setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
        return this;
    }

    public DocTypeAuthDim setAuthTypeCode(String authTypeCode) {
        this.authTypeCode = authTypeCode;
        return this;
    }

    public DocTypeAuthDim setSourceMatchTable(String sourceMatchTable) {
        this.sourceMatchTable = sourceMatchTable;
        return this;
    }

    public DocTypeAuthDim setSourceMatchField(String sourceMatchField) {
        this.sourceMatchField = sourceMatchField;
        return this;
    }

    public DocTypeAuthDim setAuthTypeMeaning(String authTypeMeaning) {
        this.authTypeMeaning = authTypeMeaning;
        return this;
    }

    public DocTypeAuthDim setActionType(Integer actionType) {
        this.actionType = actionType;
        return this;
    }

    public DocTypeAuthDim setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
        return this;
    }

    public DocTypeAuthDim setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
        return this;
    }
}
