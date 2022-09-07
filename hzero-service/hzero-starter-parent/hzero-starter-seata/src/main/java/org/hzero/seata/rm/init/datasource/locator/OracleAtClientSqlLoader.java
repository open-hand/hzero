package org.hzero.seata.rm.init.datasource.locator;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 4:34 下午
 */
public class OracleAtClientSqlLoader extends AtClientSqlLoader {

    public OracleAtClientSqlLoader() {
        super("sqlserver.sql");
    }

    public OracleAtClientSqlLoader(ClassLoader classLoader) {
        super("oracle.sql", classLoader);
    }

    @Override
    public String getSqlType() {
        return "Oracle";
    }
}
