package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.EntityTable;
import org.hzero.platform.domain.repository.EntityTableRepository;
import org.hzero.platform.infra.mapper.EntityTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 实体表 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
@Component
public class EntityTableRepositoryImpl extends BaseRepositoryImpl<EntityTable> implements EntityTableRepository {

    @Autowired
    private EntityTableMapper entityTableMapper;

    @Override
    public Page<EntityTable> pageEntityTable(PageRequest pageRequest,EntityTable entityTable) {
        return PageHelper.doPageAndSort(pageRequest,()->entityTableMapper.listEntityTable(entityTable));
    }

    @Override
    public Set<String> selectServices() {
        return entityTableMapper.selectServices();
    }
}
