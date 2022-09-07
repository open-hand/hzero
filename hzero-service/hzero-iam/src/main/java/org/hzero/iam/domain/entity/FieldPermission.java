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
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 接口字段权限维护
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@ApiModel("接口字段权限维护")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_field_permission")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldPermission extends AuditDomain {

    public static final String ENCRYPT_KEY = "hiam_field_permission";

    public static final String PERMISSION_DIMENSION_REQUIRED = "error.fieldPermission.permissionDimension.required";
    public static final String DIMENSION_VALUE_REQUIRED = "error.fieldPermission.dimensionValue.required";
    public static final String FIELD_ID_REQUIRED = "error.fieldPermission.fieldId.required";
    public static final String TENANT_ID_REQUIRED = "error.fieldPermission.tenantId.required";


    public static final String FIELD_FIELD_PERMISSION_ID = "fieldPermissionId";
    public static final String FIELD_PERMISSION_DIMENSION = "permissionDimension";
    public static final String FIELD_DIMENSION_VALUE = "dimensionValue";
    public static final String FIELD_FIELD_ID = "fieldId";
    public static final String FIELD_PERMISSION_TYPE = "permissionType";
    public static final String FIELD_PERMISSION_RULE = "permissionRule";
    private static final String[] UPDATABLE_FIELD = new String[]{FIELD_PERMISSION_TYPE, FIELD_PERMISSION_RULE};

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public FieldPermission() {

    }

    public FieldPermission(String permissionDimension, Long dimensionValue, Long fieldId, Long tenantId) {
        this.permissionDimension = permissionDimension;
        this.dimensionValue = dimensionValue;
        this.fieldId = fieldId;
        this.tenantId = tenantId;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long fieldPermissionId;
    @ApiModelProperty(value = "权限维度,值集HIAM.FIELD.PERMISSION_DIMENSION[USER,ROLE]", required = true)
    @NotBlank
    @Unique
    @Length(max = 30)
    private String permissionDimension;
    @ApiModelProperty(value = "维度值[USER(用户ID),ROLE(角色ID)]", required = true)
    @NotNull
    @Unique
    private Long dimensionValue;
    @ApiModelProperty(value = "字段ID，hiam_field.field_id", required = true)
    @NotNull
    @Unique
    @Encrypt
    private Long fieldId;
    @LovValue(lovCode = "HIAM.FIELD.PERMISSION_TYPE")
    @ApiModelProperty(value = "权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]", required = true)
    @NotBlank
    @Length(max = 30)
    private String permissionType;
    @Length(max = 60)
    @ApiModelProperty(value = "权限规则（脱敏预留）")
    private String permissionRule;
    @ApiModelProperty(value = "租户ID", required = true)
    private Long tenantId;
    @ApiModelProperty(value = "数据来源[DEFAULT/SEC_GRP]")
    private String dataSource;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String fieldName;
    @Transient
    @LovValue(lovCode = "HIAM.FIELD.TYPE")
    private String fieldType;
    @Transient
    private String fieldTypeMeaning;
    @Transient
    private String fieldDescription;
    @Transient
    private String permissionTypeMeaning;
    @Transient
    private  String serviceName;
    @Transient
    private String method;
    @Transient
    private String path;

    public String getRuleCache() {
        if (StringUtils.hasText(permissionRule)) {
            return fieldName + ":" + permissionType + ":" + permissionRule;
        } else {
            return fieldName + ":" + permissionType;
        }
    }

    public static String[] getUpdatableField() {
        return Arrays.copyOf(UPDATABLE_FIELD, UPDATABLE_FIELD.length);
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getFieldPermissionId() {
        return fieldPermissionId;
    }

    public void setFieldPermissionId(Long fieldPermissionId) {
        this.fieldPermissionId = fieldPermissionId;
    }

    /**
     * @return 权限维度, 值集HIAM.FIELD.PERMISSION_DIMENSION[USER, ROLE]
     */
    public String getPermissionDimension() {
        return permissionDimension;
    }

    public FieldPermission setPermissionDimension(String permissionDimension) {
        this.permissionDimension = permissionDimension;
        return this;
    }

    /**
     * @return 维度值[USER(用户ID), ROLE(角色ID)]
     */
    public Long getDimensionValue() {
        return dimensionValue;
    }

    public FieldPermission setDimensionValue(Long dimensionValue) {
        this.dimensionValue = dimensionValue;
        return this;
    }

    /**
     * @return 字段ID，hiam_field.field_id
     */
    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * @return 权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]
     */
    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    /**
     * @return 权限规则（脱敏预留）
     */
    public String getPermissionRule() {
        return permissionRule;
    }

    public void setPermissionRule(String permissionRule) {
        this.permissionRule = permissionRule;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public FieldPermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldPermission setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    public FieldPermission setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public FieldPermission setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
        return this;
    }

    public String getFieldTypeMeaning() {
        return fieldTypeMeaning;
    }

    public FieldPermission setFieldTypeMeaning(String fieldTypeMeaning) {
        this.fieldTypeMeaning = fieldTypeMeaning;
        return this;
    }

    public String getPermissionTypeMeaning() {
        return permissionTypeMeaning;
    }

    public FieldPermission setPermissionTypeMeaning(String permissionTypeMeaning) {
        this.permissionTypeMeaning = permissionTypeMeaning;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public FieldPermission setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public FieldPermission setMethod(String method) {
        this.method = method;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FieldPermission setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * 包含默认数据权限
     *
     * @return true: 包含
     */
    public boolean containDefaultDataSource() {
        return org.apache.commons.lang.StringUtils.contains(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }

    /**
     * 包含安全组的数据权限
     *
     * @return true:包含
     */
    public boolean containSecGrpDataSource() {
        return org.apache.commons.lang.StringUtils.contains(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }

    /**
     * 等于安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalSecGrpDataSource() {
        return org.apache.commons.lang.StringUtils.equals(this.dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }


    /**
     * 等于默认的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultDataSource() {
        return org.apache.commons.lang.StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }


    /**
     * 等于默认和安全组的数据权限
     *
     * @return true:相等
     */
    public boolean equalDefaultSecGrpDataSource() {
        return org.apache.commons.lang.StringUtils.equals(this.dataSource, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
    }
}
