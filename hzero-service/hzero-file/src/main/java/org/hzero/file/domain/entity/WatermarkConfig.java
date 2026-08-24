package org.hzero.file.domain.entity;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 水印配置
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-13 13:45:12
 */
@ApiModel("水印配置")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hfle_watermark_config")
public class WatermarkConfig extends AuditDomain {

    public static final String FIELD_WATERMARK_ID = "watermarkId";
    public static final String FIELD_WATERMARK_CODE = "watermarkCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_WATERMARK_TYPE = "watermarkType";
    public static final String FIELD_FILL_OPACITY = "fillOpacity";
    public static final String FIELD_COLOR = "color";
    public static final String FIELD_FONT_SIZE = "fontSize";
    public static final String FIELD_X_AXIS = "xAxis";
    public static final String FIELD_Y_AXIS = "yAxis";
    public static final String FIELD_ALIGN = "align";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_HEIGHT = "height";
    public static final String FIELD_ROTATION = "rotation";
    public static final String FIELD_DETAIL = "detail";
    public static final String FIELD_FONT_URL = "fontUrl";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验
     */
    public void validate() {
        if (Objects.equals(watermarkType, HfleConstant.WatermarkType.TEXT)) {
            Assert.isTrue(StringUtils.isNotBlank(color), BaseConstants.ErrorCode.DATA_INVALID);
            Assert.notNull(fontSize, BaseConstants.ErrorCode.DATA_INVALID);
            Assert.notNull(align, BaseConstants.ErrorCode.DATA_INVALID);
        }
        if (Objects.equals(watermarkType, HfleConstant.WatermarkType.IMAGE)) {
            Assert.notNull(height, BaseConstants.ErrorCode.DATA_INVALID);
            Assert.notNull(weight, BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long watermarkId;
    @ApiModelProperty(value = "水印编码", required = true)
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    private String watermarkCode;
    @ApiModelProperty(value = "水印描述", required = true)
    @Length(max = 240)
    private String description;
    @ApiModelProperty(value = "水印类型，值集：HFLE.WATERMARK_TYPE", required = true)
    @NotBlank
    @LovValue(lovCode = "HFLE.WATERMARK_TYPE")
    @Length(max = 30)
    private String watermarkType;
    @ApiModelProperty(value = "透明度", required = true)
    @NotNull
    private BigDecimal fillOpacity;
    @ApiModelProperty(value = "色彩")
    private String color;
    @ApiModelProperty(value = "字体大小")
    private Long fontSize;
    @ApiModelProperty(value = "横坐标", required = true)
    @NotNull
    private Long xAxis;
    @ApiModelProperty(value = "纵坐标", required = true)
    @NotNull
    private Long yAxis;
    @ApiModelProperty(value = "对齐方式，0左对齐1居中2右对齐")
    private Integer align;
    @ApiModelProperty(value = "图片宽度")
    private Long weight;
    @ApiModelProperty(value = "图片高度")
    private Long height;
    @ApiModelProperty(value = "倾斜角度", required = true)
    @NotNull
    private Long rotation;
    @ApiModelProperty(value = "水印内容", required = true)
    @NotBlank
    @Length(max = 480)
    private String detail;
    @ApiModelProperty(value = "字体文件")
    @Length(max = 480)
    private String fontUrl;
    @ApiModelProperty(value = "启用标识", required = true)
    @NotNull
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String watermarkTypeMeaning;
    @Transient
    private String filename;

    @Transient
    private String contextBucket;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getWatermarkId() {
        return watermarkId;
    }

    public WatermarkConfig setWatermarkId(Long watermarkId) {
        this.watermarkId = watermarkId;
        return this;
    }

    public String getWatermarkCode() {
        return watermarkCode;
    }

    public WatermarkConfig setWatermarkCode(String watermarkCode) {
        this.watermarkCode = watermarkCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public WatermarkConfig setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getWatermarkType() {
        return watermarkType;
    }

    public WatermarkConfig setWatermarkType(String watermarkType) {
        this.watermarkType = watermarkType;
        return this;
    }

    public BigDecimal getFillOpacity() {
        return fillOpacity;
    }

    public WatermarkConfig setFillOpacity(BigDecimal fillOpacity) {
        this.fillOpacity = fillOpacity;
        return this;
    }

    public String getColor() {
        return color;
    }

    public WatermarkConfig setColor(String color) {
        this.color = color;
        return this;
    }

    public Long getFontSize() {
        return fontSize;
    }

    public WatermarkConfig setFontSize(Long fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public Long getxAxis() {
        return xAxis;
    }

    public WatermarkConfig setxAxis(Long xAxis) {
        this.xAxis = xAxis;
        return this;
    }

    public Long getyAxis() {
        return yAxis;
    }

    public WatermarkConfig setyAxis(Long yAxis) {
        this.yAxis = yAxis;
        return this;
    }

    public Integer getAlign() {
        return align;
    }

    public WatermarkConfig setAlign(Integer align) {
        this.align = align;
        return this;
    }

    public Long getWeight() {
        return weight;
    }

    public WatermarkConfig setWeight(Long weight) {
        this.weight = weight;
        return this;
    }

    public Long getHeight() {
        return height;
    }

    public WatermarkConfig setHeight(Long height) {
        this.height = height;
        return this;
    }

    public Long getRotation() {
        return rotation;
    }

    public WatermarkConfig setRotation(Long rotation) {
        this.rotation = rotation;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public WatermarkConfig setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public String getFontUrl() {
        return fontUrl;
    }

    public WatermarkConfig setFontUrl(String fontUrl) {
        this.fontUrl = fontUrl;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public WatermarkConfig setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public WatermarkConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public WatermarkConfig setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getWatermarkTypeMeaning() {
        return watermarkTypeMeaning;
    }

    public WatermarkConfig setWatermarkTypeMeaning(String watermarkTypeMeaning) {
        this.watermarkTypeMeaning = watermarkTypeMeaning;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public WatermarkConfig setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public String getContextBucket() {
        return contextBucket;
    }

    public WatermarkConfig setContextBucket(String contextBucket) {
        this.contextBucket = contextBucket;
        return this;
    }
}
