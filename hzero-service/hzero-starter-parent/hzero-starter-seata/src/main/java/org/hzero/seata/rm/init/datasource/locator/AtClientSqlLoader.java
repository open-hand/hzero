package org.hzero.seata.rm.init.datasource.locator;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 3:22 下午
 */
public abstract class AtClientSqlLoader extends FileSqlLoader{

    public AtClientSqlLoader(String fileName) {
        this(fileName, ClassLoader.getSystemClassLoader());
    }

    public AtClientSqlLoader(String fileName, ClassLoader classLoader) {
        super("script/client/at/db/", fileName, classLoader);
    }

    @Override
    public abstract String getSqlType();
}
