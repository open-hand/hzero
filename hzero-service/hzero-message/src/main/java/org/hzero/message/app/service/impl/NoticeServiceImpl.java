package org.hzero.message.app.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.EscapeUtils;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.app.service.NoticeService;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.entity.NoticeContent;
import org.hzero.message.domain.repository.NoticeContentRepository;
import org.hzero.message.domain.repository.NoticeRepository;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 公告基础信息应用服务默认实现
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private NoticeContentRepository noticeContentRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private UserMessageRepository userMessageRepository;
    @Autowired
    private MessageConfigProperties messageConfigProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeDTO createNotice(NoticeDTO noticeDTO) {
        Notice notice = CommonConverter.beanConvert(Notice.class, noticeDTO);
        notice.setStatusCode(Notice.STATUS_DRAFT);
        if (notice.getStickyFlag() == null) {
            notice.setStickyFlag(BaseConstants.Flag.NO);
        }
        noticeRepository.insert(notice);
        BeanUtils.copyProperties(notice, noticeDTO);
        NoticeContent noticeContent = noticeDTO.getNoticeContent();
        noticeContent.setTenantId(noticeDTO.getTenantId()).setNoticeId(noticeDTO.getNoticeId());
        // 防范XSS攻击
        noticeContent.setNoticeBody(EscapeUtils.preventScript(noticeContent.getNoticeBody()));
        noticeContentRepository.insert(noticeContent);
        return noticeDTO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeDTO updateNotice(NoticeDTO noticeDTO) {
        Notice notice = CommonConverter.beanConvert(Notice.class, noticeDTO);
        // 设置为草稿状态
        notice.setStatusCode(Notice.STATUS_DRAFT);
        noticeRepository.updateByPrimaryKey(notice);
        BeanUtils.copyProperties(notice, noticeDTO);
        NoticeContent noticeContent = noticeDTO.getNoticeContent();
        // 防范XSS攻击
        noticeContent.setNoticeBody(EscapeUtils.preventScript(noticeContent.getNoticeBody()));
        noticeContentRepository.updateOptional(noticeContent, NoticeContent.FIELD_NOTICE_BODY,
                NoticeContent.FIELD_RECEIVE_TENANT_ID, NoticeContent.FIELD_RECV_USER_GROUP_ID,
                NoticeContent.FIELD_RECEIVE_USER_ID, NoticeContent.FIELD_RECV_TENANT_NAME,
                NoticeContent.FIELD_RECV_GROUP_NAME, NoticeContent.FIELD_RECV_USER_NAME);
        notice.deleteCachePublishedNotice(redisHelper);
        return noticeDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice deleteNotice(Long organizationId, Long noticeId) {
        Notice notice = Notice.updateStatus(noticeRepository, noticeId, Notice.STATUS_DELETED);
        notice.deleteCachePublishedNotice(redisHelper);
        return notice;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice publicNotice(Long organizationId, Long noticeId) {
        Notice notice = Notice.updateStatus(noticeRepository, noticeId, Notice.STATUS_PUBLISHED);
        notice.refreshCachePublishedNotices(redisHelper, objectMapper);
        List<Receiver> userList = userMessageRepository.getAllUser(organizationId);
        NoticeDTO dto = noticeRepository.detailAnnouncement(organizationId, noticeId);
        for (Receiver receiver : userList) {
            userMessageService.createSimpleMessage(receiver.getTargetUserTenantId(), receiver.getUserId(), new SimpleMessageDTO()
                    .setUserMessageId(noticeId)
                    .setNoticeId(noticeId)
                    .setUserMessageTypeCode(dto.getReceiverTypeCode())
                    .setUserMessageTypeMeaning(dto.getReceiverTypeMeaning())
                    .setSubject(dto.getTitle())
                    .setTenantId(receiver.getTargetUserTenantId())
                    .setCreationDate(notice.getPublishedDate()));
        }
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice revokeNotice(Long organizationId, Long noticeId) {
        Notice notice = Notice.updateStatus(noticeRepository, noticeId, Notice.STATUS_DRAFT);
        notice.refreshCachePublishedNotices(redisHelper, objectMapper);
        return notice;
    }

    @Override
    public Page<NoticeDTO> pageNoticeTitle(String category, String title, Long organizationId, PageRequest pageRequest) {
        String lang = LanguageHelper.language();
        pageRequest.setSize(BaseConstants.PAGE_SIZE);
        return noticeRepository.pageNoticeTitle(category, lang, title, organizationId, pageRequest);
    }

    @Override
    public List<NoticeDTO> listUserAnnouncement(NoticeDTO noticeDTO) {
        List<NoticeDTO> simpleNoticeList = noticeRepository.listUserAnnouncement(noticeDTO);
        return simpleNoticeList.stream()
                .filter(item -> item.getPublishedDate() != null)
                .sorted(Comparator.comparing(NoticeDTO::getPublishedDate).reversed())
                .limit(noticeDTO.getPreviewCount() != null ? noticeDTO.getPreviewCount() : messageConfigProperties.getMaxUnreadMessageCount())
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDTO topAnnouncement(NoticeDTO noticeDTO) {
        noticeDTO.setStickyFlag(BaseConstants.Flag.YES);
        List<NoticeDTO> simpleNoticeList = noticeRepository.listUserAnnouncement(noticeDTO).stream()
                .filter(item -> item.getPublishedDate() != null)
                .sorted(Comparator.comparing(NoticeDTO::getPublishedDate).reversed())
                .limit(1)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(simpleNoticeList)) {
            return null;
        }
        return simpleNoticeList.get(0);
    }

    @Override
    public Page<NoticeDTO> pageNotice(PageRequest pageRequest, NoticeDTO noticeDTO) {
        if (Boolean.parseBoolean(noticeDTO.getUserNotice())) {
            noticeDTO.setUserId(DetailsHelper.getUserDetails().getUserId());
            // 消息中心个人公告列表
            return noticeRepository.pageUserAnnouncement(pageRequest, noticeDTO);
        }
        // 消息管理的公告/通知列表
        return noticeRepository.pageNotice(pageRequest, noticeDTO);
    }
}
