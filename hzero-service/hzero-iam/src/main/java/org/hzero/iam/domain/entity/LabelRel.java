package org.hzero.iam.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 标签关系表
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 * @see org.hzero.iam.infra.constant.LabelAssignType
 */
@ApiModel("标签关系表")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hiam_label_rel")
public class LabelRel extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_label_rel";

    public static final String FIELD_LABEL_REL_ID = "labelRelId";
    public static final String FIELD_DATA_TYPE = "dataType";
    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_LABEL_ID = "labelId";
    public static final String FIELD_ASSIGN_TYPE = "assignType";

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
    private Long labelRelId;
    @ApiModelProperty(value = "数据类型：iam_label.type", required = true)
    @NotBlank
    private String dataType;
    @ApiModelProperty(value = "数据ID", required = true)
    @NotNull
    @Encrypt
    private Long dataId;
    @ApiModelProperty(value = "标签ID：iam_label.id", required = true)
    @NotNull
    @Encrypt
    private Long labelId;
    @ApiModelProperty(value = "分配类型：A-自动；M-手动", required = true)
    @NotBlank
    private String assignType;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private Label label;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * 构建标签关联对象
     *
     * @param dataType   数据类型
     * @param dataId     数据ID
     * @param labelId    标签ID
     * @param assignType 分配类型
     * @return 标签关系数据对象
     */
    public static LabelRel built(String dataType, Long dataId, Long labelId, String assignType) {
        LabelRel labelRel = new LabelRel();
        labelRel.setDataType(dataType);
        labelRel.setDataId(dataId);
        labelRel.setLabelId(labelId);
        labelRel.setAssignType(assignType);

        return labelRel;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getLabelRelId() {
        return labelRelId;
    }

    public void setLabelRelId(Long labelRelId) {
        this.labelRelId = labelRelId;
    }

    /**
     * @return 数据类型：iam_label.type
     */
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return 数据ID
     */
    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    /**
     * @return 标签ID：iam_label.id
     */
    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    /**
     * @return 分配类型：A-自动；M-手动
     */
    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
