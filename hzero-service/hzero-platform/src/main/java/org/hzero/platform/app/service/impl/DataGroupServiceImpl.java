package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.DataGroupService;
import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.entity.DataGroupDtl;
import org.hzero.platform.domain.entity.DataGroupLine;
import org.hzero.platform.domain.repository.DataGroupDtlRepository;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.platform.domain.repository.DataGroupRepository;
import org.hzero.platform.infra.mapper.DataGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 数据组定义应用服务默认实现
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Service
public class DataGroupServiceImpl implements DataGroupService {

    @Autowired
    private DataGroupRepository dataGroupRepository;
    @Autowired
    private DataGroupLineRepository dataGroupLineRepository;
    @Autowired
    private DataGroupDtlRepository dataGroupDtlRepository;
    @Autowired
    private DataGroupMapper dataGroupMapper;

    @Override
    public Page<DataGroup> pageDataGroupByCondition(PageRequest pageRequest, DataGroup dataGroup) {
        return dataGroupRepository.pageDataGroup(pageRequest, dataGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDataGroup(DataGroup dataGroup) {
        // 删头
        dataGroupRepository.deleteByPrimaryKey(dataGroup);
        // 删除行
        DataGroupLine dataGroupLine = new DataGroupLine();
        dataGroupLine.setGroupId(dataGroup.getGroupId());
        dataGroupLineRepository.delete(dataGroupLine);
        // 删除明细
        DataGroupDtl dataGroupDtl = new DataGroupDtl();
        dataGroupDtl.setGroupId(dataGroup.getGroupId());
        dataGroupDtlRepository.delete(dataGroupDtl);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DataGroup createDataGroup(DataGroup dataGroup) {

        // 校验唯一性索引
        dataGroup.validUniqueIndex(dataGroupRepository);

        // insert
        dataGroupRepository.insertSelective(dataGroup);
        return dataGroup;
    }

    @Override
    public DataGroup detailDataGroup(DataGroup dataGroup) {
        List<DataGroup> dataGroups = dataGroupMapper.listDataGroup(dataGroup);
        return Optional.ofNullable(dataGroups)
                .orElseThrow(()->new CommonException(BaseConstants.ErrorCode.DATA_INVALID))
                .get(0);
    }
}
