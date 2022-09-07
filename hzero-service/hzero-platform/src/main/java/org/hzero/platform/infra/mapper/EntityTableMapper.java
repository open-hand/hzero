package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.EntityTable;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;
import java.util.Set;

/**
 * 实体表Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
public interface EntityTableMapper extends BaseMapper<EntityTable> {

    /**
     * 查询实体列表
     * @param entityTable 查询条件
     * @return
     */
    List<EntityTable> listEntityTable(EntityTable entityTable);

    Set<String> selectServices();
}
