/**
 * 
 */
package org.hzero.mybatis;


/**
 * Statement管理器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午7:38:27
 */
public interface StatementManager {
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @return statementId
     */
    String select(String sql);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param parameterType
     * @return statementId
     */
    String selectDynamic(String sql, Class<?> parameterType);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param resultType
     * @return statementId
     */
    String select(String sql, Class<?> resultType);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param parameterType
     * @param resultType
     * @return statementId
     */
    String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @return statementId
     */
    String insert(String sql);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param parameterType
     * @return statementId
     */
    String insertDynamic(String sql, Class<?> parameterType);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @return statementId
     */
    String update(String sql);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param parameterType
     * @return statementId
     */
    String updateDynamic(String sql, Class<?> parameterType);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @return statementId
     */
    String delete(String sql);
    
    /**
     * 根据sql创建Statement
     * @param sql
     * @param parameterType
     * @return statementId
     */
    String deleteDynamic(String sql, Class<?> parameterType);
    
}
