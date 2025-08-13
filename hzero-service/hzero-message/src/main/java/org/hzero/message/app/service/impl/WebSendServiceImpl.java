package org.hzero.message.app.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.message.api.dto.SimpleMessageDTO;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.app.service.UserMessageService;
import org.hzero.message.app.service.WebSendService;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.domain.service.IMessageLangService;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 站内消息发送接口实现
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 9:30
 */
@Service
public class WebSendServiceImpl extends AbstractSendService implements WebSendService {

    private final MessageGeneratorService messageGeneratorService;
    private final UserMessageService userMessageService;
    private final MessageRepository messageRepository;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;
    private final UserMessageRepository userMessageRepository;
    private final MessageReceiverService messageReceiverService;
    private final IMessageLangService messageLangService;

    @Autowired
    public WebSendServiceImpl(MessageGeneratorService messageGeneratorService,
                              UserMessageService userMessageService,
                              MessageRepository messageRepository,
                              MessageReceiverRepository messageReceiverRepository,
                              MessageTransactionRepository messageTransactionRepository,
                              UserMessageRepository userMessageRepository,
                              MessageReceiverService messageReceiverService,
                              IMessageLangService messageLangService) {
        this.messageGeneratorService = messageGeneratorService;
        this.userMessageService = userMessageService;
        this.messageRepository = messageRepository;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
        this.userMessageRepository = userMessageRepository;
        this.messageReceiverService = messageReceiverService;
        this.messageLangService = messageLangService;
    }

    @Override
    public Message sendMessage(MessageSender messageSender) {
        // 兼容甄云的多语言处理方式，按照语言分组
        List<MessageSender> senderList = messageLangService.getLang(messageSender);
        Message result = null;
        for (MessageSender sender : senderList) {
            result = sendMessageWithLang(sender);
        }
        return result;
    }

    private Message sendMessageWithLang(MessageSender messageSender) {
        // 生成消息记录
        Message message = createMessage(messageSender, HmsgConstant.MessageType.WEB);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(messageSender, message);
            // 获取消息接收人 noinspection Duplicates
            messageSender = messageReceiverService.queryReceiver(messageSender);
            if (CollectionUtils.isEmpty(messageSender.getReceiverAddressList())) {
                messageRepository.updateOptional(message.setSendFlag(BaseConstants.Flag.NO), Message.FIELD_SEND_FLAG);
                MessageTransaction transaction = new MessageTransaction()
                        .setMessageId(message.getMessageId())
                        .setTrxStatusCode(HmsgConstant.TransactionStatus.P)
                        .setTenantId(message.getTenantId())
                        .setTransactionMessage(MessageAccessor.getMessage(HmsgConstant.ErrorCode.NO_RECEIVER).desc());
                messageTransactionRepository.insertSelective(transaction);
                message.setTransactionId(transaction.getTransactionId());
                return message;
            }
            // 发送消息
            messageRepository.updateByPrimaryKeySelective(message);
            sendMessage(messageSender.getTenantId(), messageSender.getReceiverAddressList(), message);
            // 记录成功
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction()
                    .setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(messageSender.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            // 记录失败
            failedProcess(message, e);
        }
        return message;
    }

    private void sendMessage(Long fromTenantId, List<Receiver> receiverList, Message message) {
        Set<Pair<Long, Long>> cacheSet = new HashSet<>();
        receiverList.forEach(receiver -> {
            if (receiver.getTargetUserTenantId() == null || receiver.getUserId() == null) {
                throw new CommonException("Sending a message error because no recipient or target tenant is specified : " + receiver.toString());
            }
            // 接收消息去重
            Pair<Long, Long> cache = Pair.of(receiver.getTargetUserTenantId(), receiver.getUserId());
            if (cacheSet.contains(cache)) {
                return;
            }
            messageReceiverRepository.insertSelective(new MessageReceiver().setMessageId(message.getMessageId())
                    .setTenantId(receiver.getTargetUserTenantId())
                    .setReceiverAddress(String.valueOf(receiver.getUserId())));
            // 缓存
            saveUserMessage(HmsgConstant.UserMessageType.MSG, receiver.getUserId(), message.getMessageId(), fromTenantId, receiver.getTargetUserTenantId(), message.getSubject());
            cacheSet.add(cache);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        }
        try {
            // 发送消息
            message.getMessageReceiverList().forEach(receiver -> {
                if (!StringUtils.hasText(receiver.getReceiverAddress()) || receiver.getTenantId() == null) {
                    throw new CommonException("Sending a message error because no recipient or target tenant is specified : " + receiver.toString());
                }
                resaveUserMessage(Long.valueOf(receiver.getReceiverAddress()), message.getMessageId(), message.getFromTenantId(), receiver.getTenantId(), message.getSubject());
            });
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }

    @Override
    public void saveUserMessage(String messageType, Long userId, long messageId, long fromTenantId, long targetUserTenantId, String subject) {
        UserMessage userMessage = new UserMessage().setUserMessageTypeCode(messageType)
                .setUserId(userId)
                .setMessageId(messageId)
                .setReadFlag(BaseConstants.Flag.NO)
                .setFromTenantId(fromTenantId)
                .setTenantId(targetUserTenantId);
        userMessage.setCreationDate(new Date());
        userMessageRepository.insertSelective(userMessage);
        UserMessage queryMessage = userMessageRepository.getUserMessage(userMessage.getUserMessageId());
        SimpleMessageDTO simpleMessageDTO = new SimpleMessageDTO().setUserMessageId(queryMessage.getUserMessageId())
                .setMessageId(messageId)
                .setUserMessageTypeCode(messageType)
                .setUserMessageTypeMeaning(queryMessage.getUserMessageTypeMeaning())
                .setSubject(subject)
                .setTenantId(targetUserTenantId)
                .setCreationDate(userMessage.getCreationDate());
        userMessageService.createSimpleMessage(targetUserTenantId, userMessage.getUserId(), simpleMessageDTO);
    }

    private void resaveUserMessage(Long userId, long messageId, long fromTenantId, long targetTenantId, String subject) {
        UserMessage userMessage = userMessageRepository.selectOne(new UserMessage().setTenantId(targetTenantId).setMessageId(messageId).setUserId(userId));
        if (userMessage == null) {
            saveUserMessage(HmsgConstant.UserMessageType.MSG, userId, messageId, fromTenantId, targetTenantId, subject);
        } else {
            if (BaseConstants.Flag.YES.equals(userMessage.getReadFlag())) {
                userMessageRepository.updateOptional(userMessage.setReadFlag(BaseConstants.Flag.NO), UserMessage.FIELD_READ_FLAG);
                SimpleMessageDTO simpleMessageDTO = new SimpleMessageDTO().setUserMessageId(userMessage.getUserMessageId())
                        .setMessageId(messageId)
                        .setUserMessageTypeCode(HmsgConstant.UserMessageType.MSG)
                        .setSubject(subject)
                        .setTenantId(targetTenantId)
                        .setCreationDate(userMessage.getCreationDate());
                userMessageService.createSimpleMessage(targetTenantId, userMessage.getUserId(), simpleMessageDTO);
            }
        }
    }
}
