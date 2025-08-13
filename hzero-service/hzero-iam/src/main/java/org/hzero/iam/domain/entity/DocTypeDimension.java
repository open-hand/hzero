package org.hzero.iam.domain.entity;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.Unique;
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
 * 单据类型维度
 *
 * @author zifeng.ding@hand-china.com 2019-09-18 11:48:01
 */
@ApiModel("单据类型维度")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_doc_type_dimension")
@MultiLanguage
public class DocTypeDimension extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_doc_type_dimension";

    public static final String FIELD_DIMENSION_ID = "dimensionId";
    public static final String FIELD_DIMENSION_CODE = "dimensionCode";
    public static final String FIELD_DIMENSION_NAME = "dimensionName";
    public static final String FIELD_DIMENSION_TYPE = "dimensionType";
    public static final String FIELD_VALUE_SOURCE_TYPE = "valueSourceType";
    public static final String FIELD_VALUE_SOURCE = "valueSource";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_TENANT_ID = "tenantId";

    public interface Biz {
    }


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long dimensionId;
    @ApiModelProperty(value = "维度编码", required = true)
    @Length(max = 30)
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Unique
    private String dimensionCode;
    @ApiModelProperty(value = "维度名称", required = true)
    @Length(max = 60)
    @NotBlank
    @MultiLanguageField
    private String dimensionName;
    @ApiModelProperty(value = "维度类型，值集：HIAM.AUTHORITY_SCOPE_CODE", required = true)
    @Length(max = 30)
    @NotBlank
    @LovValue(lovCode = "HIAM.AUTHORITY_SCOPE_CODE", meaningField = "dimensionTypeMeaning")
    private String dimensionType;
    @ApiModelProperty(value = "值来源类型，值集：HIAM.DOC_DIMENSION.SOURCE_TYPE", required = true)
    @Length(max = 30)
    @NotBlank(groups = Biz.class)
    @LovValue(lovCode = "HIAM.DOC_DIMENSION.SOURCE_TYPE", meaningField = "valueSourceTypeMeaning")
    private String valueSourceType;
    @ApiModelProperty(value = "值来源", required = true)
    @NotBlank(groups = Biz.class)
    @Length(max = 30)
    private String valueSource;
    @ApiModelProperty(value = "是否启用。1启用，0未启用", required = true)
    @NotNull
    @Range(max = 1, min = 0)
    private Integer enabledFlag;
    @ApiModelProperty(value = "排序号", required = true)
    @NotNull
    private Long orderSeq;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @MultiLanguageField
    private Long tenantId;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String dimensionTypeMeaning;
    @Transient
    private String valueSourceTypeMeaning;
    @Transient
    private String viewName;
    @Transient
    private String viewCode;
    @Transient
    private Long authDimId;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */

    public Long getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(Long dimensionId) {
        this.dimensionId = dimensionId;
    }

    /**
     * @return 维度编码
     */
    public String getDimensionCode() {
        return dimensionCode;
    }

    public DocTypeDimension setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
        return this;
    }

    /**
     * @return 维度名称
     */
    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    /**
     * @return 维度类型，值集：HIAM.AUTHORITY_SCOPE_CODE
     */
    public String getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(String dimensionType) {
        this.dimensionType = dimensionType;
    }

    /**
     * @return 值来源类型，值集：HIAM.DOC_DIMENSION.SOURCE_TYPE
     */
    public String getValueSourceType() {
        return valueSourceType;
    }

    public void setValueSourceType(String valueSourceType) {
        this.valueSourceType = valueSourceType;
    }

    /**
     * @return 值来源
     */
    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
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

    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public DocTypeDimension setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 维度类型 值集
     */
    public String getDimensionTypeMeaning() {
        return dimensionTypeMeaning;
    }

    public void setDimensionTypeMeaning(String dimensionTypeMeaning) {
        this.dimensionTypeMeaning = dimensionTypeMeaning;
    }

    /**
     * @return 值来源类型 值集
     */
    public String getValueSourceTypeMeaning() {
        return valueSourceTypeMeaning;
    }

    public void setValueSourceTypeMeaning(String valueSourceTypeMeaning) {
        this.valueSourceTypeMeaning = valueSourceTypeMeaning;
    }

    /**
     * @return 值集视图名称
     */
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * @return 值集视图编码
     */
    public String getViewCode() {
        return viewCode;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }

    /**
     * @return 重写父类get、set
     */

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

    public Long getAuthDimId() {
        return authDimId;
    }

    public void setAuthDimId(Long authDimId) {
        this.authDimId = authDimId;
    }
}
