package org.hzero.boot.platform.lov.handler.impl;

import org.hzero.boot.platform.lov.handler.SqlChecker;

/**
 * sql校验器默认实现
 *
 * @author gaokuo.dai@hand-china.com 2018年6月27日上午9:52:11
 */
public class DefaultSqlChecker implements SqlChecker {

    private static final String[] KEYWORDS = {"insert", "delete", "update", "drop table", "truncate",
            "xp_cmdshell", "exec   master", "netlocalgroup administrators",
            "net user", ";"};

    @Override
    public void doFilter(String sql) {
        // 方法过于粗暴，字段包含delete导致无法通过校验，SQL值集已限制平台创建，去掉校验
    }
}
