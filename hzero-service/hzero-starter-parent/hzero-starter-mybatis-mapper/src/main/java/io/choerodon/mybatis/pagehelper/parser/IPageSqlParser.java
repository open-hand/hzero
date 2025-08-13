package io.choerodon.mybatis.pagehelper.parser;

/**
 * 分页语句解析器
 *
 * @author XCXCXCXCX
 * @date 2020/2/26 1:33 下午
 */
public interface IPageSqlParser {

    /**
     * 转换为分页语句
     *
     * @param sql    sql
     * @param offset offset
     * @param limit  limit
     * @return String String
     */
    String convertToPageSql(String sql, Integer offset, Integer limit);
}
