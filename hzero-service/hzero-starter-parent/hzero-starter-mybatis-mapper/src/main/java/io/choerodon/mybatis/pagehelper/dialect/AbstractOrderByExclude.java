package io.choerodon.mybatis.pagehelper.dialect;

import org.hzero.mybatis.parser.SqlInterceptor;

/**
 * @author 废柴 2020/8/25 16:49
 */
public abstract class AbstractOrderByExclude implements SqlInterceptor {
    @Override
    public boolean select() {
        return true;
    }
}
