package org.hzero.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;

import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;

/**
 * ConditionProvider实现类，基础方法实现类
 *
 *
 * @author liuzh
 */
public class ConditionProvider extends MapperTemplate {

    public ConditionProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 根据Condition查询总数
     *
     * @param ms
     * @return
     */
    public String selectCountByCondition(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if(isCheckExampleEntityClass()){
            sql.append(SqlHelper.conditionCheck(entityClass));
        }
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append(SqlHelper.selectCount(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.exampleWhereClause());
        sql.append(SqlHelper.conditionForUpdate());
        return sql.toString();
    }

    /**
     * 根据Condition查询
     *
     * @param ms
     * @return
     */
    public String selectByCondition(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder("SELECT ");
        if(isCheckExampleEntityClass()){
            sql.append(SqlHelper.conditionCheck(entityClass));
        }
        if (EntityHelper.getTableByEntity(entityClass).isMultiLanguage()) {
            sql.append(SqlHelper.getLangBind());
        }
        sql.append("<if test=\"distinct\">distinct</if>");
        //支持查询指定列
        sql.append(SqlHelper.conditionSelectColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.exampleWhereClause());
        sql.append(SqlHelper.conditionOrderBy(entityClass));
        sql.append(SqlHelper.conditionForUpdate());
        return sql.toString();
    }

    /**
     * 根据Condition查询
     *
     * @param ms
     * @return
     */
    public String selectByConditionAndRowBounds(MappedStatement ms) {
        return selectByCondition(ms);
    }
    
}

