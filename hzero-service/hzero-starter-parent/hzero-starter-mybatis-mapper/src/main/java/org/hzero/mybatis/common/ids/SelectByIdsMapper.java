package org.hzero.mybatis.common.ids;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import org.hzero.mybatis.provider.IdsProvider;

/**
 * 通用Mapper接口,根据ids查询
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface SelectByIdsMapper<T> {

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ids 如 "1,2,3,4"
     * @return
     */
    @SelectProvider(type = IdsProvider.class, method = "dynamicSql")
    List<T> selectByIds(String ids);

}
