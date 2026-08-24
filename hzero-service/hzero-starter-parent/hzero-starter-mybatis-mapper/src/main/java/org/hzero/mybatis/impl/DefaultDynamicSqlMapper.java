package org.hzero.mybatis.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.SqlSession;
import org.hzero.mybatis.DynamicSqlMapper;
import org.hzero.mybatis.StatementManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis执行sql工具
 *
 * @author liuzh
 * @since 2015-03-10
 */
public class DefaultDynamicSqlMapper implements DynamicSqlMapper{
    private static final Logger logger = LoggerFactory.getLogger(DefaultDynamicSqlMapper.class);
    private final StatementManager statementManager;
    private final SqlSession sqlSession;
    
    /**
     * 构造方法，默认缓存MappedStatement
     *
     * @param sqlSession
     */
    public DefaultDynamicSqlMapper(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
        this.statementManager = new DefaultStatementManager(sqlSession.getConfiguration());
    }

    /**
     * 获取List中最多只有一个的数据
     *
     * @param list List结果
     * @param <T>  泛型类型
     * @return
     */
    private <T> T getOne(List<T> list) {
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Object> selectOne(String sql) {
        List<Map<String, Object>> list = selectList(sql);
        return getOne(list);
    }

    @Override
    public Map<String, Object> selectOne(String sql, Object value) {
        List<Map<String, Object>> list = selectList(sql, value);
        return getOne(list);
    }

    @Override
    public <T> T selectOne(String sql, Class<T> resultType) {
        List<T> list = selectList(sql, resultType);
        return getOne(list);
    }

    @Override
    public <T> T selectOne(String sql, Object value, Class<T> resultType) {
        List<T> list = selectList(sql, value, resultType);
        return getOne(list);
    }

    @Override
    public List<Map<String, Object>> selectList(String sql) {
        logger.debug("Select List : {}", sql);
        String msId = statementManager.select(sql);
        return sqlSession.selectList(msId);
    }

    @Override
    public List<Map<String, Object>> selectList(String sql, Object value) {
        Class<?> parameterType = value != null ? value.getClass() : null;
        String msId = statementManager.selectDynamic(sql, parameterType);
        return sqlSession.selectList(msId, value);
    }

    @Override
    public <T> List<T> selectList(String sql, Class<T> resultType) {
        String msId;
        if (resultType == null) {
            msId = statementManager.select(sql);
        } else {
            msId = statementManager.select(sql, resultType);
        }
        return sqlSession.selectList(msId);
    }

    @Override
    public <T> List<T> selectList(String sql, Object value, Class<T> resultType) {
        String msId;
        Class<?> parameterType = value != null ? value.getClass() : null;
        if (resultType == null) {
            msId = statementManager.selectDynamic(sql, parameterType);
        } else {
            msId = statementManager.selectDynamic(sql, parameterType, resultType);
        }
        return sqlSession.selectList(msId, value);
    }

    @Override
    public int insert(String sql) {
        String msId = statementManager.insert(sql);
        return sqlSession.insert(msId);
    }

    @Override
    public int insert(String sql, Object value) {
        Class<?> parameterType = value != null ? value.getClass() : null;
        String msId = statementManager.insertDynamic(sql, parameterType);
        return sqlSession.insert(msId, value);
    }

    @Override
    public int update(String sql) {
        String msId = statementManager.update(sql);
        return sqlSession.update(msId);
    }

    @Override
    public int update(String sql, Object value) {
        Class<?> parameterType = value != null ? value.getClass() : null;
        String msId = statementManager.updateDynamic(sql, parameterType);
        return sqlSession.update(msId, value);
    }

    @Override
    public int delete(String sql) {
        String msId = statementManager.delete(sql);
        return sqlSession.delete(msId);
    }

    @Override
    public int delete(String sql, Object value) {
        Class<?> parameterType = value != null ? value.getClass() : null;
        String msId = statementManager.deleteDynamic(sql, parameterType);
        return sqlSession.delete(msId, value);
    }

}