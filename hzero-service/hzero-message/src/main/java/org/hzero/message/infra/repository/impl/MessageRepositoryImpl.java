package org.hzero.message.infra.repository.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.infra.mapper.MessageMapper;
import org.hzero.message.infra.mapper.MessageReceiverMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息信息 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Component
public class MessageRepositoryImpl extends BaseRepositoryImpl<Message> implements MessageRepository {

    private final MessageMapper messageMapper;
    private final MessageReceiverMapper messageReceiverMapper;

    @Autowired
    public MessageRepositoryImpl(MessageMapper messageMapper,
                                 MessageReceiverMapper messageReceiverMapper) {
        this.messageMapper = messageMapper;
        this.messageReceiverMapper = messageReceiverMapper;
    }

    @Override
    public Page<Message> selectMessage(Long tenantId,
                                       String serverCode,
                                       String messageTypeCode,
                                       String subject,
                                       String trxStatusCode,
                                       Date startDate,
                                       Date endDate,
                                       String receiver,
                                       PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> messageMapper.selectMessage(tenantId, serverCode, messageTypeCode, subject, trxStatusCode, startDate, endDate, receiver));
    }

    @Override
    public UserMessageDTO selectMessageDetails(long transactionId) {
        return messageMapper.selectMessageDetails(transactionId);
    }

    @Override
    public List<Message> listRecentMessage(Long tenantId, String messageType, Date after) {
        List<Message> messageList = messageMapper.selectByCondition(Condition.builder(Message.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Message.FIELD_TENANT_ID, tenantId, true)
                        .andEqualTo(Message.FIELD_MESSAGE_TYPE_CODE, messageType)
                        .andGreaterThan(Message.FIELD_LAST_UPDATE_DATE, after))
                .build());
        if (!CollectionUtils.isEmpty(messageList)) {
            Map<Long, List<MessageReceiver>> messageReceiverMap = messageReceiverMapper.listReceiver(messageList.stream().map(Message::getMessageId).collect(Collectors.toList()))
                    .stream()
                    .collect(Collectors.groupingBy(MessageReceiver::getMessageId));
            for (Message message : messageList) {
                message.setMessageReceiverList(messageReceiverMap.getOrDefault(message.getMessageId(), Collections.emptyList()));
            }
        }
        return messageList;
    }
}
