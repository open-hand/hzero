package org.hzero.boot.platform.entity.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.domain.EntityTable;

import org.hzero.core.base.BaseConstants;

/**
 * 实体表
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
public class EntityTableDTO  extends AuditDomain{

    public static final String FIELD_ENTITY_TABLE_ID = "entityTableId";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_ENTITY_CLASS = "entityClass";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_TABLE_CATALOG = "tableCatalog";
    public static final String FIELD_TABLE_SCHEMA = "tableSchema";
    public static final String FIELD_TABLE_COMMENT = "tableComment";
    public static final String FIELD_MULTI_LANGUAGE_FLAG = "multiLanguageFlag";
    public static final String FIELD_MULTI_LANGUAGE_UNIQUE_FLAG = "multiLanguageUniqueFlag";
    public static final String FIELD_MULTI_LANGUAGE_TABLE_NAME = "multiLanguageTableName";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_DELETED_FLAG = "deletedFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 拷贝相关字段
     *
     * @param entityTable 实体表对象
     */
    public void copyPropertiesFromEntityTable(EntityTable entityTable) {
        if (entityTable == null) {
            return;
        }
        this.deletedFlag=BaseConstants.Flag.NO;
        this.entityClass = entityTable.getEntityClass().toString();
        this.tableName = entityTable.getName();
        this.tableSchema = entityTable.getSchema();
        this.multiLanguageFlag = entityTable.isMultiLanguage() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
        this.multiLanguageUniqueFlag = entityTable.isMultiLanguageUnique() ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
        this.multiLanguageTableName = entityTable.getMultiLanguageTableName();
        this.tableCatalog = entityTable.getCatalog();
        //表说明
        ApiModel annotation = entityTable.getEntityClass().getAnnotation(ApiModel.class);
        if (annotation != null) {
            this.tableComment = annotation.value();
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(serviceName)
                .append(entityClass)
                .append(tableName)
                .append(tableCatalog)
                .append(tableSchema)
                .append(tableComment)
                .append(remark)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "EntityTableDTO{" +
                "entityTableId=" + entityTableId +
                ", serviceName='" + serviceName + '\'' +
                ", entityClass='" + entityClass + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableCatalog='" + tableCatalog + '\'' +
                ", tableSchema='" + tableSchema + '\'' +
                ", tableComment='" + tableComment + '\'' +
                ", multiLanguageFlag=" + multiLanguageFlag +
                ", multiLanguageUniqueFlag=" + multiLanguageUniqueFlag +
                ", multiLanguageTableName='" + multiLanguageTableName + '\'' +
                ", remark='" + remark + '\'' +
                ", entityColumnList=" + entityColumnList +
                '}';
    }
//
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long entityTableId;
    @ApiModelProperty(value = "服务名", required = true)
    @NotBlank
    private String serviceName;
    @ApiModelProperty(value = "实体类完全限定名", required = true)
    @NotBlank
    private String entityClass;
    @ApiModelProperty(value = "表名称", required = true)
    @NotBlank
    private String tableName;
    @ApiModelProperty(value = "表目录")
    private String tableCatalog;
    @ApiModelProperty(value = "表模式")
    private String tableSchema;
    @ApiModelProperty(value = "表说明")
    private String tableComment;
    @ApiModelProperty(value = "是否多语言表", required = true)
    @NotNull
    private Integer multiLanguageFlag;
    @ApiModelProperty(value = "是否存在多语言字段作为唯一性字段", required = true)
    @NotNull
    private Integer multiLanguageUniqueFlag;
    @ApiModelProperty(value = "多语言表名称")
    private String multiLanguageTableName;
    @ApiModelProperty(value = "备注说明")
    private String remark;
    @ApiModelProperty(value = "表所有列")
    private List<EntityColumnDTO> entityColumnList;
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
    public Long getEntityTableId() {
        return entityTableId;
    }

    public void setEntityTableId(Long entityTableId) {
        this.entityTableId = entityTableId;
    }

    /**
     * @return 服务名
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return 实体类完全限定名
     */
    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return 表名称
     */
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return 表目录
     */
    public String getTableCatalog() {
        return tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    /**
     * @return 表模式
     */
    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    /**
     * @return 表说明
     */
    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    /**
     * @return 是否多语言表
     */
    public Integer getMultiLanguageFlag() {
        return multiLanguageFlag;
    }

    public void setMultiLanguageFlag(Integer multiLanguageFlag) {
        this.multiLanguageFlag = multiLanguageFlag;
    }

    /**
     * @return 是否存在多语言字段作为唯一性字段
     */
    public Integer getMultiLanguageUniqueFlag() {
        return multiLanguageUniqueFlag;
    }

    public void setMultiLanguageUniqueFlag(Integer multiLanguageUniqueFlag) {
        this.multiLanguageUniqueFlag = multiLanguageUniqueFlag;
    }

    /**
     * @return 多语言表名称
     */
    public String getMultiLanguageTableName() {
        return multiLanguageTableName;
    }

    public void setMultiLanguageTableName(String multiLanguageTableName) {
        this.multiLanguageTableName = multiLanguageTableName;
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

    public List<EntityColumnDTO> getEntityColumnList() {
        return entityColumnList;
    }

    public void setEntityColumnList(List<EntityColumnDTO> entityColumnList) {
        this.entityColumnList = entityColumnList;
    }

    public Integer getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Integer deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
