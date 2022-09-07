package io.choerodon.mybatis.common.special;

import io.choerodon.mybatis.provider.special.SpecialProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * 通用Mapper接口,特殊方法，批量插入，支持批量插入的数据库都可以使用，例如mysql,h2等
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface InsertListMapper<T> {

    /**
     * 批量插入，支持数据库自增字段，支持回写
     * 由于这个方法只支持mysql数据库，所以计划在0.8.2版本移除
     * @param recordList
     * @return
     */
    @Deprecated
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @InsertProvider(type = SpecialProvider.class, method = "dynamicSql")
    int insertList(List<T> recordList);

    /**
     * ======如果主键不是id怎么用？==========
     * 假设主键的属性名是uid,那么新建一个Mapper接口如下
     * <pre>
        public interface InsertUidListMapper<T> {
            @Options(useGeneratedKeys = true, keyProperty = "uid")
            @InsertProvider(type = SpecialProvider.class, method = "dynamicSql")
            int insertList(List<T> recordList);
        }
     * 只要修改keyProperty = "uid"就可以
     *
     * 然后让你自己的Mapper继承InsertUidListMapper<T>即可
     *
     * </pre>
     */
}
