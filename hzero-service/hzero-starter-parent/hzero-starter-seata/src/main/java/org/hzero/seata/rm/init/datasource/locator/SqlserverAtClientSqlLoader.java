package org.hzero.seata.rm.init.datasource.locator;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 4:38 下午
 */
public class SqlserverAtClientSqlLoader extends AtClientSqlLoader {

    public SqlserverAtClientSqlLoader() {
        super("sqlserver.sql");
    }

    public SqlserverAtClientSqlLoader(ClassLoader classLoader) {
        super("sqlserver.sql", classLoader);
    }

    @Override
    public String getSqlType() {
        return "Microsoft SQL Server";
    }
}
