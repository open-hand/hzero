package org.hzero.mybatis.provider;

import java.util.Map;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.helper.MapperHelper;
import io.choerodon.mybatis.helper.MapperTemplate;
import io.choerodon.mybatis.helper.SqlHelper;
import org.apache.ibatis.mapping.MappedStatement;
import org.hzero.mybatis.common.Criteria;

/**
 * <p>
 * OptionalSelectMapper实现类
 * </p>
 *
 * @author qingsheng.chen 2018/11/27 星期二 11:23
 */
public class OptionalSelectProvider extends MapperTemplate {

    public OptionalSelectProvider() {
    }

    public OptionalSelectProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 指定列查询
     *
     * @param ms 参数
     */
    public void selectOptional(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        setResultType(ms, entityClass);
    }

    /**
     * 动态查询SQL.
     *
     * @param parameter parameter
     * @return sql
     */
    public String selectOptional(Map<String,Object> parameter) {
        AuditDomain dto = (AuditDomain) parameter.get("record");
        Criteria criteria = (Criteria)parameter.get("criteria");
        return SqlHelper.buildSelectSelectiveSql(dto, criteria);
    }
}
