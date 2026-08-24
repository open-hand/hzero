package org.hzero.mybatis.common;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2019/2/20 星期三 18:58
 */
public class SimpleSqlSource implements SqlSource {
    private BoundSql boundSql;

    public SimpleSqlSource(BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundSql;
    }
}
