package org.hzero.message.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ResponseUtils;
import org.hzero.message.api.dto.UnitUserDTO;
import org.hzero.message.app.service.NoticePublishedService;
import org.hzero.message.app.service.WebSendService;
import org.hzero.message.domain.entity.*;
import org.hzero.message.domain.repository.*;
import org.hzero.message.domain.vo.UserInfoVO;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.feign.IamRemoteService;
import org.hzero.message.infra.feign.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author minghui.qiu@hand-china.com
 */
@Service
public class NoticePublishedServiceImpl implements NoticePublishedService {

    @Autowired
    private NoticeReceiverRepository noticeReceiverRepository;
    @Autowired
    private NoticePublishedRepository noticePublishedRepository;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private WebSendService webSendService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NoticeContentRepository noticeContentRepository;
    @Autowired
    private IamRemoteService iamRemoteService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private UserMessageRepository userMessageRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public NoticePublished publicNotice(List<Long> publishedIds, Long noticeId, Long organizationId) {
        if (CollectionUtils.isEmpty(publishedIds)) {
            return null;
        }
        organizationId = organizationId == null ? DetailsHelper.getUserDetails().getTenantId() : organizationId;
        // 公告设置已发布
        Notice notice = Notice.updateStatus(noticeRepository, noticeId, Notice.STATUS_PUBLISHED);
        // 获取公告正文
        NoticeContent noticeContent = noticeContentRepository.selectOne(new NoticeContent().setNoticeId(noticeId));
        // 获取接收者
        List<NoticeReceiver> noticeReceiverList = noticeReceiverRepository.listReceiveRecordPage(publishedIds);
        // 创建公告发布记录
        NoticePublished noticePublished = new NoticePublished().setNoticeId(noticeId)
                .setPublishedStatusCode(HmsgConstant.PublishedStatus.DRAFT).setTenantId(organizationId);
        int count = noticePublishedRepository.selectCount(noticePublished);
        if (count > 0) {
            // 如果该公告有记录，表示重新发布，更新记录
            noticePublished = noticePublishedRepository.selectOne(noticePublished);
            noticePublishedRepository.updateByPrimaryKeySelective(noticePublished.setTitle(notice.getTitle()).setReceiverTypeCode(notice.getReceiverTypeCode())
                    .setNoticeBody(noticeContent.getNoticeBody()).setNoticeTypeCode(notice.getNoticeTypeCode())
                    .setPublishedDate(notice.getPublishedDate()).setStartDate(notice.getStartDate())
                    .setPublishedBy(notice.getPublishedBy()).setEndDate(notice.getEndDate())
                    .setAttachmentUuid(notice.getAttachmentUuid())
                    .setNoticeCategoryCode(notice.getNoticeCategoryCode())
                    .setPublishedStatusCode(Notice.STATUS_PUBLISHED));
        } else {
            // 没记录，新增一条记录
            noticePublishedRepository.insertSelective(noticePublished.setTitle(notice.getTitle()).setReceiverTypeCode(notice.getReceiverTypeCode())
                    .setNoticeBody(noticeContent.getNoticeBody()).setNoticeTypeCode(notice.getNoticeTypeCode())
                    .setPublishedDate(notice.getPublishedDate()).setStartDate(notice.getStartDate())
                    .setPublishedBy(notice.getPublishedBy()).setEndDate(notice.getEndDate())
                    .setAttachmentUuid(notice.getAttachmentUuid())
                    .setNoticeCategoryCode(notice.getNoticeCategoryCode())
                    .setPublishedStatusCode(Notice.STATUS_PUBLISHED));
        }
        if (!(publishedIds.size() == 1 && Objects.equals(publishedIds.get(0), noticePublished.getPublishedId()))) {
            noticeReceiverRepository.batchDelete(noticeReceiverRepository
                    .select(new NoticeReceiver().setPublishedId(noticePublished.getPublishedId())));
            for (NoticeReceiver item : noticeReceiverList) {
                noticeReceiverRepository.insertSelective(
                        item.setPublishedId(noticePublished.getPublishedId()).setTenantId(organizationId));
            }
        }

        if (HmsgConstant.NoticeReceiveTypeCode.SYS_NOTIFY.equals(notice.getReceiverTypeCode())) {
            // 通知类型，需要发送消息
            sendMessage(notice, noticeReceiverList, noticePublished, organizationId);
        } else {
            // 公告类型，缓存公告数据
            notice.refreshCachePublishedNotices(redisHelper, objectMapper);
        }
        return noticePublished;
    }

    private void sendMessage(Notice notice, List<NoticeReceiver> noticeReceiverList, NoticePublished noticePublished,
                             Long organizationId) {
        List<Receiver> receiverList = new ArrayList<>();
        List<UnitUserDTO> unitList = new ArrayList<>();
        List<UserGroupAssign> userGroupAssignList = new ArrayList<>();
        for (NoticeReceiver noticeReceiver : noticeReceiverList) {
            if (HmsgConstant.ReceiverRecordTypeCode.USER.equals(noticeReceiver.getReceiverTypeCode())) {
                receiverList.add(new Receiver().setUserId(noticeReceiver.getReceiverSourceId())
                        .setTargetUserTenantId(organizationId));
            } else if (HmsgConstant.ReceiverRecordTypeCode.USER_GROUP.equals(noticeReceiver.getReceiverTypeCode())) {
                UserGroupAssign dto = new UserGroupAssign();
                dto.setUserGroupId(noticeReceiver.getReceiverSourceId());
                dto.setTenantId(noticeReceiver.getTenantId());
                userGroupAssignList.add(dto);
            } else if (HmsgConstant.ReceiverRecordTypeCode.TENANT.equals(noticeReceiver.getReceiverTypeCode())) {
                int page = 0;
                int size = 400;
                List<UserInfoVO> userInfoList;
                do {
                    userInfoList = ResponseUtils.getResponse(iamRemoteService.pageUser(noticeReceiver.getReceiverSourceId(), page, size), new TypeReference<Page<UserInfoVO>>() {
                    }).getContent();
                    page += 1;
                    for (UserInfoVO userInfo : userInfoList) {
                        receiverList.add(new Receiver().setUserId(userInfo.getId()).setTargetUserTenantId(organizationId));
                    }
                } while (userInfoList.size() == size);
            } else if (HmsgConstant.ReceiverRecordTypeCode.UNIT.equals(noticeReceiver.getReceiverTypeCode())) {
                UnitUserDTO dto = new UnitUserDTO();
                dto.setUnitId(noticeReceiver.getReceiverSourceId());
                dto.setTenantId(noticeReceiver.getTenantId());
                unitList.add(dto);
            } else if (HmsgConstant.ReceiverRecordTypeCode.ALL.equals(noticeReceiver.getReceiverTypeCode()) &&
                    HmsgConstant.NoticeReceiveTypeCode.ANNOUNCE.equals(notice.getReceiverTypeCode())) {
                receiverList = userMessageRepository.getAllUser(organizationId);
                userGroupAssignList = null;
                unitList = null;
                break;
            } else if (HmsgConstant.ReceiverRecordTypeCode.ROLE.equals(noticeReceiver.getReceiverTypeCode())) {
                int page = 0;
                int size = 400;
                List<UserInfoVO> userList;
                do {
                    userList = ResponseUtils.getResponse(iamRemoteService.listRoleMembers(BaseConstants.DEFAULT_TENANT_ID, noticeReceiver.getReceiverSourceId(), page, size), new TypeReference<Page<UserInfoVO>>() {
                    }).getContent();
                    page += 1;
                    for (UserInfoVO userInfo : userList) {
                        receiverList.add(new Receiver().setUserId(userInfo.getId()).setTargetUserTenantId(organizationId));
                    }
                } while (userList.size() == size);
            }
        }
        // 查询用户组和组织下的用户
        if (CollectionUtils.isNotEmpty(userGroupAssignList)) {
            Set<Receiver> receiverSet = ResponseUtils.getResponse(iamRemoteService.listUserGroupAssignUsers(userGroupAssignList), new TypeReference<Set<Receiver>>() {
            });
            if (CollectionUtils.isNotEmpty(receiverSet)) {
                for (Receiver receiver : receiverSet) {
                    receiverList.add(new Receiver().setUserId(receiver.getUserId()).setTargetUserTenantId(receiver.getTargetUserTenantId()));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(unitList)) {
            Set<Receiver> receiverSet = ResponseUtils.getResponse(unitService.listUnitUsers(unitList), new TypeReference<Set<Receiver>>() {
            });
            if (CollectionUtils.isNotEmpty(receiverSet)) {
                for (Receiver receiver : receiverSet) {
                    receiverList.add(new Receiver().setUserId(receiver.getUserId()).setTargetUserTenantId(receiver.getTargetUserTenantId()));
                }
            }
        }
        for (Receiver receiver : receiverList.stream().distinct().collect(Collectors.toList())) {
            webSendService.saveUserMessage(HmsgConstant.UserMessageType.NOTICE, receiver.getUserId(), noticePublished.getPublishedId(),
                    organizationId, receiver.getTargetUserTenantId(), notice.getTitle());
        }
    }

    @Override
    public Page<NoticePublished> listNoticePublished(Long tenantId, PageRequest pageRequest, Long noticeId) {
        return noticePublishedRepository.listNoticePublished(tenantId, pageRequest, noticeId);
    }
}
