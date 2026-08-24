package org.hzero.mybatis.common;

import org.hzero.mybatis.common.condition.SelectByConditionMapper;
import org.hzero.mybatis.common.condition.SelectCountByConditionMapper;

/**
 * 通用Mapper接口,Condition操作
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface ConditionMapper<T> extends
        SelectByConditionMapper<T>,
        SelectCountByConditionMapper<T> {

}