package org.hzero.platform.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.platform.app.service.EntityColumnService;
import org.hzero.platform.domain.entity.EntityColumn;
import org.hzero.platform.domain.repository.EntityColumnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 实体列应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
@Service
public class EntityColumnServiceImpl implements EntityColumnService {
    @Autowired
    private EntityColumnRepository entityColumnRepository;

    @Override
    public void deleteByEntityTableId(Long entityTableId) {
        List<EntityColumn> select = entityColumnRepository.select(EntityColumn.FIELD_ENTITY_TABLE_ID, entityTableId);
        if (CollectionUtils.isNotEmpty(select)) {
            select.forEach(entityColumn -> {
                entityColumnRepository.deleteByPrimaryKey(entityColumn.getEntityColumnId());
            });
        }
    }
}
