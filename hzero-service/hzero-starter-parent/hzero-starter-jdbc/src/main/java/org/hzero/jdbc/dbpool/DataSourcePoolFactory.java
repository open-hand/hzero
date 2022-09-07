package org.hzero.jdbc.dbpool;

import org.hzero.jdbc.constant.QueryConstants;

import io.choerodon.core.exception.CommonException;

/**
 * 数据源连接池工厂
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:46:20
 */
public class DataSourcePoolFactory {

    private DataSourcePoolFactory() {
    }

    public static DataSourcePoolWrapper create(final String className) {
        try {
            return (DataSourcePoolWrapper) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            throw new CommonException(QueryConstants.Error.ERROR_POOL_FACTORY_LOAD_CLASS, ex);
        }
    }
}
