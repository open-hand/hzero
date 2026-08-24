/**
 * 
 */
package org.hzero.mybatis.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.hzero.mybatis.StatementManager;

/**
 * 默认Statement管理类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午7:38:56
 */
public class DefaultStatementManager implements StatementManager {
    
    private Configuration configuration;
    private LanguageDriver languageDriver;

    public DefaultStatementManager(Configuration configuration) {
        this.configuration = configuration;
        languageDriver = configuration.getDefaultScriptingLanguageInstance();
    }

    /**
     * 创建MSID
     *
     * @param sql 执行的sql
     * @param sql 执行的sqlCommandType
     * @return
     */
    private String newMsId(String sql, SqlCommandType sqlCommandType) {
        StringBuilder msIdBuilder = new StringBuilder(sqlCommandType.toString());
        msIdBuilder.append(".").append(sql.hashCode());
        return msIdBuilder.toString();
    }

    /**
     * 是否已经存在该ID
     *
     * @param msId
     * @return
     */
    private boolean hasMappedStatement(String msId) {
        return configuration.hasStatement(msId, false);
    }

    /**
     * 创建一个查询的MS
     *
     * @param msId
     * @param sqlSource  执行的sqlSource
     * @param resultType 返回的结果类型
     */
    private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        resultMaps.add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<ResultMapping>(0)).build());
        MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, SqlCommandType.SELECT)
                .resultMaps(resultMaps)
                .build();
        //缓存
        configuration.addMappedStatement(ms);
    }

    /**
     * 创建一个DML的MS
     *
     * @param msId
     * @param sqlSource      执行的sqlSource
     * @param sqlCommandType 执行的sqlCommandType
     */
    private void newUpdateMappedStatement(String msId, SqlSource sqlSource, SqlCommandType sqlCommandType) {
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        resultMaps.add(new ResultMap.Builder(configuration, "defaultResultMap", int.class, new ArrayList<ResultMapping>(0)).build());
        MappedStatement ms = new MappedStatement.Builder(configuration, msId, sqlSource, sqlCommandType)
                .resultMaps(resultMaps)
                .build();
        //缓存
        configuration.addMappedStatement(ms);
    }

    @Override
    public String select(String sql) {
        String msId = newMsId(sql, SqlCommandType.SELECT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
        newSelectMappedStatement(msId, sqlSource, Map.class);
        return msId;
    }

    @Override
    public String selectDynamic(String sql, Class<?> parameterType) {
        String msId = newMsId(sql + parameterType, SqlCommandType.SELECT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        newSelectMappedStatement(msId, sqlSource, Map.class);
        return msId;
    }

    @Override
    public String select(String sql, Class<?> resultType) {
        String msId = newMsId(resultType + sql, SqlCommandType.SELECT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
        newSelectMappedStatement(msId, sqlSource, resultType);
        return msId;
    }

    @Override
    public String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
        String msId = newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        newSelectMappedStatement(msId, sqlSource, resultType);
        return msId;
    }

    @Override
    public String insert(String sql) {
        String msId = newMsId(sql, SqlCommandType.INSERT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
        return msId;
    }

    @Override
    public String insertDynamic(String sql, Class<?> parameterType) {
        String msId = newMsId(sql + parameterType, SqlCommandType.INSERT);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.INSERT);
        return msId;
    }

    @Override
    public String update(String sql) {
        String msId = newMsId(sql, SqlCommandType.UPDATE);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
        return msId;
    }

    @Override
    public String updateDynamic(String sql, Class<?> parameterType) {
        String msId = newMsId(sql + parameterType, SqlCommandType.UPDATE);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.UPDATE);
        return msId;
    }

    @Override
    public String delete(String sql) {
        String msId = newMsId(sql, SqlCommandType.DELETE);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        StaticSqlSource sqlSource = new StaticSqlSource(configuration, sql);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
        return msId;
    }

    @Override
    public String deleteDynamic(String sql, Class<?> parameterType) {
        String msId = newMsId(sql + parameterType, SqlCommandType.DELETE);
        if (hasMappedStatement(msId)) {
            return msId;
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
        newUpdateMappedStatement(msId, sqlSource, SqlCommandType.DELETE);
        return msId;
    }
}
