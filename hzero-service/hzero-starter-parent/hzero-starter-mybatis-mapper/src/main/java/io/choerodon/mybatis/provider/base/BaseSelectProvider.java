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

import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.SQLException;

/**
 * BaseSelectProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class BaseSelectProvider extends MapperTemplate {

    public BaseSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 查询
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String selectOne(MappedStatement ms) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumnsTl(entityClass, isNotEmpty()));
        sql = limitOne(sql);
        return sql.toString();
    }

    private StringBuilder limitOne(StringBuilder sql) throws SQLException {

        DbType dbType = getDbType();
        if (DbType.MYSQL.getValue().equals(dbType.getValue())
                || DbType.H2.getValue().equals(dbType.getValue())) {
            sql.append(" LIMIT 1");
        } else if (DbType.ORACLE.getValue().equals(dbType.getValue())) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT * FROM (");
            sb.append(sql);
            sb.append(") TEM_PAGE WHERE ROWNUM &lt;= 1");
            sql = sb;
        } else if (DbType.SQLSERVER.getValue().equals(dbType.getValue())
                || DbType.HANA.getValue().equals(dbType.getValue())) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT TOP 1 * FROM (");
            sb.append(sql.toString());
            sb.append(") t");
            sql = sb;
        } else {
            throw new SQLException("unsupported databases type!");
        }
        return sql;
    }

    /**
     * 查询
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String select(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumnsTl(entityClass, isNotEmpty()));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }

    /**
     * 根据主键进行查询
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String selectByPrimaryKey(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePkColumnsTl(entityClass));
        return sql.toString();
    }

    /**
     * 查询总数
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String selectCount(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectCount(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumnsTl(entityClass, isNotEmpty()));
        return sql.toString();
    }

    /**
     * 根据主键查询总数
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String existsWithPrimaryKey(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCountExists(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePkColumnsTl(entityClass));
        return sql.toString();
    }

    /**
     * 查询全部结果
     *
     * @param ms MappedStatement
     * @return String String
     */
    public String selectAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.getWhereTenantLimit(entityClass));
        sql.append(SqlHelper.orderByDefault(entityClass));
        return sql.toString();
    }
}
