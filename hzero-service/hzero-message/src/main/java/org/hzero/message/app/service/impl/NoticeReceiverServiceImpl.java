package org.hzero.message.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.app.service.NoticePublishedService;
import org.hzero.message.app.service.NoticeReceiverService;
import org.hzero.message.domain.entity.NoticePublished;
import org.hzero.message.domain.entity.NoticeReceiver;
import org.hzero.message.domain.repository.NoticePublishedRepository;
import org.hzero.message.domain.repository.NoticeReceiverRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author minghui.qiu@hand-china.com
 */
@Service
public class NoticeReceiverServiceImpl implements NoticeReceiverService {

    @Autowired
    private NoticeReceiverRepository noticeReceiverRepository;
    @Autowired
    private NoticePublishedRepository noticePublishedRepository;
    @Autowired
    private NoticePublishedService noticePublishedService;

    @Override
    public Page<NoticeReceiver> listReceiveRecordPage(Long tenantId, PageRequest pageRequest, List<Long> publishedIds, Long noticeId) {
        if (CollectionUtils.isEmpty(publishedIds)) {
            return null;
        }
        NoticePublished noticePublished = noticePublishedRepository.selectOne(new NoticePublished()
                .setTenantId(tenantId)
                .setNoticeId(noticeId).setPublishedStatusCode(HmsgConstant.PublishedStatus.DRAFT));
        if (publishedIds.size() == 1 && noticePublished != null && publishedIds.get(0).equals(noticePublished.getPublishedId())) {
            return noticeReceiverRepository.listReceiveRecord(pageRequest, noticePublished.getPublishedId());
        }
        return noticeReceiverRepository.listReceiveRecordPage(pageRequest, publishedIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<NoticeReceiver> createNoticeReceiver(long noticeId, long organizationId, List<NoticeReceiver> noticeReceiveList) {
        NoticePublished noticePublished = new NoticePublished().setNoticeId(noticeId).setTenantId(organizationId)
                .setPublishedStatusCode(HmsgConstant.PublishedStatus.DRAFT);
        int count = noticePublishedRepository.selectCount(noticePublished);
        if (count > 0) {
            noticePublished = noticePublishedRepository.selectOne(noticePublished);
            List<NoticeReceiver> oldReceiverList = noticeReceiverRepository
                    .select(new NoticeReceiver().setPublishedId(noticePublished.getPublishedId()));
            noticeReceiveList.addAll(oldReceiverList);
            for (NoticeReceiver noticeReceiver : noticeReceiveList) {
                noticeReceiver.setPublishedId(noticePublished.getPublishedId());
                noticeReceiver.setReceiverSourceId(noticeReceiver.getReceiverSourceId() == null ? BaseConstants.DEFAULT_TENANT_ID : noticeReceiver.getReceiverSourceId());
            }
            noticeReceiverRepository.batchDelete(oldReceiverList);
            noticeReceiverRepository.batchInsert(noticeReceiveList.stream().distinct().collect(Collectors.toList()));
        } else {
            noticePublishedRepository.insertSelective(noticePublished);
            for (NoticeReceiver noticeReceiver : noticeReceiveList) {
                noticeReceiver.setPublishedId(noticePublished.getPublishedId());
                noticeReceiver.setReceiverSourceId(noticeReceiver.getReceiverSourceId() == null ? BaseConstants.DEFAULT_TENANT_ID : noticeReceiver.getReceiverSourceId());
            }
            noticeReceiverRepository.batchInsert(noticeReceiveList.stream().distinct().collect(Collectors.toList()));
        }
        List<Long> publishedIds = Collections.singletonList(noticePublished.getPublishedId());
        noticePublishedService.publicNotice(publishedIds, noticeId, organizationId);
        return noticeReceiveList;
    }
}