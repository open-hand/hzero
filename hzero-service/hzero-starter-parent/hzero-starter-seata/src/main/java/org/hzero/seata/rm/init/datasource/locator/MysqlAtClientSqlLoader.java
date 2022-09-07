package org.hzero.seata.rm.init.datasource.locator;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 4:35 下午
 */
public class MysqlAtClientSqlLoader extends AtClientSqlLoader {

    public MysqlAtClientSqlLoader() {
        super("mysql.sql");
    }

    public MysqlAtClientSqlLoader(ClassLoader classLoader) {
        super("mysql.sql", classLoader);
    }

    @Override
    public String getSqlType() {
        return "MySQL";
    }
}
