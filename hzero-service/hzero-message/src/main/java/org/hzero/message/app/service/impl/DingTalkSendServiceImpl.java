package org.hzero.message.app.service.impl;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.entity.DingTalkMsgType;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.dd.service.DingCorpMessageService;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.DingTalkSendService;
import org.hzero.message.app.service.DingTalkServerService;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.supporter.DingTalkServerSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 钉钉消息发送
 *
 * @author zifeng.ding@hand-china.com 2019/11/14 11:34
 */
@Service
public class DingTalkSendServiceImpl extends AbstractSendService implements DingTalkSendService {

    private final MessageRepository messageRepository;
    private final DingTalkServerService dingTalkServerService;
    private final DingCorpMessageService dingCorpMessageService;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageGeneratorService messageGeneratorService;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public DingTalkSendServiceImpl(MessageRepository messageRepository,
                                   DingTalkServerService dingTalkServerService,
                                   DingCorpMessageService dingCorpMessageService,
                                   MessageConfigProperties messageConfigProperties,
                                   MessageGeneratorService messageGeneratorService,
                                   MessageReceiverRepository messageReceiverRepository,
                                   MessageTransactionRepository messageTransactionRepository) {
        this.messageRepository = messageRepository;
        this.dingTalkServerService = dingTalkServerService;
        this.dingCorpMessageService = dingCorpMessageService;
        this.messageConfigProperties = messageConfigProperties;
        this.messageGeneratorService = messageGeneratorService;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(DingTalkSender dingTalkSender) {
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(DingTalkSendService.class).asyncSendMessage(dingTalkSender);
            return null;
        } else {
            return sendDingTalk(dingTalkSender);
        }
    }

    @Override
    @Async("commonAsyncTaskExecutor")
    public void asyncSendMessage(DingTalkSender dingTalkSender) {
        sendDingTalk(dingTalkSender);
    }

    private Message sendDingTalk(DingTalkSender dingTalkSender) {
        // 处理默认应用ID
        if (dingTalkSender.getAgentId() == null) {
            dingTalkSender.setAgentId(dingTalkServerService.getDefaultAgentId(dingTalkSender.getTenantId(), dingTalkSender.getServerCode()));
        }
        // 清空集合中的空字符串
        dingTalkSender.setUserIdList(removeEmptyStr(dingTalkSender.getUserIdList()));
        dingTalkSender.setDeptIdList(removeEmptyStr(dingTalkSender.getDeptIdList()));
        // 生成消息记录
        Message message = createMessage(dingTalkSender, HmsgConstant.MessageType.DT);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(dingTalkSender, message);
            if (CollectionUtils.isEmpty(dingTalkSender.getUserIdList()) && CollectionUtils.isEmpty(dingTalkSender.getDeptIdList()) && !dingTalkSender.getToAllUser()) {
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
            sendDingTalkMessage(dingTalkSender.getTenantId(), dingTalkSender.getServerCode(), dingTalkSender.getUserIdList(), message, dingTalkSender.getMsgType());
            // 记录成功
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction().setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(dingTalkSender.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            // 记录失败
            failedProcess(message, e);
        }
        return message;
    }

    @Override
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        }
        // 发送消息
        try {
            String token = dingTalkServerService.getToken(message.getTenantId(), message.getServerCode());
            List<String> userList = message.getMessageReceiverList().stream().map(MessageReceiver::getReceiverAddress).collect(Collectors.toList());
            DingTalkMsgType msgType = null;
            Map<String, Object> map = message.getArgs();
            if (map.containsKey(WeChatSender.FIELD_MSG_TYPE)) {
                msgType = DingTalkMsgType.getType(String.valueOf(map.get(WeChatSender.FIELD_MSG_TYPE)));
                map.remove(WeChatSender.FIELD_MSG_TYPE);
            }
            // 重新生成消息内容
            Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), map);
            if (messageContent != null) {
                message.setPlainContent(messageContent.getPlainContent());
            }
            sendDingTalkMessage(token, userList, message, msgType);
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }

    private void sendDingTalkMessage(Long tenantId, String serverCode, List<String> userIdList, Message message, DingTalkMsgType msgType) {
        List<String> receiverList = saveReceiver(tenantId, userIdList, message);
        String token = dingTalkServerService.getToken(tenantId, serverCode);
        Assert.isTrue(StringUtils.isNotBlank(token), BaseConstants.ErrorCode.DATA_INVALID);
        sendDingTalkMessage(token, receiverList, message, msgType);
    }

    private void sendDingTalkMessage(String token, List<String> userList, Message message, DingTalkMsgType msgType) {
        DingTalkServerSupporter.sendMessage(dingCorpMessageService, token, userList, message, msgType);
    }


    private List<String> saveReceiver(Long tenantId, List<String> userList, Message message) {
        // 记录接收人
        userList.forEach(receiver -> messageReceiverRepository.insertSelective(new MessageReceiver().setMessageId(message.getMessageId()).setTenantId(tenantId).setReceiverAddress(receiver)));
        return userList;
    }

    private List<String> removeEmptyStr(List<String> strings) {
        if (CollectionUtils.isEmpty(strings)) {
            return Collections.emptyList();
        }
        return strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
    }
}
