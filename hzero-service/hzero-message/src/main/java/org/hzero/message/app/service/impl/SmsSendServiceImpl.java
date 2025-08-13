package org.hzero.message.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.*;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.entity.SmsServer;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.service.IMessageLangService;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.starter.sms.entity.SmsConfig;
import org.hzero.starter.sms.entity.SmsMessage;
import org.hzero.starter.sms.entity.SmsReceiver;
import org.hzero.starter.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * @author qingsheng.chen@hand-china.com
 */

@Service
public class SmsSendServiceImpl extends AbstractSendService implements SmsSendService {

    private static final Logger logger = LoggerFactory.getLogger(SmsSendServiceImpl.class);

    private final SmsServerService smsServerService;
    private final MessageRepository messageRepository;
    private final IMessageLangService messageLangService;
    private final MessageReceiverService messageReceiverService;
    private final MessageTemplateService messageTemplateService;
    private final MessageGeneratorService messageGeneratorService;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public SmsSendServiceImpl(SmsServerService smsServerService,
                              MessageRepository messageRepository,
                              IMessageLangService messageLangService,
                              MessageReceiverService messageReceiverService,
                              MessageTemplateService messageTemplateService,
                              MessageGeneratorService messageGeneratorService,
                              MessageConfigProperties messageConfigProperties,
                              MessageReceiverRepository messageReceiverRepository,
                              MessageTransactionRepository messageTransactionRepository) {
        this.smsServerService = smsServerService;
        this.messageRepository = messageRepository;
        this.messageLangService = messageLangService;
        this.messageReceiverService = messageReceiverService;
        this.messageTemplateService = messageTemplateService;
        this.messageGeneratorService = messageGeneratorService;
        this.messageConfigProperties = messageConfigProperties;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    /**
     * 因为阿里云和腾讯云的短信参数格式不同
     * 腾讯的短信参数map的key必须为1，2，3，4，5...
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(MessageSender messageSender) {
        Message result = null;
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(SmsSendService.class).asyncSendMessage(messageSender);
        } else {
            // 兼容甄云的多语言处理方式，按照语言分组
            List<MessageSender> senderList = messageLangService.getLang(messageSender);
            for (MessageSender sender : senderList) {
                result = sendMessageWithLang(sender);
            }
        }
        return result;
    }

    @Override
    @Async("commonAsyncTaskExecutor")
    public void asyncSendMessage(MessageSender messageSender) {
        // 兼容甄云的多语言处理方式，按照语言分组
        List<MessageSender> senderList = messageLangService.getLang(messageSender);
        for (MessageSender sender : senderList) {
            sendMessageWithLang(sender);
        }
    }

    private Message sendMessageWithLang(MessageSender messageSender) {
        // 生成消息记录
        Message message = createMessage(messageSender, HmsgConstant.MessageType.SMS);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(messageSender, message);
            // 获取消息接收人
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
            // 获取短信配置
            SmsServer smsServer = smsServerService.getSmsServer(messageSender.getTenantId(), messageSender.getServerCode());
            validServer(smsServer, messageSender.getTenantId(), messageSender.getServerCode());
            // 发送消息
            messageRepository.updateByPrimaryKeySelective(message);
            for (Receiver receiver : messageSender.getReceiverAddressList()) {
                messageReceiverRepository.insertSelective(new MessageReceiver().setMessageId(message.getMessageId())
                        .setTenantId(message.getTenantId()).setReceiverAddress(receiver.getPhone()).setIdd(receiver.getIdd()));
            }
            sendMessage(messageSender.getReceiverAddressList(), message, smsServer, messageSender.getObjectArgs());
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction()
                    .setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(message.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Send SMS failed [{} -> {}]", messageSender.getServerCode(), messageSender.getReceiverAddressList(), e.fillInStackTrace());
            failedProcess(message, e);
        }
        return message;
    }

    private void sendMessage(List<Receiver> receiverAddressList, Message message, SmsServer smsServer, Map<String, Object> argMap) {
        Map<String, String> args = new HashMap<>(16);
        List<String> argList = messageTemplateService.getTemplateArg(message.getTenantId(), message.getTemplateCode(), message.getLang());
        // 甄云逻辑，过滤模板不需要的参数
        if (argMap != null && argMap.size() > 0) {
            argMap.forEach((k, v) -> {
                if (argList.contains(k)) {
                    args.put(k, String.valueOf(v));
                }
            });
        }
        SmsService smsService = null;
        Map<String, SmsService> smsServiceMap = ApplicationContextHelper.getContext().getBeansOfType(SmsService.class);
        for (Map.Entry<String, SmsService> entry : smsServiceMap.entrySet()) {
            if (Objects.equals(entry.getValue().serverType(), smsServer.getServerTypeCode())) {
                smsService = entry.getValue();
                break;
            }
        }
        if (smsService == null) {
            throw new SendMessageException(String.format("Unsupported sms server! type: [%s], Please add sms dependencies!", smsServer.getServerTypeCode()));
        }
        smsService.setTemplateArgs(argList);
        List<SmsReceiver> smsReceiverList = new ArrayList<>();
        for (Receiver receiver : receiverAddressList) {
            smsReceiverList.add(new SmsReceiver().setPhone(receiver.getPhone()).setIdd(receiver.getIdd()));
        }
        SmsConfig smsConfig = new SmsConfig();
        SmsMessage smsMessage = new SmsMessage();
        BeanUtils.copyProperties(smsServer, smsConfig);
        BeanUtils.copyProperties(message, smsMessage);
        // 明文消息不存在，使用content
        smsMessage.setContent(StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent());
        smsService.smsSend(smsReceiverList, smsConfig, smsMessage, args);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        } // 发送消息
        try {
            // 获取短信配置
            SmsServer smsServer = smsServerService.getSmsServer(message.getTenantId(), message.getServerCode());
            validServer(smsServer, message.getTenantId(), message.getServerCode());
            sendMessage(message.getMessageReceiverList().stream()
                            .map(item -> new Receiver().setPhone(item.getReceiverAddress()).setIdd(item.getIdd()))
                            .collect(Collectors.toList()),
                    message, smsServer, message.getArgs());
            successProcessUpdate(message);
        } catch (Exception e) {
            logger.error("Send email failed [{} -> {}]", message.getServerCode(), message.getMessageReceiverList(), e.fillInStackTrace());
            failedProcessUpdate(message, e);
        }
        return message;
    }

    private void validServer(SmsServer smsServer, long tenantId, String serverCode) {
        if (smsServer == null || BaseConstants.Flag.NO.equals(smsServer.getEnabledFlag())) {
            throw new SendMessageException(String.format("Sms server not enabled : tenantId = [%d] , serverCode = [%s]", tenantId, serverCode));
        }
    }
}
