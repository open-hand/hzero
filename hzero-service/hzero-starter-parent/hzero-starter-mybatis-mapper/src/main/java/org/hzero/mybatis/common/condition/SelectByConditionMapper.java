package org.hzero.mybatis.common.condition;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import org.hzero.mybatis.provider.ConditionProvider;

/**
 * 通用Mapper接口,Condition查询
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface SelectByConditionMapper<T> {

    /**
     * 根据Condition条件进行查询
     *
     * @param condition
     * @return
     */
    @SelectProvider(type = ConditionProvider.class, method = "dynamicSql")
    List<T> selectByCondition(Object condition);

}