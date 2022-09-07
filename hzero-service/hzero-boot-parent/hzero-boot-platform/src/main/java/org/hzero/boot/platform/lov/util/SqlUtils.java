package org.hzero.boot.platform.lov.util;


import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class SqlUtils {
    private static final String SQL_ID_PATTERN = "^([a-zA-Z_\\\\$][a-zA-Z0-9_\\\\$]*\\.)+[a-zA-Z_\\\\$][a-zA-Z0-9_\\\\$]*$";

    /**
     * 判断是否为 SQL ID
     * 每段以小写字母或者大写字母下划线"$"开头，中间可以包含以小写字母或者大写字母下划线"$"以及数字，多段可以用"."分隔，但不允许连续多个"."并且结尾也不能是"."，至少有两段。
     * 可以匹配：
     * org.hzero.org.platform.domain.mapper.UserMapper.select
     * org.hzero.org.platform.domain.mapper.UserMapper.select_COUNT
     * UserMapper.select
     * UserMapper.select_COUNT
     * 不可以匹配：
     * UserMapper..select
     * select column from table
     * select
     * 空字符串
     * null
     *
     * @param content 匹配内容
     * @return 是否是 sql id
     */
    public static boolean isSqlId(String content) {
        return StringUtils.hasText(content) && Pattern.matches(SQL_ID_PATTERN, content);
    }
}
