package io.choerodon.mybatis.provider.special;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.constant.InsertOrUpdateConstant;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

import javax.persistence.GeneratedValue;
import java.util.Set;

/**
 * SpecialProvider实现类，特殊方法实现类
 *
 * @author liuzh
 */
public class SpecialProvider extends MapperTemplate {

    public SpecialProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量插入，数据库需要设置主键自增策略，id不会被赋值
     *
     * @param ms
     */
    public String insertList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //是否加了自定义注解，自动维护五个自维护字段
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        insertColumns(modifyAudit, versionAudit, sql, columnList);
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            insertColumnValue(modifyAudit, versionAudit, sql, column);
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }

    private void insertColumnValue(boolean modifyAudit, boolean versionAudit, StringBuilder sql, EntityColumn column) {
        String columnName = column.getProperty();
        boolean generated = column.getField().isAnnotationPresent(GeneratedValue.class) && !column.isUuid();
        if (generated && column.isId()) {
            return;
        }
        if (processModifyAuditFields(modifyAudit, sql, columnName)) {
            return;
        }
        if (processVersionAuditFields(versionAudit, sql, columnName)) {
            return;
        }
        if (column.isInsertable()
                && !SqlHelper.MODIFY_AUDIT_FIELDS.contains(columnName)
                && !SqlHelper.VERSION_AUDIT_FIELDS.contains(columnName)) {
            if (column.isUuid()) {
                sql.append(SqlHelper.getIfIsNull("record", column, SqlHelper.buildOGNL(getUuid(), null), Boolean.TRUE))
                        .append(SqlHelper.getIfNotNull("record", column, column.getColumnHolder("record"), Boolean.FALSE))
                        .append(",");
            } else {
                sql.append(column.getColumnHolder("record")).append(",");
            }
        }
    }

    private boolean processVersionAuditFields(boolean versionAudit, StringBuilder sql, String columnName) {
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(columnName)) {
            if (InsertOrUpdateConstant.OBJECT_VERSION_NUMBER.equals(columnName)) {
                sql.append("1,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.VERSION_EXCEPTION + columnName);
            }
            return true;
        }
        return false;
    }

    private boolean processModifyAuditFields(boolean modifyAudit, StringBuilder sql, String columnName) {
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(columnName)) {
            if (InsertOrUpdateConstant.CREATION_DATE.equals(columnName)) {
                sql.append(InsertOrUpdateConstant.AUDIT_NOW);
            } else if (InsertOrUpdateConstant.CREATED_BY.equals(columnName)) {
                sql.append(InsertOrUpdateConstant.AUDIT_USER);
            } else if (InsertOrUpdateConstant.LAST_UPDATE_DATE.equals(columnName)) {
                sql.append(InsertOrUpdateConstant.AUDIT_NOW);
            } else if (InsertOrUpdateConstant.LAST_UPDATE_BY.equals(columnName)) {
                sql.append(InsertOrUpdateConstant.AUDIT_USER);
            } else {
                throw new MapperException(InsertOrUpdateConstant.MODIFY_EXCEPTION + columnName);
            }
            return true;
        }
        return false;
    }

    private void insertColumns(boolean modifyAudit, boolean versionAudit, StringBuilder sql, Set<EntityColumn> columnList) {
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            //主键自增，跳过id
            boolean generated = column.getField().isAnnotationPresent(GeneratedValue.class) && !column.isUuid();
            if (generated && column.isId()) {
                continue;
            }
            String columnName = column.getProperty();
            if (processModifyAuditValues(modifyAudit, sql, columnName)) {
                continue;
            }
            if (processVersionAuditValues(versionAudit, sql, columnName)) {
                continue;
            }
            if (!SqlHelper.MODIFY_AUDIT_FIELDS.contains(columnName)
                    && !SqlHelper.VERSION_AUDIT_FIELDS.contains(columnName)) {
                sql.append(column.getColumn()).append(",");
            }
        }
        sql.append("</trim>");
    }

    private boolean processVersionAuditValues(boolean versionAudit, StringBuilder sql, String columnName) {
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(columnName)) {
            if (InsertOrUpdateConstant.OBJECT_VERSION_NUMBER.equals(columnName)) {
                sql.append("object_version_number,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.VERSION_EXCEPTION + columnName);
            }
            return true;
        }
        return false;
    }

    private boolean processModifyAuditValues(boolean modifyAudit, StringBuilder sql, String columnName) {
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(columnName)) {
            if (InsertOrUpdateConstant.CREATION_DATE.equals(columnName)) {
                sql.append("creation_date,");
            } else if (InsertOrUpdateConstant.CREATED_BY.equals(columnName)) {
                sql.append("created_by,");
            } else if (InsertOrUpdateConstant.LAST_UPDATE_DATE.equals(columnName)) {
                sql.append("last_update_date,");
            } else if (InsertOrUpdateConstant.LAST_UPDATE_BY.equals(columnName)) {
                sql.append("last_updated_by,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.MODIFY_EXCEPTION + columnName);
            }
            return true;
        }
        return false;
    }

    /**
     * 插入，主键id，自增
     *
     * @param ms
     */
    public String insertUseGeneratedKeys(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(SqlHelper.insertValuesColumns(entityClass, true, false, false));
        return sql.toString();
    }
}
