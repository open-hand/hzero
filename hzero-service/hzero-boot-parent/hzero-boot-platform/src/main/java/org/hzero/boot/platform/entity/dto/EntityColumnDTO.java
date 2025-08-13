package org.hzero.boot.platform.entity.dto;

import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.domain.EntityColumn;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import javax.validation.constraints.NotBlank;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;

/**
 * 实体列
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */

public class EntityColumnDTO extends AuditDomain {

    public static final String FIELD_ENTITY_COLUMN_ID = "entityColumnId";
    public static final String FIELD_ENTITY_TABLE_ID = "entityTableId";
    public static final String FIELD_FIELD_NAME = "fieldName";
    public static final String FIELD_JAVA_TYPE = "javaType";
    public static final String FIELD_COLUMN_NAME = "columnName";
    public static final String FIELD_COLUMN_COMMENT = "columnComment";
    public static final String FIELD_JDBC_TYPE = "jdbcType";
    public static final String FIELD_TYPE_HANDLER = "typeHandler";
    public static final String FIELD_PK_ID_FLAG = "pkIdFlag";
    public static final String FIELD_PK_SEQUENCE_NAME = "pkSequenceName";
    public static final String FIELD_PK_UUID_FLAG = "pkUuidFlag";
    public static final String FIELD_PK_IDENTITY_FLAG = "pkIdentityFlag";
    public static final String FIELD_PK_GENERATOR = "pkGenerator";
    public static final String FIELD_UNIQUE_FLAG = "uniqueFlag";
    public static final String FIELD_TRANSIENT_FLAG = "transientFlag";
    public static final String FIELD_DATA_SECURITY_FLAG = "dataSecurityFlag";
    public static final String FIELD_BLOB_FLAG = "blobFlag";
    public static final String FIELD_LOV_CODE = "lovCode";
    public static final String FIELD_MULTI_LANGUAGE_FLAG = "multiLanguageFlag";
    public static final String FIELD_REMARK = "remark";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    public void copyPropertiesFromEntityColumn(EntityColumn entityColumn, Integer uniqueFlag, Integer dataSecurityFlag, Integer transientFlag) {
        this.deletedFlag=BaseConstants.Flag.NO;
        this.fieldName = entityColumn.getProperty();
        this.javaType = entityColumn.getJavaType().toString();
        this.columnName = entityColumn.getColumn();
        ApiModelProperty apiModelAnnotation = entityColumn.getField().getField().getDeclaredAnnotation(ApiModelProperty.class);
        if (apiModelAnnotation != null) {
            this.columnComment = apiModelAnnotation.value();
        }

        JdbcType jdbcType = entityColumn.getJdbcType();
        if (jdbcType != null) {
            this.jdbcType = jdbcType.name();
        }
        Class<? extends TypeHandler<?>> typeHandler = entityColumn.getTypeHandler();
        if (typeHandler != null) {
            this.typeHandler = entityColumn.getTypeHandler().getName();
        }


        this.pkIdFlag = entityColumn.isId() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
        this.pkSequenceName = entityColumn.getSequenceName();
        this.pkUuidFlag = entityColumn.isUuid() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
        this.pkIdentityFlag = entityColumn.isIdentity() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
        this.pkGenerator = entityColumn.getGenerator();
        this.blobFlag = entityColumn.isBlob() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;

        //获取lovCode
        LovValue lovValueAnnotation = entityColumn.getField().getField().getDeclaredAnnotation(LovValue.class);
        if (lovValueAnnotation != null) {
            this.lovCode = lovValueAnnotation.lovCode();
        }
        //是否是transient列
        this.transientFlag = transientFlag;
        //是否唯一列
        this.uniqueFlag = uniqueFlag;
        //是否是是否敏感字段，
        this.dataSecurityFlag = dataSecurityFlag;
        this.multiLanguageFlag = entityColumn.isMultiLanguage() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .append(javaType)
                .append(columnName)
                .append(columnComment)
                .append(jdbcType)
                .append(typeHandler)
                .append(pkIdFlag)
                .append(pkSequenceName)
                .append(pkUuidFlag)
                .append(pkIdentityFlag)
                .append(pkGenerator)
                .append(uniqueFlag)
                .append(transientFlag)
                .append(dataSecurityFlag)
                .append(blobFlag)
                .append(lovCode)
                .append(multiLanguageFlag)
                .append(remark)
                .toHashCode();
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long entityColumnId;
    @ApiModelProperty(value = "实体表ID", required = true)
    @NotNull
    private Long entityTableId;
    @ApiModelProperty(value = "字段名称", required = true)
    @NotBlank
    private String fieldName;
    @ApiModelProperty(value = "字段Java类型", required = true)
    @NotBlank
    private String javaType;
    @ApiModelProperty(value = "列名称")
    private String columnName;
    @ApiModelProperty(value = "列说明")
    private String columnComment;
    @ApiModelProperty(value = "JDBC类型")
    private String jdbcType;
    @ApiModelProperty(value = "类型转换器")
    private String typeHandler;
    @ApiModelProperty(value = "是否主键-@Id", required = true)
    @NotNull
    private Integer pkIdFlag;
    @ApiModelProperty(value = "序列名字-序列号生成器SequenceGenerator")
    private String pkSequenceName;
    @ApiModelProperty(value = "主键生成方式是否UUID方式", required = true)
    @NotNull
    private Integer pkUuidFlag;
    @ApiModelProperty(value = "主键生成方式Identity方式", required = true)
    @NotNull
    private Integer pkIdentityFlag;
    @ApiModelProperty(value = "主键生成器-GeneratedValue.generator/UUID/JDBC等")
    private String pkGenerator;
    @ApiModelProperty(value = "是否唯一列", required = true)
    @NotNull
    private Integer uniqueFlag;
    @ApiModelProperty(value = "是否非数据库字段", required = true)
    @NotNull
    private Integer transientFlag;
    @ApiModelProperty(value = "是否敏感字段", required = true)
    @NotNull
    private Integer dataSecurityFlag;
    @ApiModelProperty(value = "是否Blob", required = true)
    @NotNull
    private Integer blobFlag;
    @ApiModelProperty(value = "LOV代码")
    private String lovCode;
    @ApiModelProperty(value = "是否多语言字段", required = true)
    @NotNull
    private Integer multiLanguageFlag;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "删除标识，0-未删除，1-已删除", required = true)
    @NotNull
    private Integer deletedFlag;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------


    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getEntityColumnId() {
        return entityColumnId;
    }

    public void setEntityColumnId(Long entityColumnId) {
        this.entityColumnId = entityColumnId;
    }

    /**
     * @return 实体表ID
     */
    public Long getEntityTableId() {
        return entityTableId;
    }

    public void setEntityTableId(Long entityTableId) {
        this.entityTableId = entityTableId;
    }

    /**
     * @return 字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return 字段Java类型
     */
    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * @return 列名称
     */
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return 列说明
     */
    public String getColumnComment() {
        return columnComment;
    }

    public void setColumnComment(String columnComment) {
        this.columnComment = columnComment;
    }

    /**
     * @return JDBC类型
     */
    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    /**
     * @return 类型转换器
     */
    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    /**
     * @return 是否主键-@Id
     */
    public Integer getPkIdFlag() {
        return pkIdFlag;
    }

    public void setPkIdFlag(Integer pkIdFlag) {
        this.pkIdFlag = pkIdFlag;
    }

    /**
     * @return 序列名字-序列号生成器SequenceGenerator
     */
    public String getPkSequenceName() {
        return pkSequenceName;
    }

    public void setPkSequenceName(String pkSequenceName) {
        this.pkSequenceName = pkSequenceName;
    }

    /**
     * @return 主键生成方式是否UUID方式
     */
    public Integer getPkUuidFlag() {
        return pkUuidFlag;
    }

    public void setPkUuidFlag(Integer pkUuidFlag) {
        this.pkUuidFlag = pkUuidFlag;
    }

    /**
     * @return 主键生成方式Identity方式
     */
    public Integer getPkIdentityFlag() {
        return pkIdentityFlag;
    }

    public void setPkIdentityFlag(Integer pkIdentityFlag) {
        this.pkIdentityFlag = pkIdentityFlag;
    }

    /**
     * @return 主键生成器-GeneratedValue.generator/UUID/JDBC等
     */
    public String getPkGenerator() {
        return pkGenerator;
    }

    public void setPkGenerator(String pkGenerator) {
        this.pkGenerator = pkGenerator;
    }

    /**
     * @return 是否唯一列
     */
    public Integer getUniqueFlag() {
        return uniqueFlag;
    }

    public void setUniqueFlag(Integer uniqueFlag) {
        this.uniqueFlag = uniqueFlag;
    }

    /**
     * @return 是否非数据库字段
     */
    public Integer getTransientFlag() {
        return transientFlag;
    }

    public void setTransientFlag(Integer transientFlag) {
        this.transientFlag = transientFlag;
    }

    /**
     * @return 是否敏感字段
     */
    public Integer getDataSecurityFlag() {
        return dataSecurityFlag;
    }

    public void setDataSecurityFlag(Integer dataSecurityFlag) {
        this.dataSecurityFlag = dataSecurityFlag;
    }

    /**
     * @return 是否Blob
     */
    public Integer getBlobFlag() {
        return blobFlag;
    }

    public void setBlobFlag(Integer blobFlag) {
        this.blobFlag = blobFlag;
    }

    /**
     * @return LOV代码
     */
    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    /**
     * @return 是否多语言字段
     */
    public Integer getMultiLanguageFlag() {
        return multiLanguageFlag;
    }

    public void setMultiLanguageFlag(Integer multiLanguageFlag) {
        this.multiLanguageFlag = multiLanguageFlag;
    }

    /**
     * @return 备注说明
     */
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Integer deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
