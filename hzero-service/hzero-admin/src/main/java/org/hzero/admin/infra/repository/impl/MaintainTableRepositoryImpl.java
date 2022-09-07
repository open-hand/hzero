package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.MaintainTable;
import org.hzero.admin.domain.repository.MaintainTableRepository;
import org.hzero.admin.infra.mapper.MaintainTableMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:45 上午
 */
@Repository
public class MaintainTableRepositoryImpl extends BaseRepositoryImpl<MaintainTable> implements MaintainTableRepository {

    @Autowired
    private MaintainTableMapper maintainTableMapper;

    @Override
    public Page<MaintainTable> pageMaintainTable(Long maintainId, String serviceCode, String tableName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                maintainTableMapper.listMaintainTable(maintainId, serviceCode, tableName));
    }

    @Override
    public List<MaintainTable> selectMaintainTables(Long maintainId, String serviceCode) {
        return maintainTableMapper.selectMaintainTables(maintainId, serviceCode);
    }

    @Override
    public MaintainTable selectMaintainTable(Long maintainId, String serviceCode, String tableName) {
        return maintainTableMapper.selectMaintainTable(maintainId, serviceCode, tableName);
    }
}
