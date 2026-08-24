package org.hzero.seata.rm.init.datasource;

import javax.sql.DataSource;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 4:56 下午
 */
public interface SqlTypeRecognizer {

    String recognizeSqlType(DataSource dataSource);

}
