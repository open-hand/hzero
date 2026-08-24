package org.hzero.message.infra.repository.impl;

import java.util.List;

import org.hzero.message.domain.entity.NoticeReceiver;
import org.hzero.message.domain.entity.Unit;
import org.hzero.message.domain.repository.NoticeReceiverRepository;
import org.hzero.message.infra.mapper.NoticeReceiverMapper;
import org.hzero.message.infra.mapper.NoticeUnitMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告接收记录服务实现类
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-10 16:18:09
 */
@Service
public class NoticeReceiverRepositoryImpl extends BaseRepositoryImpl<NoticeReceiver> implements NoticeReceiverRepository {

    @Autowired
    private NoticeReceiverMapper noticeReceiverMapper;

    @Autowired
    private NoticeUnitMapper unitMapper;

    @Override
    public Page<NoticeReceiver> listReceiveRecordPage(PageRequest pageRequest, List<Long> publishedIds) {
        return PageHelper.doPageAndSort(pageRequest, () -> noticeReceiverMapper.listReceiveRecordPage(publishedIds));
    }

    @Override
    public Page<NoticeReceiver> listReceiveRecord(PageRequest pageRequest, Long publishedId) {
        return PageHelper.doPageAndSort(pageRequest, () -> noticeReceiverMapper.listReceiveRecord(publishedId));
    }

    @Override
    public Page<Unit> listAllUnits(PageRequest pageRequest, Long tenantId, String unitName, String unitCode) {
        return PageHelper.doPageAndSort(pageRequest, () -> unitMapper.listAllUnits(tenantId, unitName, unitCode));
    }

    @Override
    public List<NoticeReceiver> listReceiveRecordPage(List<Long> publishedIds) {
        return noticeReceiverMapper.listReceiveRecordPage(publishedIds);
    }
}
