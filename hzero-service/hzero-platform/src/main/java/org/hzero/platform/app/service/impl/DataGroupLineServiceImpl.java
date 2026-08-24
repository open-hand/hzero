package org.hzero.platform.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.DataGroupLineService;
import org.hzero.platform.domain.entity.DataGroupDtl;
import org.hzero.platform.domain.entity.DataGroupLine;
import org.hzero.platform.domain.repository.DataGroupDtlRepository;
import org.hzero.platform.domain.repository.DataGroupLineRepository;
import org.hzero.platform.domain.repository.LovRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 数据组行定义应用服务默认实现
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
@Service
public class DataGroupLineServiceImpl implements DataGroupLineService {
    @Autowired
    private DataGroupLineRepository dataGroupLineRepository;
    @Autowired
    private DataGroupDtlRepository dataGroupDtlRepository;
    @Autowired
    private LovRepository lovRepository;

    @Override
    public Page<DataGroupLine> pageDataGroupLine(PageRequest pageRequest, DataGroupLine dataGroupLine) {
        return dataGroupLineRepository.pageDataGroupLine(pageRequest, dataGroupLine);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DataGroupLine> createDataGroupLine(List<DataGroupLine> dataGroupLineList) {
        if (CollectionUtils.isEmpty(dataGroupLineList)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        // 校验索引
        dataGroupLineList.forEach(item->item.validUniqueIndex(dataGroupLineRepository,lovRepository));

        // insert
        dataGroupLineRepository.batchInsert(dataGroupLineList);

        return dataGroupLineList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteDataGroupLine(List<DataGroupLine> dataGroupLineList) {
        if (CollectionUtils.isEmpty(dataGroupLineList)) {
            return;
        }
        dataGroupLineList.forEach(item -> {
            dataGroupLineRepository.deleteByPrimaryKey(item);
            // 删明细
            DataGroupDtl dataGroupDtl = new DataGroupDtl();
            dataGroupDtl.setGroupLineId(item.getGroupLineId());
            dataGroupDtlRepository.delete(dataGroupDtl);
        });
    }


}
