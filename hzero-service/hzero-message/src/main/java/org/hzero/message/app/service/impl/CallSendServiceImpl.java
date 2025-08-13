package org.hzero.message.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.CallSendService;
import org.hzero.message.app.service.CallServerService;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.CallServer;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageReceiver;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.service.IMessageLangService;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.starter.call.entity.CallConfig;
import org.hzero.starter.call.entity.CallMessage;
import org.hzero.starter.call.entity.CallReceiver;
import org.hzero.starter.call.service.CallService;
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
 * 语音消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/27 14:20
 */
@Service
public class CallSendServiceImpl extends AbstractSendService implements CallSendService {

    private static final Logger logger = LoggerFactory.getLogger(CallSendServiceImpl.class);

    private final MessageRepository messageRepository;
    private final CallServerService callServerService;
    private final IMessageLangService messageLangService;
    private final MessageReceiverService messageReceiverService;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageGeneratorService messageGeneratorService;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public CallSendServiceImpl(MessageRepository messageRepository,
                               CallServerService callServerService,
                               IMessageLangService messageLangService,
                               MessageReceiverService messageReceiverService,
                               MessageConfigProperties messageConfigProperties,
                               MessageGeneratorService messageGeneratorService,
                               MessageReceiverRepository messageReceiverRepository,
                               MessageTransactionRepository messageTransactionRepository) {
        this.messageRepository = messageRepository;
        this.callServerService = callServerService;
        this.messageLangService = messageLangService;
        this.messageReceiverService = messageReceiverService;
        this.messageConfigProperties = messageConfigProperties;
        this.messageGeneratorService = messageGeneratorService;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(MessageSender messageSender) {
        Message result = null;
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(CallSendService.class).asyncSendMessage(messageSender);
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
        Message message = createMessage(messageSender, HmsgConstant.MessageType.CALL);
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
            // 获取语音配置
            CallServer callServer = callServerService.getCallServer(messageSender.getTenantId(), messageSender.getServerCode());
            validServer(callServer, messageSender.getTenantId(), messageSender.getServerCode());
            // 发送消息
            messageRepository.updateByPrimaryKeySelective(message);
            for (Receiver receiver : messageSender.getReceiverAddressList()) {
                messageReceiverRepository.insertSelective(new MessageReceiver().setMessageId(message.getMessageId())
                        .setTenantId(message.getTenantId()).setReceiverAddress(receiver.getPhone()).setIdd(receiver.getIdd()));
            }
            sendMessage(messageSender.getReceiverAddressList(), message, callServer, messageSender.getObjectArgs());
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction()
                    .setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(message.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Send Call failed [{} -> {}]", messageSender.getServerCode(), messageSender.getReceiverAddressList(), e.fillInStackTrace());
            failedProcess(message, e);
        }
        return message;
    }

    private void sendMessage(List<Receiver> receiverAddressList, Message message, CallServer callServer, Map<String, Object> args) {
        CallService callService = null;
        Map<String, CallService> callServiceMap = ApplicationContextHelper.getContext().getBeansOfType(CallService.class);
        for (Map.Entry<String, CallService> entry : callServiceMap.entrySet()) {
            if (Objects.equals(entry.getValue().serverType(), callServer.getServerTypeCode())) {
                callService = entry.getValue();
                break;
            }
        }
        if (callService == null) {
            throw new SendMessageException(String.format("Unsupported server type : type code = [%s], tenantId = [%d], templateCode = [%s]", callServer.getServerTypeCode(), message.getTenantId(), message.getTemplateCode()));
        }
        List<CallReceiver> callReceiverList = new ArrayList<>();
        for (Receiver receiver : receiverAddressList) {
            callReceiverList.add(new CallReceiver().setPhone(receiver.getPhone()).setIdd(receiver.getIdd()));
        }
        CallConfig callConfig = new CallConfig();
        CallMessage callMessage = new CallMessage();
        BeanUtils.copyProperties(callServer, callConfig);
        BeanUtils.copyProperties(message, callMessage);
        // 明文消息不存在，使用content
        callMessage.setContent(StringUtils.isEmpty(message.getPlainContent()) ? message.getContent() : message.getPlainContent());
        Map<String, String> map = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(args)) {
            args.forEach((k, v) -> map.put(k, String.valueOf(v)));
        }
        callService.callSend(callReceiverList, callConfig, callMessage, map);
    }

    @Override
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        } // 发送消息
        try {
            // 获取短信配置
            CallServer callServer = callServerService.getCallServer(message.getTenantId(), message.getServerCode());
            validServer(callServer, message.getTenantId(), message.getServerCode());
            // 重新生成消息内容
            Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), message.getArgs());
            if (messageContent != null) {
                message.setPlainContent(messageContent.getPlainContent());
            }
            sendMessage(message.getMessageReceiverList().stream()
                            .map(item -> new Receiver().setPhone(item.getReceiverAddress()).setIdd(item.getIdd()))
                            .collect(Collectors.toList()),
                    message, callServer, message.getArgs());
            successProcessUpdate(message);
        } catch (Exception e) {
            logger.error("Send email failed [{} -> {}]", message.getServerCode(), message.getMessageReceiverList(), e.fillInStackTrace());
            failedProcessUpdate(message, e);
        }
        return message;
    }

    private void validServer(CallServer callServer, long tenantId, String serverCode) {
        if (callServer == null || BaseConstants.Flag.NO.equals(callServer.getEnabledFlag())) {
            throw new SendMessageException(String.format("Call server not enabled : tenantId = [%d] , serverCode = [%s]", tenantId, serverCode));
        }
    }
}
