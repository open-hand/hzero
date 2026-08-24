package io.choerodon.mybatis.common;

import io.choerodon.mybatis.common.special.InsertListMapper;
import io.choerodon.mybatis.common.special.InsertUseGeneratedKeysMapper;

/**
 * 通用Mapper接口,MySql独有的通用方法
 *
 * @param <T> 不能为空
 * @author liuzh
 */
public interface MySqlMapper<T> extends
        InsertListMapper<T>,
        InsertUseGeneratedKeysMapper<T> {

}
