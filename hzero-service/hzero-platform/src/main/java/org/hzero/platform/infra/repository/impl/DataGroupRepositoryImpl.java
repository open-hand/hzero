package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.repository.DataGroupRepository;
import org.hzero.platform.infra.mapper.DataGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据组定义 资源库实现
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Component
public class DataGroupRepositoryImpl extends BaseRepositoryImpl<DataGroup> implements DataGroupRepository {

    @Autowired
    private DataGroupMapper dataGroupMapper;

    @Override
    public Page<DataGroup> pageDataGroup(PageRequest pageRequest, DataGroup dataGroup) {
        return PageHelper.doPageAndSort(pageRequest,()->dataGroupMapper.listDataGroup(dataGroup));
    }
}
