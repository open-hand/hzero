package org.hzero.message.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.message.entity.WeChatMsgType;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.WeChatEnterpriseService;
import org.hzero.message.app.service.WeChatOfficialService;
import org.hzero.message.app.service.WeChatSendService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.supporter.WeChatEnterpriseSupporter;
import org.hzero.message.infra.supporter.WeChatOfficialSupporter;
import org.hzero.wechat.enterprise.service.WechatCorpMessageService;
import org.hzero.wechat.service.BaseWechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 微信消息发送实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 13:37
 */
@Service
public class WeChatSendServiceImpl extends AbstractSendService implements WeChatSendService {

    private final BaseWechatService weChatService;
    private final MessageRepository messageRepository;
    private final WeChatOfficialService weChatOfficialService;
    private final MessageConfigProperties messageConfigProperties;
    private final WeChatEnterpriseService weChatEnterpriseService;
    private final MessageGeneratorService messageGeneratorService;
    private final WechatCorpMessageService weChatCorpMessageService;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public WeChatSendServiceImpl(BaseWechatService weChatService,
                                 MessageRepository messageRepository,
                                 WeChatOfficialService weChatOfficialService,
                                 MessageConfigProperties messageConfigProperties,
                                 WeChatEnterpriseService weChatEnterpriseService,
                                 MessageGeneratorService messageGeneratorService,
                                 WechatCorpMessageService weChatCorpMessageService,
                                 MessageReceiverRepository messageReceiverRepository,
                                 MessageTransactionRepository messageTransactionRepository) {
        this.weChatService = weChatService;
        this.messageRepository = messageRepository;
        this.weChatOfficialService = weChatOfficialService;
        this.messageConfigProperties = messageConfigProperties;
        this.weChatEnterpriseService = weChatEnterpriseService;
        this.messageGeneratorService = messageGeneratorService;
        this.weChatCorpMessageService = weChatCorpMessageService;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendOfficialMessage(WeChatSender weChatSender) {
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(WeChatSendService.class).asyncSendOfficialMessage(weChatSender);
            return null;
        } else {
            return sendOfficial(weChatSender);
        }
    }

    @Override
    @Async("commonAsyncTaskExecutor")
    public void asyncSendOfficialMessage(WeChatSender weChatSender) {
        sendOfficial(weChatSender);
    }

    private Message sendOfficial(WeChatSender weChatSender) {
        // 生成消息记录
        Message message = createMessage(weChatSender, HmsgConstant.MessageType.WC_O);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(weChatSender, message);
            if (CollectionUtils.isEmpty(weChatSender.getUserList())) {
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
            sendOfficialMessage(weChatSender.getTenantId(), weChatSender.getServerCode(), weChatSender.getUserList(), message);
            // 记录成功
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction().setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(weChatSender.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            // 记录失败
            failedProcess(message, e);
        }
        return message;
    }

    private List<String> saveReceiver(Long tenantId, List<String> userList, Message message) {
        // 接收人去重
        List<String> receiverList = userList.stream().distinct().collect(Collectors.toList());
        // 记录接收人
        receiverList.forEach(receiver -> messageReceiverRepository.insertSelective(new MessageReceiver()
                .setMessageId(message.getMessageId()).setTenantId(tenantId).setReceiverAddress(receiver)));
        return receiverList;
    }

    private void sendOfficialMessage(Long tenantId, String serverCode, List<String> userList, Message message) {
        List<String> receiverList = saveReceiver(tenantId, userList, message);
        // 发送消息
        String token = weChatOfficialService.getToken(tenantId, serverCode);
        Assert.isTrue(StringUtils.isNotBlank(token), BaseConstants.ErrorCode.DATA_INVALID);
        sendOfficialMessage(token, receiverList, message);
    }

    private void sendOfficialMessage(String token, List<String> userList, Message message) {
        WeChatOfficialSupporter.sendMessage(weChatService, token, message, userList);
    }

    @Override
    public Message resendOfficialMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        }
        // 发送消息
        try {
            // 获取token
            String token = weChatOfficialService.getToken(message.getTenantId(), message.getServerCode());
            List<String> userList = message.getMessageReceiverList().stream().map(MessageReceiver::getReceiverAddress).collect(Collectors.toList());
            sendOfficialMessage(token, userList, message);
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }

    @Override
    public Message sendEnterpriseMessage(WeChatSender weChatSender) {
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(WeChatSendService.class).asyncSendEnterpriseMessage(weChatSender);
            return null;
        } else {
            return sendEnterprise(weChatSender);
        }
    }

    @Override
    public void asyncSendEnterpriseMessage(WeChatSender weChatSender) {
        sendEnterprise(weChatSender);
    }

    private Message sendEnterprise(WeChatSender weChatSender) {
        // 处理默认应用ID
        if (weChatSender.getAgentId() == null) {
            weChatSender.setAgentId(weChatEnterpriseService.getDefaultAgentId(weChatSender.getTenantId(), weChatSender.getServerCode()));
        }
        // 生成消息记录
        Message message = createMessage(weChatSender, HmsgConstant.MessageType.WC_E);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(weChatSender, message);
            if (CollectionUtils.isEmpty(weChatSender.getUserIdList()) && CollectionUtils.isEmpty(weChatSender.getPartyList()) && CollectionUtils.isEmpty(weChatSender.getTagList())) {
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
            sendEnterpriseMessage(weChatSender.getTenantId(), weChatSender.getServerCode(), weChatSender.getUserIdList(), message, weChatSender.getMsgType());
            // 记录成功
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction().setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S).setTenantId(weChatSender.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            // 记录失败
            failedProcess(message, e);
        }
        return message;
    }

    private void sendEnterpriseMessage(Long tenantId, String serverCode, List<String> userIdList, Message message, WeChatMsgType msgType) {
        List<String> receiverList = saveReceiver(tenantId, userIdList, message);
        String token = weChatEnterpriseService.getToken(tenantId, serverCode);
        Assert.isTrue(StringUtils.isNotBlank(token), BaseConstants.ErrorCode.DATA_INVALID);
        sendEnterpriseMessage(token, receiverList, message, msgType);
    }

    private void sendEnterpriseMessage(String token, List<String> userList, Message message, WeChatMsgType msgType) {
        WeChatEnterpriseSupporter.sendMessage(weChatCorpMessageService, token, userList, message, msgType);
    }

    @Override
    public Message resendEnterpriseMessage(UserMessageDTO message) {
        // 发送消息
        try {
            // 获取token
            String token = weChatEnterpriseService.getToken(message.getTenantId(), message.getServerCode());
            List<String> userList = message.getMessageReceiverList().stream().map(MessageReceiver::getReceiverAddress).collect(Collectors.toList());
            WeChatMsgType msgType = null;
            Map<String, Object> map = message.getArgs();
            if (map.containsKey(WeChatSender.FIELD_MSG_TYPE)) {
                msgType = WeChatMsgType.getType(String.valueOf(map.get(WeChatSender.FIELD_MSG_TYPE)));
                map.remove(WeChatSender.FIELD_MSG_TYPE);
            }
            // 重新生成消息内容
            Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), map);
            if (messageContent != null) {
                message.setPlainContent(messageContent.getPlainContent());
            }
            sendEnterpriseMessage(token, userList, message, msgType);
            successProcessUpdate(message);
        } catch (Exception e) {
            failedProcessUpdate(message, e);
        }
        return message;
    }
}
