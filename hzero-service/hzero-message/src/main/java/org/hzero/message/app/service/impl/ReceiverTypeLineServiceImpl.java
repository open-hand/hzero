package org.hzero.message.app.service.impl;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.message.app.service.ReceiverTypeLineService;
import org.hzero.message.domain.entity.*;
import org.hzero.message.domain.repository.ReceiverDetailRepository;
import org.hzero.message.domain.repository.ReceiverTypeLineRepository;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收组类型行应用服务默认实现
 *
 * @author liufanghan
 */
@Service
public class ReceiverTypeLineServiceImpl implements ReceiverTypeLineService {

    private final ReceiverTypeLineRepository receiverTypeLineRepository;
    private final ReceiverDetailRepository receiverDetailRepository;
    private final ReceiverTypeRepository receiverTypeRepository;

    @Autowired
    public ReceiverTypeLineServiceImpl(ReceiverTypeLineRepository receiverTypeLineRepository,
                                       ReceiverDetailRepository receiverDetailRepository,
                                       ReceiverTypeRepository receiverTypeRepository) {
        this.receiverTypeLineRepository = receiverTypeLineRepository;
        this.receiverDetailRepository = receiverDetailRepository;
        this.receiverTypeRepository = receiverTypeRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserGroup(List<ReceiverTypeLine> receiverLineList) {
        if (CollectionUtils.isEmpty(receiverLineList)) {
            return;
        }
        receiverLineList.forEach(receiverTypeLine -> receiverDetailRepository.deleteByPrimaryKey(receiverTypeLine.getReceiveTargetId()));
        receiverTypeLineRepository.batchDeleteByPrimaryKey(receiverLineList);
    }

    @Override
    @ProcessLovValue(targetField = ReceiverTypeLine.FIELD_RECEIVER_DETAIL)
    public Page<ReceiverTypeLine> listReceiverTypeLine(PageRequest pageRequest, Long receiverTypeId) {
        return receiverTypeLineRepository.listReceiveTypeLine(pageRequest, receiverTypeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ReceiverTypeLine> createReceiverTypeLine(long receiverTypeId, List<ReceiverTypeLine> receiverTypeLineList) {
        ReceiverType receiverType = receiverTypeRepository.selectByPrimaryKey(receiverTypeId);
        List<ReceiverTypeLine> oldLineList = receiverTypeLineRepository.listOldReceiveTypeLine(receiverTypeId);
        if (!CollectionUtils.isEmpty(oldLineList)) {
            receiverTypeLineList.addAll(oldLineList);
            receiverTypeLineRepository.batchDelete(oldLineList);
        }
        // 去重
        List<ReceiverTypeLine> lines = receiverTypeLineList.stream().distinct().collect(Collectors.toList());
        // 接收组类型为外部用户
        if (HmsgConstant.ReceiverTypeMode.EXT_USER.equals(receiverType.getTypeModeCode())) {
            oldLineList.forEach(receiverTypeLine -> receiverDetailRepository.deleteByPrimaryKey(receiverTypeLine.getReceiveTargetId()));
            lines.forEach(receiverTypeLine -> {
                ReceiverDetail receiverDetail = receiverTypeLine.getReceiverDetail();
                receiverDetail.validAccountNum();
                receiverDetail.setTenantId(receiverType.getTenantId()).setReceiverTypeId(receiverTypeId);
                receiverDetailRepository.insertSelective(receiverDetail);
                receiverTypeLine.setReceiveTargetId(receiverDetail.getReceiverDetailId()).setReceiveTargetTenantId(receiverType.getTenantId());
            });
        }
        return receiverTypeLineRepository.batchInsert(lines);
    }

    @Override
    public Page<UserGroup> listUserGroups(PageRequest pageRequest, long receiverTypeId, String groupName, String groupCode) {
        return receiverTypeLineRepository.listUserGroups(pageRequest, receiverTypeId, groupName, groupCode);
    }

    @Override
    public Page<Unit> listUnits(PageRequest pageRequest, long receiverTypeId, String unitName, String unitCode) {
        return receiverTypeLineRepository.listUnits(pageRequest, receiverTypeId, unitName, unitCode);
    }

}
