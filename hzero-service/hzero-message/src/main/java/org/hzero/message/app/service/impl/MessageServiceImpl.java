package org.hzero.message.app.service.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.api.dto.UserSimpleDTO;
import org.hzero.message.app.service.*;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.entity.UserMessage;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.repository.UserMessageRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 消息信息应用服务默认实现
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final SmsSendService smsSendService;
    private final WebSendService webSendService;
    private final CallSendService callSendService;
    private final EmailSendService emailSendService;
    private final MessageRepository messageRepository;
    private final WeChatSendService weChatSendService;
    private final WebHookSendService webHookSendService;
    private final UserMessageService userMessageService;
    private final DingTalkSendService dingTalkSendService;
    private final UserMessageRepository userMessageRepository;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public MessageServiceImpl(ObjectMapper objectMapper,
                              SmsSendService smsSendService,
                              WebSendService webSendService,
                              CallSendService callSendService,
                              EmailSendService emailSendService,
                              MessageRepository messageRepository,
                              WeChatSendService weChatSendService,
                              WebHookSendService webHookSendService,
                              UserMessageService userMessageService,
                              DingTalkSendService dingTalkSendService,
                              UserMessageRepository userMessageRepository,
                              MessageReceiverRepository messageReceiverRepository,
                              MessageTransactionRepository messageTransactionRepository) {
        this.objectMapper = objectMapper;
        this.smsSendService = smsSendService;
        this.webSendService = webSendService;
        this.callSendService = callSendService;
        this.emailSendService = emailSendService;
        this.messageRepository = messageRepository;
        this.weChatSendService = weChatSendService;
        this.webHookSendService = webHookSendService;
        this.userMessageService = userMessageService;
        this.dingTalkSendService = dingTalkSendService;
        this.userMessageRepository = userMessageRepository;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    @Override
    public Page<Message> listMessage(Long tenantId,
                                     String serverCode,
                                     String messageTypeCode,
                                     String subject,
                                     String trxStatusCode,
                                     Date startDate,
                                     Date endDate,
                                     String receiver,
                                     PageRequest pageRequest) {
        return messageRepository.selectMessage(tenantId, serverCode, messageTypeCode, subject, trxStatusCode, startDate, endDate, receiver, pageRequest);
    }

    @Override
    public Message getMessage(Long tenantId, long messageId) {
        return messageRepository.selectOne(new Message().setMessageId(messageId).setTenantId(tenantId));
    }

    @Override
    public Page<MessageReceiver> listMessageReceiver(Long tenantId, long messageId, PageRequest pageRequest) {
        Message message = messageRepository.selectOne(new Message().setMessageId(messageId).setTenantId(tenantId));
        Assert.notNull(message, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Page<MessageReceiver> page = messageReceiverRepository.pageAndSort(pageRequest, new MessageReceiver().setMessageId(messageId));
        if (CollectionUtils.isNotEmpty(page.getContent()) && Objects.equals(message.getMessageTypeCode(), HmsgConstant.MessageType.WEB)) {
            page.forEach(item -> {
                Long userId = Long.valueOf(item.getReceiverAddress());
                UserSimpleDTO user = messageReceiverRepository.getUser(userId);
                if (user != null) {
                    item.setReceiverAddress(user.toString());
                }
            });
        }
        return page;
    }

    @Override
    public MessageTransaction getMessageTransaction(Long tenantId, long transactionId) {
        return messageTransactionRepository.selectOne(new MessageTransaction()
                .setTransactionId(transactionId)
                .setTenantId(tenantId));
    }

    @Override
    public List<Message> listRecentMessage(Long tenantId, String messageType, long before) {
        return messageRepository.listRecentMessage(tenantId, messageType, new Date(System.currentTimeMillis() - before));
    }

    @Override
    public Message resendMessage(Long tenantId, long transactionId) {
        UserMessageDTO message = messageRepository.selectMessageDetails(transactionId);
        Assert.notNull(message, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        // 获取消息参数
        message.setArgs(buildArgs(message.getSendArgs()));
        if (tenantId != null) {
            Assert.isTrue(tenantId.equals(message.getFromTenantId()), BaseConstants.ErrorCode.DATA_INVALID);
        }
        switch (message.getMessageTypeCode()) {
            case HmsgConstant.MessageType.WEB:
                return webSendService.resendMessage(message);
            case HmsgConstant.MessageType.EMAIL:
                return emailSendService.resendMessage(message);
            case HmsgConstant.MessageType.SMS:
                return smsSendService.resendMessage(message);
            case HmsgConstant.MessageType.CALL:
                return callSendService.resendMessage(message);
            case HmsgConstant.MessageType.WC_O:
                return weChatSendService.resendOfficialMessage(message);
            case HmsgConstant.MessageType.WC_E:
                return weChatSendService.resendEnterpriseMessage(message);
            case HmsgConstant.MessageType.DT:
                return dingTalkSendService.resendMessage(message);
            case HmsgConstant.MessageType.WEB_HOOK:
                return webHookSendService.resendWebHookMessage(message);
            default:
                throw new SendMessageException(BaseConstants.ErrorCode.DATA_INVALID);
        }
    }

    private Map<String, Object> buildArgs(String argsStr) {
        Map<String, Object> args = new HashMap<>(16);
        try {
            if (StringUtils.hasText(argsStr)) {
                args = objectMapper.readValue(argsStr, new TypeReference<Map<String, Object>>() {});
                for(Map.Entry<String, Object> entry : args.entrySet()) {
                    String key = entry.getKey();
                    try {
                        args.put(key, DataSecurityHelper.decrypt(String.valueOf(entry.getValue())));
                    } catch (Exception e) {
                        args.put(key, entry.getValue());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return args;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Message> messageList) {
        messageList.forEach(item -> {
            Message message = messageRepository.selectByPrimaryKey(item.getMessageId());
            if (message != null) {
                Long messageId = message.getMessageId();
                // 删除站内消息
                if (Objects.equals(message.getMessageTypeCode(), HmsgConstant.MessageType.WEB)) {
                    List<UserMessage> userMessageList = userMessageRepository.select(new UserMessage().setMessageId(messageId));
                    userMessageList.forEach(userMessage -> {
                        if (Objects.equals(userMessage.getReadFlag(), BaseConstants.Flag.NO)) {
                            List<Long> idList = new ArrayList<>();
                            idList.add(messageId);
                            userMessageService.readMessage(userMessage.getTenantId(), userMessage.getUserId(), idList);
                        }
                    });
                    userMessageRepository.delete(new UserMessage().setMessageId(messageId));
                }
                // 删除事务
                messageTransactionRepository.delete(new MessageTransaction().setMessageId(messageId));
                // 删除接收人
                messageReceiverRepository.delete(new MessageReceiver().setMessageId(messageId));
                // 删除消息
                messageRepository.deleteByPrimaryKey(messageId);
            }
        });
    }
}
