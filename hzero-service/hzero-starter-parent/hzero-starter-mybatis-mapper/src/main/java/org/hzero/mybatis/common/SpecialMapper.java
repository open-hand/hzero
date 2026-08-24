package org.hzero.mybatis.common;

import io.choerodon.mybatis.common.special.InsertListMapper;
import io.choerodon.mybatis.common.special.InsertUseGeneratedKeysMapper;

/**
 * 
 * 针对数据库特殊语法Mapper
 * @param <T>
 * @author xianzhi.chen@hand-china.com	2018年9月10日下午8:27:21
 */
public interface SpecialMapper<T> extends InsertListMapper<T>, InsertUseGeneratedKeysMapper<T> {

}
