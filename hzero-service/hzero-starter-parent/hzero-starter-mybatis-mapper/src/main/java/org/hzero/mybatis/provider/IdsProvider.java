package org.hzero.mybatis.provider;

import java.util.Set;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * 通过 ids 字符串的各种操作
 * <p/>
 * ids 如 "1,2,3"
 *
 * @author liuzh
 */
public class IdsProvider extends MapperTemplate {

    public IdsProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ms
     * @return
     */
    public String selectByIds(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder("select ");
        sql.append(SqlHelper.conditionSelectColumns(entityClass));
        sql.append(SqlHelper.selectFromTableTl(entityClass, tableName(entityClass)));
        Set<EntityColumn> columnList = EntityHelper.getPkColumns(entityClass);
        if (columnList.size() == 1) {
            EntityColumn column = columnList.iterator().next();
            sql.append(" where ");
            sql.append(EntityHelper.getTableByEntity(entityClass).isMultiLanguage() ? ("b." + column.getColumn()) : column.getColumn());
            sql.append(" in (${_parameter})");
        } else {
            throw new MapperException("继承 selectByIds 方法的实体类[" + entityClass.getCanonicalName() + "]中必须只有一个带有 @Id 注解的字段");
        }
        sql.append(SqlHelper.getTenantLimit(entityClass));
        return sql.toString();
    }
}

