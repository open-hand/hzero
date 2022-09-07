package io.choerodon.mybatis.pagehelper.parser;

/**
 * count语句解析器
 *
 * @author XCXCXCXCX
 * @date 2020/2/26 1:29 下午
 */
public interface ICountSqlParser {

    /**
     * 获取智能的countSql
     *
     * @param sql
     * @return
     */
    String getSmartCountSql(String sql);
}
