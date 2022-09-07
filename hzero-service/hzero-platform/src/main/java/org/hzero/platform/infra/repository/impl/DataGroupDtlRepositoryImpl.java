package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DataGroupDtl;
import org.hzero.platform.domain.entity.DataGroupLine;
import org.hzero.platform.domain.repository.DataGroupDtlRepository;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.infra.mapper.DataGroupDtlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 数据组明细定义 资源库实现
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Component
public class DataGroupDtlRepositoryImpl extends BaseRepositoryImpl<DataGroupDtl> implements DataGroupDtlRepository {

    @Autowired
    private DataGroupDtlMapper dataGroupDtlMapper;
    @Autowired
    private DataGroupDtlRepository dataGroupDtlRepository;
    @Autowired
    private DataGroupLineRepository dataGroupLineRepository;
    @Autowired
    private LovValueRepository lovValueRepository;

    @Override
    public Page<DataGroupDtl> pageDataGroupDtl(PageRequest pageRequest, DataGroupDtl dataGroupDtl) {
        return PageHelper.doPageAndSort(pageRequest, () -> dataGroupDtlMapper.listDataGroupDtl(dataGroupDtl));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DataGroupDtl> createDataGroupDtl(List<DataGroupDtl> dataGroupDtlList) {
        if (CollectionUtils.isEmpty(dataGroupDtlList)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        // 检查groupId 是否存在
        if (dataGroupDtlList.stream().anyMatch(item -> item.getGroupId() == null)) {
            DataGroupLine dataGroupLine = dataGroupLineRepository.selectByPrimaryKey(dataGroupDtlList.get(0).getGroupLineId());
            dataGroupDtlList.forEach(item -> {
                if (item.getGroupId() == null) {
                    item.setGroupId(dataGroupLine.getGroupId());
                }
            });
        }

        // 校验参数
        dataGroupDtlList.forEach(item -> {
            item.validUniqueIndex(this,lovValueRepository);

        });

        // insert
        dataGroupDtlRepository.batchInsert(dataGroupDtlList);
        return dataGroupDtlList;
    }

    @Override
    public void deleteDataGroupDtl(List<DataGroupDtl> dataGroupDtlList) {
        dataGroupDtlList.forEach(item -> {
            dataGroupDtlRepository.deleteByPrimaryKey(item);
        });
    }
}
