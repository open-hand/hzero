package io.choerodon.mybatis.provider.optional;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.constant.InsertOrUpdateConstant;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Set;


/**
 * Created by xausky on 3/20/17.
 */
public class OptionalProvider extends MapperTemplate {

    public OptionalProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    private static final String OPTIONAL = "optional";
    private static final String TENANT_ID = "tenant_id";

    /**
     * 选择性插入列，只插入传入的列
     *
     * @param ms MappedStatement
     * @return String
     */
    public String insertOptional(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //Identity列只能有一个
        Boolean hasIdentityKey = false;
        //先处理cache或bind节点
        dealCacheOrBindNode(ms, entityClass, sql, columnList, hasIdentityKey);
        //如果启用监控，添加一个线程变量bind
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        sql.append(SqlHelper.getOptionalBind());
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        dealSelfMaintainFileds(modifyAudit, versionAudit, sql, columnList);
        sql.append("</trim>");
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            //如果启用监控，插入时更新值
            if (fillSelfMaintainValue(modifyAudit, versionAudit, sql, column)) {
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况ss
            if (StringUtil.isNotEmpty(column.getSequenceName())) {
                sql.append(SqlHelper.getIfIsNull(column, getSeqNextVal(column) + " ,", false));
            } else if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
            } else if (column.isUuid()) {
                sql.append(SqlHelper.getIfIsNull(column, column.getColumnHolder("_bind", ","), isNotEmpty()));
            } else {
                //当null的时候，如果不指定jdbcType，oracle可能会报异常，指定VARCHAR不影响其他
                sql.append(SqlHelper.getIfContains(OPTIONAL, column, column.getColumnHolder(null, null, ",")));
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    private boolean fillSelfMaintainValue(boolean modifyAudit, boolean versionAudit,
                                          StringBuilder sql, EntityColumn column) {
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(column.getProperty())) {
            String columnName = column.getProperty();
            if (columnName.equals(InsertOrUpdateConstant.CREATION_DATE)) {
                sql.append(InsertOrUpdateConstant.AUDIT_NOW);
            } else if (columnName.equals(InsertOrUpdateConstant.CREATED_BY)) {
                sql.append(InsertOrUpdateConstant.AUDIT_USER);
            } else if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_DATE)) {
                sql.append(InsertOrUpdateConstant.AUDIT_NOW);
            } else if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_BY)) {
                sql.append(InsertOrUpdateConstant.AUDIT_USER);
            } else {
                throw new MapperException(InsertOrUpdateConstant.MODIFY_EXCEPTION + columnName);
            }
            return true;
        }
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(column.getProperty())) {
            String columnName = column.getProperty();
            if (columnName.equals(InsertOrUpdateConstant.OBJECT_VERSION_NUMBER)) {
                sql.append("1,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.VERSION_EXCEPTION + columnName);
            }
            return true;
        }
        return !column.isInsertable();
    }

    private void dealSelfMaintainFileds(boolean modifyAudit, boolean versionAudit,
                                        StringBuilder sql, Set<EntityColumn> columnList) {
        for (EntityColumn column : columnList) {
            //如果启用监控，插入时更新值
            if (connectedWithColumn(modifyAudit, versionAudit, sql, column)) {
                continue;
            }
            if (StringUtil.isNotEmpty(column.getSequenceName()) || column.isIdentity() || column.isUuid()) {
                sql.append(column.getColumn() + ",");
            } else {
                sql.append(SqlHelper.getIfContains(OPTIONAL, column, column.getColumn() + ","));
            }
        }
    }

    private boolean connectedWithColumn(boolean modifyAudit, boolean versionAudit, StringBuilder sql,
                                        EntityColumn column) {
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(column.getProperty())) {
            String columnName = column.getProperty();
            if (columnName.equals(InsertOrUpdateConstant.CREATION_DATE)) {
                sql.append("CREATION_DATE,");
            } else if (columnName.equals(InsertOrUpdateConstant.CREATED_BY)) {
                sql.append("CREATED_BY,");
            } else if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_DATE)) {
                sql.append("LAST_UPDATE_DATE,");
            } else if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_BY)) {
                sql.append("LAST_UPDATED_BY,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.MODIFY_EXCEPTION + columnName);
            }
            return true;
        }
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(column.getProperty())) {
            String columnName = column.getProperty();
            if (columnName.equals(InsertOrUpdateConstant.OBJECT_VERSION_NUMBER)) {
                sql.append("OBJECT_VERSION_NUMBER,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.VERSION_EXCEPTION + columnName);
            }
            return true;
        }
        return !column.isInsertable();
    }

    private void dealCacheOrBindNode(MappedStatement ms, Class<?> entityClass, StringBuilder sql,
                                     Set<EntityColumn> columnList, Boolean hasIdentityKey) {
        for (EntityColumn column : columnList) {
            if (column.isInsertable()) {
                if (!StringUtil.isNotEmpty(column.getSequenceName())
                        && column.isIdentity()) {
                    //这种情况下,如果原先的字段有值,需要先缓存起来,否则就一定会使用自动增长
                    //这是一个bind节点
                    sql.append(SqlHelper.getBindCache(null, column));
                    //如果是Identity列，就需要插入selectKey
                    //如果已经存在Identity列，抛出异常
                    if (dealWithIdentityColumns(ms, entityClass, hasIdentityKey, column)) {
                        continue;
                    }
                    //插入selectKey
                    newSelectKeyMappedStatement(null, ms, column);
                    hasIdentityKey = true;
                } else if (!StringUtil.isNotEmpty(column.getSequenceName())
                        && column.isUuid()) {
                    //uuid的情况，直接插入bind节点
                    sql.append(SqlHelper.getBindValue(column, getUuid()));
                }
            }

        }
    }

    private boolean dealWithIdentityColumns(MappedStatement ms, Class<?> entityClass,
                                            Boolean hasIdentityKey, EntityColumn column) {
        if (hasIdentityKey) {
            //jdbc类型只需要添加一次
            if (column.getGenerator() != null && column.getGenerator().equals("JDBC")) {
                return true;
            }
            throw new MapperException(ms.getId() + "对应的实体类"
                    + entityClass.getCanonicalName() + "中包含多个MySql的自动增长列,最多只能有一个!");
        }
        return false;
    }

    /**
     * 选择性更新，只更新传入的列
     *
     * @param ms MappedStatement
     * @return String
     */
    public String updateOptional(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        sql.append(SqlHelper.getOptionalBind());
        for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
            if (SqlHelper.TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                sql.append(SqlHelper.getTenantBind());
                break;
            }
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        sql.append("<set>");
        for (EntityColumn column : columnList) {
            if (dealWithColumn(sql, modifyAudit, versionAudit, column)) {
                continue;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                    sql.append("<if test=\"!__tenantLimit or (__tenantId == null and (__tenantIds == null or __tenantIds.isEmpty()))\">");
                }
                sql.append(SqlHelper.getIfContains(OPTIONAL, column, column.getColumnEqualsHolder() + ","));
                if (TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                    sql.append("</if>");
                }
            }
        }
        sql.append("</set>");
        sql.append("<where>");
        //获取全部列
        Set<EntityColumn> primaryColumns = EntityHelper.getPkColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : primaryColumns) {
            sql.append(" AND ").append(column.getColumnEqualsHolder());
        }
        for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
            if (TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                sql.append(SqlHelper.getTenantLimit(column, false, false));
                break;
            }
        }
        if (versionAudit) {
            sql.append(" AND OBJECT_VERSION_NUMBER = #{objectVersionNumber}");
        }
        sql.append("</where>");
        return sql.toString();
    }

    private boolean dealWithColumn(StringBuilder sql, boolean modifyAudit,
                                   boolean versionAudit, EntityColumn column) {
        String columnName = column.getProperty();
        //如果启用监控，插入时更新值
        if (modifyAudit && SqlHelper.MODIFY_AUDIT_FIELDS.contains(column.getProperty())) {
            if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_DATE)) {
                sql.append("LAST_UPDATE_DATE = #{audit.now,jdbcType=TIMESTAMP},");
            } else if (columnName.equals(InsertOrUpdateConstant.LAST_UPDATE_BY)) {
                sql.append("LAST_UPDATED_BY = #{audit.user,jdbcType=BIGINT},");
            }
            return true;
        }
        if (versionAudit && SqlHelper.VERSION_AUDIT_FIELDS.contains(column.getProperty())) {
            if (columnName.equals(InsertOrUpdateConstant.OBJECT_VERSION_NUMBER)) {
                sql.append("OBJECT_VERSION_NUMBER = OBJECT_VERSION_NUMBER+1,");
            } else {
                throw new MapperException(InsertOrUpdateConstant.VERSION_EXCEPTION + columnName);
            }
            return true;
        }
        return false;
    }

}
