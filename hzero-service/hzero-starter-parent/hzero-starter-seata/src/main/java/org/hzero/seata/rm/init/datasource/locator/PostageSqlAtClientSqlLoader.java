package org.hzero.seata.rm.init.datasource.locator;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 4:36 下午
 */
public class PostageSqlAtClientSqlLoader extends AtClientSqlLoader {

    public PostageSqlAtClientSqlLoader() {
        super("sqlserver.sql");
    }

    public PostageSqlAtClientSqlLoader(ClassLoader classLoader) {
        super("postgresql.sql", classLoader);
    }

    @Override
    public String getSqlType() {
        return "PostgreSQL";
    }
}
