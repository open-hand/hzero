package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DataGroupLine;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.platform.infra.mapper.DataGroupLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 数据组行定义 资源库实现
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Component
public class DataGroupLineRepositoryImpl extends BaseRepositoryImpl<DataGroupLine> implements DataGroupLineRepository {

    @Autowired
    private DataGroupLineMapper dataGroupLineMapper;

    @Override
    public Page<DataGroupLine> pageDataGroupLine(PageRequest pageRequest, DataGroupLine dataGroupLine) {
        return PageHelper.doPageAndSort(pageRequest,()->dataGroupLineMapper.listDataGroupLine(dataGroupLine));
    }
}
