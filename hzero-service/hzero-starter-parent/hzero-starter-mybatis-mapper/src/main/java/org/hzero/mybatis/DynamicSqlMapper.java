/**
 * 
 */
package org.hzero.mybatis;

import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午7:34:01
 */
public interface DynamicSqlMapper {

    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql 执行的sql
     * @return
     */
    Map<String, Object> selectOne(String sql);
    
    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    Map<String, Object> selectOne(String sql, Object value);
    
    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql        执行的sql
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    <T> T selectOne(String sql, Class<T> resultType);
    
    /**
     * 查询返回一个结果，多个结果时抛出异常
     *
     * @param sql        执行的sql
     * @param value      参数
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    <T> T selectOne(String sql, Object value, Class<T> resultType);
    
    /**
     * 查询返回List<Map<String, Object>>
     *
     * @param sql 执行的sql
     * @return
     */
    List<Map<String, Object>> selectList(String sql);
    
    /**
     * 查询返回List<Map<String, Object>>
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    List<Map<String, Object>> selectList(String sql, Object value);
    
    /**
     * 查询返回指定的结果类型
     *
     * @param sql        执行的sql
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    <T> List<T> selectList(String sql, Class<T> resultType);
    
    /**
     * 查询返回指定的结果类型
     *
     * @param sql        执行的sql
     * @param value      参数
     * @param resultType 返回的结果类型
     * @param <T>        泛型类型
     * @return
     */
    <T> List<T> selectList(String sql, Object value, Class<T> resultType);
    
    /**
     * 插入数据
     *
     * @param sql 执行的sql
     * @return
     */
    int insert(String sql);
    
    /**
     * 插入数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    int insert(String sql, Object value);
    
    /**
     * 更新数据
     *
     * @param sql 执行的sql
     * @return
     */
    int update(String sql);
    
    /**
     * 更新数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    int update(String sql, Object value);
    
    /**
     * 删除数据
     *
     * @param sql 执行的sql
     * @return
     */
    int delete(String sql);
    
    /**
     * 删除数据
     *
     * @param sql   执行的sql
     * @param value 参数
     * @return
     */
    int delete(String sql, Object value);
    
}
