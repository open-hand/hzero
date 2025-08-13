/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.provider.base;

import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


/**
 * BaseInsertProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class BaseInsertProvider extends MapperTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseInsertProvider.class);

    public BaseInsertProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    private static final String TRIM = "</trim>";

    /**
     * 插入全部,这段代码比较复杂，这里举个例子
     * CountryU生成的insert方法结构如下：
     * <pre>
     * &lt;bind name="countryname_bind" value='@java.util.UUID@randomUUID().toString().replace("-", "")'/&gt;
     * INSERT INTO country_u(id,countryname,countrycode) VALUES
     * &lt;trim prefix="(" suffix=")" suffixOverrides=","&gt;
     * &lt;if test="id != null"&gt;#{id,javaType=java.lang.Integer},&lt;/if&gt;
     * &lt;if test="id == null"&gt;#{id,javaType=java.lang.Integer},&lt;/if&gt;
     * &lt;if test="countryname != null"&gt;#{countryname,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countryname == null"&gt;#{countryname_bind,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countrycode != null"&gt;#{countrycode,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countrycode == null"&gt;#{countrycode,javaType=java.lang.String},&lt;/if&gt;
     * &lt;/trim&gt;
     * </pre>
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String insert(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //先处理cache或bind节点
        processKey(ms, entityClass, sql, columnSet);
        //如果启用监控，添加一个线程变量bind
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnSet) {
            if (createSelfMaintenanceFields(modifyAudit, versionAudit, sql, column)
                    || !column.isInsertable()) {
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            } else {
                //其他情况值仍然存在原property中
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况
            if (StringUtil.isNotEmpty(column.getSequenceName())) {
                sql.append(SqlHelper.getIfIsNull(column, getSeqNextVal(column) + " ,", false));
            } else if (column.isIdentity()) {
                // 这里为了兼容某些自增数据库  如果指定了自增，但是又不能插入null，例如Postgre SQL
//                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
                LOGGER.trace("ignore column.isIdentity()");
            } else if (column.isUuid()) {
                sql.append(SqlHelper.getIfIsNull(column, column.getColumnHolder(null, "_bind", ","), isNotEmpty()));
            } else {
                //当null的时候，如果不指定jdbcType，oracle可能会报异常，指定VARCHAR不影响其他
                sql.append(SqlHelper.getIfIsNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
        }
        sql.append(TRIM);
        return sql.toString();
    }

    /**
     * 插入不为null的字段,这段代码比较复杂，这里举个例子
     * CountryU生成的insertSelective方法结构如下：
     * <pre>
     * &lt;bind name="countryname_bind" value='@java.util.UUID@randomUUID().toString().replace("-", "")'/&gt;
     * INSERT INTO country_u
     * &lt;trim prefix="(" suffix=")" suffixOverrides=","&gt;
     * &lt;if test="id != null"&gt;id,&lt;/if&gt;
     * countryname,
     * &lt;if test="countrycode != null"&gt;countrycode,&lt;/if&gt;
     * &lt;/trim&gt;
     * VALUES
     * &lt;trim prefix="(" suffix=")" suffixOverrides=","&gt;
     * &lt;if test="id != null"&gt;#{id,javaType=java.lang.Integer},&lt;/if&gt;
     * &lt;if test="countryname != null"&gt;#{countryname,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countryname == null"&gt;#{countryname_bind,javaType=java.lang.String},&lt;/if&gt;
     * &lt;if test="countrycode != null"&gt;#{countrycode,javaType=java.lang.String},&lt;/if&gt;
     * &lt;/trim&gt;
     * </pre>
     * 这段代码可以注意对countryname的处理
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String insertSelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //先处理cache或bind节点
        processKey(ms, entityClass, sql, columnSet);
        //如果启用监控，添加一个线程变量bind
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnSet) {
            //如果启用监控，插入时更新值
            if (connectSql(modifyAudit, versionAudit, sql, column)) {
                continue;
            }
            if (StringUtil.isNotEmpty(column.getSequenceName()) || column.isUuid()) {
                sql.append(column.getColumn()).append(",");
            } else if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumn() + ","));
            } else {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", isNotEmpty()));
            }
        }
        sql.append(TRIM);
        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        appendSql(modifyAudit, versionAudit, sql, columnSet);
        sql.append(TRIM);
        return sql.toString();
    }

    private void appendSql(boolean modifyAudit, boolean versionAudit, StringBuilder sql, Set<EntityColumn> columnList) {
        for (EntityColumn column : columnList) {
            //如果启用监控，插入时更新值
            if (createSelfMaintenanceFields(modifyAudit, versionAudit, sql, column)
                    || !column.isInsertable()) {
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            } else {
                //其他情况值仍然存在原property中
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况
            if (StringUtil.isNotEmpty(column.getSequenceName())) {
                sql.append(SqlHelper.getIfIsNull(column, getSeqNextVal(column) + " ,", isNotEmpty()));
            } else if (column.isIdentity()) {
                // 这里为了兼容某些自增数据库  如果指定了自增，但是又不能插入null，例如Postgre SQL
//                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
                LOGGER.trace("ignore column.isIdentity()");
            } else if (column.isUuid()) {
                sql.append(SqlHelper.getIfIsNull(column, column.getColumnHolder(null, "_bind", ","), isNotEmpty()));
            }
        }
    }

    private boolean connectSql(boolean modifyAudit, boolean versionAudit, StringBuilder sql, EntityColumn column) {
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

    private void processKey(MappedStatement ms,
                            Class<?> entityClass,
                            StringBuilder sql,
                            Set<EntityColumn> columnList) {
        //Identity列只能有一个
        boolean hasIdentityKey = false;
        for (EntityColumn column : columnList) {
            if (column.isIdentity()
                    && !StringUtil.isNotEmpty(column.getSequenceName())) {
                //这种情况下,如果原先的字段有值,需要先缓存起来,否则就一定会使用自动增长
                //这是一个bind节点
                sql.append(SqlHelper.getBindCache(column));
                //如果是Identity列，就需要插入selectKey
                //如果已经存在Identity列，抛出异常
                if (hasIdentityKey) {
                    //jdbc类型只需要添加一次
                    if (column.getGenerator() != null && column.getGenerator().equals("JDBC")) {
                        continue;
                    }
                    throw new MapperException(ms.getId() + "对应的实体类"
                            + entityClass.getCanonicalName() + "中包含多个MySql的自动增长列,最多只能有一个!");
                }
                //插入selectKey
                newSelectKeyMappedStatement(ms, column);
                hasIdentityKey = true;
            } else if (column.isUuid()
                    && !StringUtil.isNotEmpty(column.getSequenceName())) {
                //uuid的情况，直接插入bind节点
                sql.append(SqlHelper.getBindValue(column, getUuid()));
            }
        }
    }

    private boolean dealWithIdentityColumn(MappedStatement ms, Class<?> entityClass,
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

    private boolean createSelfMaintenanceFields(boolean modifyAudit, boolean versionAudit, StringBuilder sql,
                                                EntityColumn column) {
        //如果启用监控，插入时更新值
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
        return false;
    }
}
