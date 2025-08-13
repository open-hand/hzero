package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 标签参数
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@ApiModel("标签参数")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_label_parameter")
public class LabelParameter extends AuditDomain {

    public static final String FIELD_LABEL_PARAMETER_ID = "labelParameterId";
    public static final String FIELD_LABEL_TEMPLATE_ID = "labelTemplateId";
    public static final String FIELD_PARAMETER_CODE = "parameterCode";
    public static final String FIELD_PARAMETER_NAME = "parameterName";
    public static final String FIELD_PARAM_TYPE_CODE = "paramTypeCode";
    public static final String FIELD_TEXT_LENGTH = "textLength";
    public static final String FIELD_MAX_ROWS = "maxRows";
    public static final String FIELD_IMAGE_URL = "imageUrl";
    public static final String FIELD_BAR_CODE_TYPE = "barCodeType";
    public static final String FIELD_CHARACTER_ENCODING = "characterEncoding";
    public static final String FIELD_TENANT_ID = "tenantId";

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
    private Long labelParameterId;
    @ApiModelProperty(value = "模板ID，hrpt_label_template.label_template_id", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long labelTemplateId;
    @ApiModelProperty(value = "参数代码", required = true)
    @NotBlank
    @Unique
    private String parameterCode;
    @ApiModelProperty(value = "参数名称", required = true)
    @NotBlank
    private String parameterName;
    @ApiModelProperty(value = "参数类型，值集:HRPT.LABEL_PARAM_TYPE", required = true)
    @NotBlank
    private String paramTypeCode;
    @ApiModelProperty(value = "文字长度")
    private Integer textLength;
    @ApiModelProperty(value = "最大行数")
    private Integer maxRows;
    @ApiModelProperty(value = "图片地址")
    private String imageUrl;
    @ApiModelProperty(value = "条码类型，值集:HRPT.LABEL_BARCODE_TYPE")
    private String barCodeType;
    @ApiModelProperty(value = "字符编码", required = true)
    @NotBlank
    private String characterEncoding;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getLabelParameterId() {
        return labelParameterId;
    }

    public LabelParameter setLabelParameterId(Long labelParameterId) {
        this.labelParameterId = labelParameterId;
        return this;
    }

    public Long getLabelTemplateId() {
        return labelTemplateId;
    }

    public LabelParameter setLabelTemplateId(Long labelTemplateId) {
        this.labelTemplateId = labelTemplateId;
        return this;
    }

    public String getParameterCode() {
        return parameterCode;
    }

    public LabelParameter setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
        return this;
    }

    public String getParameterName() {
        return parameterName;
    }

    public LabelParameter setParameterName(String parameterName) {
        this.parameterName = parameterName;
        return this;
    }

    public String getParamTypeCode() {
        return paramTypeCode;
    }

    public LabelParameter setParamTypeCode(String paramTypeCode) {
        this.paramTypeCode = paramTypeCode;
        return this;
    }

    public Integer getTextLength() {
        return textLength;
    }

    public LabelParameter setTextLength(Integer textLength) {
        this.textLength = textLength;
        return this;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public LabelParameter setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LabelParameter setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getBarCodeType() {
        return barCodeType;
    }

    public LabelParameter setBarCodeType(String barCodeType) {
        this.barCodeType = barCodeType;
        return this;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public LabelParameter setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LabelParameter setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
