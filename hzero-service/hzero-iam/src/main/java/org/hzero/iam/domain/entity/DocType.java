package org.hzero.iam.domain.entity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单据类型定义
 *
 * @author min.wang01@hand-china.com 2018-08-08 16:32:49
 */
@ApiModel("单据类型定义")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_doc_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class DocType extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_doc_type";

    public static final String FIELD_DOC_TYPE_ID = "docTypeId";
    public static final String FIELD_DOC_TYPE_CODE = "docTypeCode";
    public static final String FIELD_DOC_TYPE_NAME = "docTypeName";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SOURCE_SERVICE_NAME = "sourceServiceName";
    public static final String FIELD_SOURCE_DATA_ENTITY = "sourceDataEntity";
    public static final String FIELD_LEVEL_CODE = "levelCode";
    public static final String FIELD_AUTH_SCOPE_CODE = "authScopeCode";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_RULE_ID = "ruleId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_AUTH_CONTROL_TYPE = "authControlType";
    private static final String[] UPDATABLE_FIELD = {FIELD_DOC_TYPE_NAME, FIELD_DESCRIPTION,
            FIELD_SOURCE_SERVICE_NAME, FIELD_SOURCE_DATA_ENTITY, FIELD_LEVEL_CODE, FIELD_AUTH_SCOPE_CODE, FIELD_ORDER_SEQ, FIELD_ENABLED_FLAG, FIELD_AUTH_CONTROL_TYPE};

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long docTypeId;
    @ApiModelProperty(value = "单据类型编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Unique
    private String docTypeCode;
    @NotNull
    @Length(max = 240)
    @MultiLanguageField
    private String docTypeName;
    @Length(max = 240)
    private String description;
    @ApiModelProperty(value = "来源微服务（也即数据实体所在微服务）")
    @NotBlank
    @Length(max = 120)
    private String sourceServiceName;
    @Length(max = 80)
    private String sourceDataEntity;
    @ApiModelProperty(value = "层级，HIAM.DOC_TYPE_LEVEL_CODE,GLOBAL/TENANT")
    @NotBlank
    @LovValue(lovCode = "HIAM.DOC_TYPE_LEVEL_CODE")
    @Length(max = 30)
    private String levelCode;
    @LovValue(lovCode = "HIAM.AUTHORITY_SCOPE_CODE")
    @Length(max = 30)
    private String authScopeCode;
    private Long orderSeq;
    @ApiModelProperty(value = "启用标识")
    private Integer enabledFlag;
    /**
     * 新增租户id字段
     */
    @Unique
    @ApiModelProperty(value = "租户Id")
    @MultiLanguageField
    private Long tenantId;
    /**
     * FIX 20200331 修改该字段为非必输，默认设置为所有人(ALL)类型
     */
    @LovValue(lovCode = "HIAM.DOC.AUTH_TYPE")
    private String authControlType;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private List<DocTypePermission> docTypePermissions;
    @Transient
    @Valid
    private List<DocTypeSqlid> docTypeSqlidList;
    @Transient
    private String levelMeaning;
    @Transient
    private String authScopeMeaning;
    @Transient
    private List<DocTypeAssign> docTypeAssigns;
    @Transient
    private List<DocTypeAuthDim> docTypeAuthDims;
    @Transient
    private String ruleCode;
    @Transient
    private String ruleName;
    @Transient
    private String authControlTypeMeaning;
    /**
     * 数据权限维护页面使用
     */
    @Transient
    private List<DocTypeDimension> docTypeDimensionList;

    public static String[] getUpdatableField() {
        return Arrays.copyOf(UPDATABLE_FIELD, UPDATABLE_FIELD.length);
    }
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getTenantId() {
        return tenantId;
    }

    /**
     * @return 表ID
     */
    public Long getDocTypeId() {
        return docTypeId;
    }

    /**
     * @return 单据类型编码
     */
    public String getDocTypeCode() {
        return docTypeCode;
    }

    /**
     * @return 单据类型名称
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 来源微服务（也即数据实体所在微服务）
     */
    public String getSourceServiceName() {
        return sourceServiceName;
    }

    /**
     * @return 来源数据实体，Mapper ID
     */
    public String getSourceDataEntity() {
        return sourceDataEntity;
    }

    /**
     * @return 层级，HIAM.DOC_TYPE_LEVEL_CODE,GLOBAL/TENANT
     */
    public String getLevelCode() {
        return levelCode;
    }

    /**
     * @return 权限限制范围，HIAM.AUTHORITY_SCOPE_CODE
     */
    public String getAuthScopeCode() {
        return authScopeCode;
    }

    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    /**
     * @return 层级描述
     */
    public String getLevelMeaning() {
        return levelMeaning;
    }

    /**
     * @return 权限限制范围
     */
    public String getAuthScopeMeaning() {
        return authScopeMeaning;
    }

    /**
     * @return 单据类型分配
     */
    public List<DocTypeAssign> getDocTypeAssigns() {
        return docTypeAssigns;
    }

    /**
     * @return 权限维度
     */
    public List<DocTypeAuthDim> getDocTypeAuthDims() {
        return docTypeAuthDims;
    }

    /**
     * @return 数据屏蔽规则代码
     */
    public String getRuleCode() {
        return ruleCode;
    }

    /**
     * @return 数据屏蔽规则名称
     */
    public String getRuleName() {
        return ruleName;
    }

    public DocType setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
        return this;
    }

    public DocType setDocTypeCode(String docTypeCode) {
        this.docTypeCode = docTypeCode;
        return this;
    }

    public DocType setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
        return this;
    }

    public DocType setDescription(String description) {
        this.description = description;
        return this;
    }

    public DocType setSourceServiceName(String sourceServiceName) {
        this.sourceServiceName = sourceServiceName;
        return this;
    }

    public DocType setSourceDataEntity(String sourceDataEntity) {
        this.sourceDataEntity = sourceDataEntity;
        return this;
    }

    public DocType setLevelCode(String levelCode) {
        this.levelCode = levelCode;
        return this;
    }

    public DocType setAuthScopeCode(String authScopeCode) {
        this.authScopeCode = authScopeCode;
        return this;
    }

    public DocType setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public DocType setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public DocType setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public DocType setLevelMeaning(String levelMeaning) {
        this.levelMeaning = levelMeaning;
        return this;
    }

    public DocType setAuthScopeMeaning(String authScopeMeaning) {
        this.authScopeMeaning = authScopeMeaning;
        return this;
    }

    public DocType setDocTypeAssigns(List<DocTypeAssign> docTypeAssigns) {
        this.docTypeAssigns = docTypeAssigns;
        return this;
    }

    public DocType setDocTypeAuthDims(List<DocTypeAuthDim> docTypeAuthDims) {
        this.docTypeAuthDims = docTypeAuthDims;
        return this;
    }

    public DocType setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public DocType setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public List<DocTypePermission> getDocTypePermissions() {
        return docTypePermissions;
    }

    public DocType setDocTypePermissions(List<DocTypePermission> docTypePermissions) {
        this.docTypePermissions = docTypePermissions;
        return this;
    }

    public List<DocTypeSqlid> getDocTypeSqlidList() {
        return docTypeSqlidList;
    }

    public DocType setDocTypeSqlidList(List<DocTypeSqlid> docTypeSqlidList) {
        this.docTypeSqlidList = docTypeSqlidList;
        return this;
    }

    public String getAuthControlType() {
        return authControlType;
    }

    public DocType setAuthControlType(String authControlType) {
        this.authControlType = authControlType;
        return this;
    }

    public String getAuthControlTypeMeaning() {
        return authControlTypeMeaning;
    }

    public DocType setAuthControlTypeMeaning(String authControlTypeMeaning) {
        this.authControlTypeMeaning = authControlTypeMeaning;
        return this;
    }

    public List<DocTypeDimension> getDocTypeDimensionList() {
        return docTypeDimensionList;
    }

    public void setDocTypeDimensionList(List<DocTypeDimension> docTypeDimensionList) {
        this.docTypeDimensionList = docTypeDimensionList;
    }
}
