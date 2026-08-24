package org.hzero.mybatis.common.condition;

import org.apache.ibatis.annotations.SelectProvider;

import org.hzero.mybatis.provider.ConditionProvider;

/**
 * 通用Mapper接口,Condition查询
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface SelectCountByConditionMapper<T> {

    /**
     * 根据Condition条件进行查询总数
     *
     * @param cndition
     * @return
     */
    @SelectProvider(type = ConditionProvider.class, method = "dynamicSql")
    int selectCountByCondition(Object condition);

}
