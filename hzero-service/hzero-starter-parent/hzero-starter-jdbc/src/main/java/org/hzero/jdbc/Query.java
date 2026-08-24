package org.hzero.jdbc;

import org.hzero.jdbc.statement.SqlPageStatement;

import java.sql.Connection;
import java.util.Map;

/**
 * @author qingsheng.chen@hand-china.com
 */
public interface Query {

    /**
     * 获取元数据行Map数据
     *
     * @param sqlText sql
     * @return map
     */
    Map<String, Object> query(String sqlText);

    /**
     * 分页获取元数据行Map数据
     *
     * @param sqlText       sql
     * @param pageStatement 分页参数
     * @return map
     */
    Map<String, Object> query(String sqlText, SqlPageStatement pageStatement);

    /**
     * 获取元数据总条数
     *
     * @param sqlText sql语句
     * @return 行总条数
     */
    long queryCount(String sqlText);

    /**
     * @return 获取JDBC连接
     */
    Connection getJdbcConnection();

    /**
     * SQL 转 Count SQL
     *
     * @param sqlText 原始SQL
     * @return Count SQL
     */
    String countSql(String sqlText);

    /**
     * SQL 转 分页SQL
     *
     * @param sqlText          原始SQL
     * @param sqlPageStatement 分页参数
     * @return 分页SQL
     */
    String pageSql(String sqlText, SqlPageStatement sqlPageStatement);
}
