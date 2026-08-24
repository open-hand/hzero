package org.hzero.boot.platform.entity.service;

import java.util.Map;

import io.choerodon.mybatis.domain.EntityTable;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/08 16:05
 */
public interface EntityRegistService {
    /**
     * 注册
     *
     * @param entityClassTableMap 包含实体类和table类的map
     */
     void doRegist(Map<Class<?>, EntityTable> entityClassTableMap);

}
