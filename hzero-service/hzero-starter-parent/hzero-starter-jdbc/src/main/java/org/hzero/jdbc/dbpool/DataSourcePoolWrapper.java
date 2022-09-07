package org.hzero.jdbc.dbpool;


import org.hzero.jdbc.statement.DatasourceStatement;

import javax.sql.DataSource;

/**
 * 数据源连接包装器
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:45:37
 */
public interface DataSourcePoolWrapper {
    DataSource wrap(DatasourceStatement rptDs);
}
