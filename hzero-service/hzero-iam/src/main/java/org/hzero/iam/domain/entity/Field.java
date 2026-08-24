package org.hzero.iam.domain.entity;

import java.util.Arrays;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 接口字段维护
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@ApiModel("接口字段维护")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_field")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_field";

    public static final String FIELD_FIELD_ID = "fieldId";
    public static final String FIELD_PERMISSION_ID = "permissionId";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_FIELD_TYPE = "fieldType";
    public static final String FIELD_FIELD_DESCRIPTION = "fieldDescription";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    private static final String[] UPDATABLE_FIELD = new String[]{FIELD_FIELD_NAME, FIELD_FIELD_TYPE, FIELD_FIELD_DESCRIPTION, FIELD_ORDER_SEQ};

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
    private Long fieldId;
    @ApiModelProperty(value = "接口权限ID,iam_permission.id", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long permissionId;
    @ApiModelProperty(value = "字段名称", required = true)
    @NotBlank
    @Length(max = 120)
    @Unique
    private String fieldName;
    @LovValue(lovCode = "HIAM.FIELD.TYPE")
    @ApiModelProperty(value = "字段类型，值集HIAM.FIELD.TYPE[NUMBER(数字),STRING(字符串)]", required = true)
    @NotBlank
    @Length(max = 30)
    private String fieldType;
    @ApiModelProperty(value = "字段描述", required = true)
    @NotBlank
    @Length(max = 480)
    private String fieldDescription;
    @ApiModelProperty(value = "排序号", required = true)
    @NotNull
    private Long orderSeq;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String fieldTypeMeaning;
    @Transient
    private String serviceName;
    @Transient
    private String method;
    @Transient
    private String path;

    public static String[] getUpdatableField() {
        return Arrays.copyOf(UPDATABLE_FIELD, UPDATABLE_FIELD.length);
    }
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getFieldId() {
        return fieldId;
    }

    public Field setFieldId(Long fieldId) {
        this.fieldId = fieldId;
        return this;
    }

    /**
     * @return 接口权限ID, iam_permission.id
     */
    public Long getPermissionId() {
        return permissionId;
    }

    public Field setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
        return this;
    }

    /**
     * @return 字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    public Field setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    /**
     * @return 字段类型，值集HIAM.FIELD.TYPE[NUMBER(数字),STRING(字符串)]
     */
    public String getFieldType() {
        return fieldType;
    }

    public Field setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    /**
     * @return 字段描述
     */
    public String getFieldDescription() {
        return fieldDescription;
    }

    public Field setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
        return this;
    }

    /**
     * @return 排序号
     */
    public Long getOrderSeq() {
        return orderSeq;
    }

    public Field setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getFieldTypeMeaning() {
        return fieldTypeMeaning;
    }

    public Field setFieldTypeMeaning(String fieldTypeMeaning) {
        this.fieldTypeMeaning = fieldTypeMeaning;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Field setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Field setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Field setPath(String path) {
        this.path = path;
        return this;
    }
}
