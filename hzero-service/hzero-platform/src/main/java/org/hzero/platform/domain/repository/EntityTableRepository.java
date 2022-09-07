package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.EntityTable;

import java.util.Set;

/**
 * 实体表资源库
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
public interface EntityTableRepository extends BaseRepository<EntityTable> {

    /**
     * 分页查询实体列表
     * @param pageRequest 分页条件
     * @param entityTable 查询条件
     * @return
     */
    Page<EntityTable> pageEntityTable(PageRequest pageRequest,EntityTable entityTable);

    Set<String> selectServices();
}
