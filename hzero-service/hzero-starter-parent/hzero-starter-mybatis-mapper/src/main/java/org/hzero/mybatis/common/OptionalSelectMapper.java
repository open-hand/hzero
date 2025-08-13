package org.hzero.mybatis.common;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.hzero.mybatis.provider.OptionalSelectProvider;

/**
 * <p>
 * 通用Mapper接口，指定列查询
 * </p>
 *
 * @author qingsheng.chen 2018/11/27 星期二 10:58
 */
public interface OptionalSelectMapper<T> {

    /**
     * 根据自定义条件进行查询，查询条件使用等号
     *
     * @param record   record
     * @param criteria 查询条件
     * @return List
     */
    @SelectProvider(type = OptionalSelectProvider.class, method = "selectOptional")
    List<T> selectOptional(@Param("record") T record, @Param("criteria") Criteria criteria);
}
