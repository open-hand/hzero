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

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.Set;

/**
 * BaseUpdateProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class BaseUpdateProvider extends MapperTemplate {

    public BaseUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 通过主键更新全部字段
     *
     * @param ms MappedStatement
     * @return String String
     * @throws Exception Exception
     */
    public String updateByPrimaryKey(MappedStatement ms) throws Exception {
        Class<?> entityClass = getEntityClass(ms);
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
            if (SqlHelper.TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                sql.append(SqlHelper.getTenantBind());
                break;
            }
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumnsAndVersion(entityClass, null, false, false));
        sql.append(SqlHelper.wherePrimaryAndVersion(entityClass));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms MappedStatement
     * @return String String
     * @throws Exception Exception
     */
    public String updateByPrimaryKeySelective(MappedStatement ms) throws Exception {
        Class<?> entityClass = getEntityClass(ms);
        boolean modifyAudit = entityClass.isAnnotationPresent(ModifyAudit.class);
        boolean versionAudit = entityClass.isAnnotationPresent(VersionAudit.class);
        StringBuilder sql = new StringBuilder();
        if (modifyAudit || versionAudit) {
            sql.append(SqlHelper.getAuditBind());
        }
        for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
            if (SqlHelper.TENANT_ID.equalsIgnoreCase(column.getColumn())) {
                sql.append(SqlHelper.getTenantBind());
                break;
            }
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumnsAndVersion(entityClass, null, true, isNotEmpty()));
        sql.append(SqlHelper.wherePrimaryAndVersion(entityClass));
        return sql.toString();
    }
}
