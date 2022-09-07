package org.hzero.seata.rm.init.datasource.locator;

import java.io.IOException;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 4:44 下午
 */
public interface SqlLoader {

    String load() throws IOException;

    String getSqlType();

}
