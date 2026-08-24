package io.choerodon.mybatis.common.optional;

import io.choerodon.mybatis.provider.optional.OptionalProvider;
import org.apache.ibatis.annotations.InsertProvider;

/**
 * Created by xausky on 3/20/17.
 */
public interface OptionalInsertMapper<T> {

    @InsertProvider(type = OptionalProvider.class, method = "dynamicSql")
    int insertOptional(T record);

}
