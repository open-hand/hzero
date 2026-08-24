package org.hzero.jdbc;

import io.choerodon.core.exception.CommonException;
import org.hzero.jdbc.constant.DBPoolTypeEnum;
import org.hzero.jdbc.constant.DatabaseTypeEnum;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.statement.DatasourceStatement;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * 报表查询器工厂方法类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:43:40
 */
public class QueryFactory {

    public static Query create(Long tenantId, String datasourceCode, String driverClass, String datasourceUrl, String username, String password, DatabaseTypeEnum dbType, DBPoolTypeEnum dbPoolType, Map<String, Object> options) {
        // 设置查询类和连接池类
        String queryClass = String.format(QueryConstants.Datasource.QUERYER_TEMPLATE, dbType.getValue());
        String dbPoolClass = String.format(QueryConstants.Datasource.DBPOOL_TEMPLATE, dbPoolType.getValue());
        return create(new DatasourceStatement(tenantId, datasourceCode, driverClass, datasourceUrl, username, password, queryClass, dbPoolClass, options));
    }

    public static Query create(DatasourceStatement dataSource) {
        try {
            Class<?> clazz = Class.forName(dataSource.getQueryerClass());
            Constructor<?> constructor = clazz.getConstructor(DatasourceStatement.class);
            return (Query) constructor.newInstance(dataSource);
        } catch (Exception ex) {
            throw new CommonException(QueryConstants.Error.ERROR_QUERYER_NOT_FOUND, ex);
        }
    }

}
