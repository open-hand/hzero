package org.hzero.report.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 标签打印
 *
 * @author fanghan.liu@hand-china.com 2019-11-27 10:35:39
 */
@ApiModel("标签打印")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hrpt_label_print")
public class LabelPrint extends AuditDomain {

    public static final String FIELD_LABEL_PRINT_ID = "labelPrintId";
    public static final String FIELD_LABEL_TEMPLATE_CODE = "labelTemplateCode";
    public static final String FIELD_PAPER_SIZE = "paperSize";
    public static final String FIELD_PAPER_WIDTH = "paperWidth";
    public static final String FIELD_PAPER_HIGH = "paperHigh";
    public static final String FIELD_PRINT_DIRECTION = "printDirection";
    public static final String FIELD_MARGIN_TOP = "marginTop";
    public static final String FIELD_MARGIN_BOTTOM = "marginBottom";
    public static final String FIELD_MARGIN_LEFT = "marginLeft";
    public static final String FIELD_MARGIN_RIGHT = "marginRight";
    public static final String FIELD_WIDE_QTY = "wideQty";
    public static final String FIELD_HIGH_QTY = "highQty";
    public static final String FIELD_HIGH_SPACE = "highSpace";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 打印纸张尺寸校验
     */
    public void validatePrintSize(LabelTemplateRepository repository, Long sourceTenantId) {
        LabelTemplate labelTemplate = repository.selectOne(new LabelTemplate().setTemplateCode(labelTemplateCode).setTenantId(sourceTenantId));
        long width = HrptConstants.LabelPrintSetting.HORIZONTAL.equals(printDirection) ? paperHigh : paperWidth;
        if (paperHigh != null) {
            long height = paperHigh + paperWidth - width;
            highQty = highQty == null ? 1 : highQty;
            if (marginBottom != null && getPrintHeight(labelTemplate) > height) {
                throw new CommonException(HrptMessageConstants.INSUFFICIENT_PAPER_HEIGHT);
            }
        }
        if (getPrintWidth(labelTemplate) > width) {
            throw new CommonException(HrptMessageConstants.INSUFFICIENT_PAPER_WIDTH);
        }
    }

    /**
     * 获取打印宽
     */
    private Long getPrintWidth(LabelTemplate labelTemplate) {
        return (wideQty * labelTemplate.getTemplateWidth() + marginLeft + marginRight);
    }

    /**
     * 获取打印高
     */
    private Long getPrintHeight(LabelTemplate labelTemplate) {
        return highQty * labelTemplate.getTemplateHigh() + marginTop + marginBottom + (highSpace == null ? 0 : (highQty - 1) * highSpace);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long labelPrintId;
    @ApiModelProperty(value = "模板编码，hrpt_label_template.template_code", required = true)
    @NotNull
    @Unique
    private String labelTemplateCode;
    @ApiModelProperty(value = "纸张尺寸，值集：HRPT.LABEL_PAPER")
    private String paperSize;
    @ApiModelProperty(value = "纸张宽度")
    private Long paperWidth;
    @ApiModelProperty(value = "纸张高度")
    private Long paperHigh;
    @ApiModelProperty(value = "0:横向 1:纵向", required = true)
    @NotNull
    private Integer printDirection;
    @ApiModelProperty(value = "上边距")
    private Long marginTop;
    @ApiModelProperty(value = "下边距")
    private Long marginBottom;
    @ApiModelProperty(value = "左边距")
    private Long marginLeft;
    @ApiModelProperty(value = "右边距")
    private Long marginRight;
    @ApiModelProperty(value = "宽数量")
    private Long wideQty;
    @ApiModelProperty(value = "高数量")
    private Long highQty;
    @ApiModelProperty(value = "高间距")
    private Long highSpace;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id", required = true)
    @NotNull
    @Unique
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getLabelPrintId() {
        return labelPrintId;
    }

    public LabelPrint setLabelPrintId(Long labelPrintId) {
        this.labelPrintId = labelPrintId;
        return this;
    }

    public String getLabelTemplateCode() {
        return labelTemplateCode;
    }

    public LabelPrint setLabelTemplateCode(String labelTemplateCode) {
        this.labelTemplateCode = labelTemplateCode;
        return this;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public LabelPrint setPaperSize(String paperSize) {
        this.paperSize = paperSize;
        return this;
    }

    public Long getPaperWidth() {
        return paperWidth;
    }

    public LabelPrint setPaperWidth(Long paperWidth) {
        this.paperWidth = paperWidth;
        return this;
    }

    public Long getPaperHigh() {
        return paperHigh;
    }

    public LabelPrint setPaperHigh(Long paperHigh) {
        this.paperHigh = paperHigh;
        return this;
    }

    public Integer getPrintDirection() {
        return printDirection;
    }

    public LabelPrint setPrintDirection(Integer printDirection) {
        this.printDirection = printDirection;
        return this;
    }

    public Long getMarginTop() {
        return marginTop;
    }

    public LabelPrint setMarginTop(Long marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public Long getMarginBottom() {
        return marginBottom;
    }

    public LabelPrint setMarginBottom(Long marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public Long getMarginLeft() {
        return marginLeft;
    }

    public LabelPrint setMarginLeft(Long marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public Long getMarginRight() {
        return marginRight;
    }

    public LabelPrint setMarginRight(Long marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public Long getWideQty() {
        return wideQty;
    }

    public LabelPrint setWideQty(Long wideQty) {
        this.wideQty = wideQty;
        return this;
    }

    public Long getHighQty() {
        return highQty;
    }

    public LabelPrint setHighQty(Long highQty) {
        this.highQty = highQty;
        return this;
    }

    public Long getHighSpace() {
        return highSpace;
    }

    public LabelPrint setHighSpace(Long highSpace) {
        this.highSpace = highSpace;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LabelPrint setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
