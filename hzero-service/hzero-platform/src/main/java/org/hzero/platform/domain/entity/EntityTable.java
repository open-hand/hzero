package org.hzero.platform.domain.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.entity.dto.EntityColumnDTO;
import org.hzero.boot.platform.entity.dto.EntityTableDTO;
import org.springframework.beans.BeanUtils;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体表
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
@ApiModel("实体表")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_entity_table")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityTable extends AuditDomain {

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

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 拷贝字段
     *
     * @param entityTableDTOList
     * @return
     */
    public static List<EntityTable> copyProperty(List<EntityTableDTO> entityTableDTOList) {
        //EntityTable
        List<EntityTable> result = new ArrayList<>(entityTableDTOList.size());
        entityTableDTOList.forEach(item -> {
            EntityTable entityTable = new EntityTable();
            BeanUtils.copyProperties(item, entityTable);
            //EntityColumn
            List<EntityColumnDTO> entityColumnDTOList = item.getEntityColumnList();
            if (CollectionUtils.isNotEmpty(entityColumnDTOList)) {
                List<EntityColumn> entityColumnList = EntityColumn.copyProperty(entityColumnDTOList);
                entityTable.setEntityColumnList(entityColumnList);
            }
            result.add(entityTable);
        });
        return result;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
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
    @ApiModelProperty(value = "删除标识，0-未删除，1-已删除", required = true)
    @NotNull
    private Integer deletedFlag;
    @Transient
    private List<EntityColumn> entityColumnList;

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

    public List<EntityColumn> getEntityColumnList() {
        return entityColumnList;
    }

    public void setEntityColumnList(List<EntityColumn> entityColumnList) {
        this.entityColumnList = entityColumnList;
    }

    public Integer getDeletedFlag() {
        return deletedFlag;
    }

    public void setDeletedFlag(Integer deletedFlag) {
        this.deletedFlag = deletedFlag;
    }
}
