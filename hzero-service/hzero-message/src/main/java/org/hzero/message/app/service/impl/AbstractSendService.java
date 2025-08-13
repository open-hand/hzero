package org.hzero.message.app.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;
import org.hzero.message.domain.entity.MessageTransaction;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.infra.constant.HmsgConstant;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 消息发送抽象
 * </p>
 *
 * @author qingsheng.chen 2019/1/22 星期二 16:17
 */
public abstract class AbstractSendService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageTransactionRepository messageTransactionRepository;

    public Message createMessage(MessageSender messageSender, String messageTypeCode) {
        Message message = new Message().setSendFlag(BaseConstants.Flag.NO)
                .setTenantId(messageSender.getTenantId())
                .setTemplateCode(messageSender.getMessageCode())
                .setLang(messageSender.getLang())
                .setMessageTypeCode(messageTypeCode)
                .setServerCode(messageSender.getServerCode());
        messageRepository.insertSelective(message);
        return message;
    }

    public Message createMessage(WeChatSender weChatSender, String messageTypeCode) {
        Message message = new Message().setSendFlag(BaseConstants.Flag.NO)
                .setTenantId(weChatSender.getTenantId())
                .setTemplateCode(weChatSender.getMessageCode())
                .setLang(weChatSender.getLang())
                .setMessageTypeCode(messageTypeCode)
                .setServerCode(weChatSender.getServerCode());
        messageRepository.insertSelective(message);
        return message;
    }

    public Message createMessage(DingTalkSender dingTalkSender, String messageTypeCode) {
        Message message = new Message().setSendFlag(BaseConstants.Flag.NO)
                .setTenantId(dingTalkSender.getTenantId())
                .setTemplateCode(dingTalkSender.getMessageCode())
                .setLang(dingTalkSender.getLang())
                .setMessageTypeCode(messageTypeCode)
                .setServerCode(dingTalkSender.getServerCode());
        messageRepository.insertSelective(message);
        return message;
    }

    public Message createMessage(WebHookSender webHookSender, String messageTypeCode) {
        Message message = new Message().setSendFlag(BaseConstants.Flag.NO)
                .setTenantId(webHookSender.getTenantId())
                .setTemplateCode(webHookSender.getMessageCode())
                .setLang(webHookSender.getLang())
                .setMessageTypeCode(messageTypeCode)
                .setServerCode(webHookSender.getServerCode());
        messageRepository.insertSelective(message);
        return message;
    }

    public void failedProcess(Message message, Exception e) {
        messageRepository.updateOptional(message.setSendFlag(BaseConstants.Flag.NO), Message.FIELD_SEND_FLAG);
        MessageTransaction transaction = new MessageTransaction()
                .setMessageId(message.getMessageId())
                .setTrxStatusCode(HmsgConstant.TransactionStatus.F)
                .setTenantId(message.getTenantId())
                .setTransactionMessage(ExceptionUtils.getStackTrace(e));
        messageTransactionRepository.insertSelective(transaction);
        message.setTransactionId(transaction.getTransactionId());
    }

    public void successProcessUpdate(UserMessageDTO message) {
        MessageTransaction messageTransaction = new MessageTransaction().setTransactionId(message.getTransactionId())
                .setTrxStatusCode(HmsgConstant.TransactionStatus.S);
        messageTransaction.setObjectVersionNumber(message.getTransactionObjectVersionNumber());
        messageTransactionRepository.updateOptional(messageTransaction,
                MessageTransaction.FIELD_TRX_STATUS_CODE, MessageTransaction.FIELD_TRANSACTION_MESSAGE);
    }

    public void failedProcessUpdate(UserMessageDTO message, Exception e) {
        messageRepository.updateOptional(message.setSendFlag(BaseConstants.Flag.NO), Message.FIELD_SEND_FLAG);
        MessageTransaction messageTransaction = new MessageTransaction().setTransactionId(message.getTransactionId())
                .setTrxStatusCode(HmsgConstant.TransactionStatus.F)
                .setTransactionMessage(ExceptionUtils.getStackTrace(e));
        messageTransaction.setObjectVersionNumber(message.getTransactionObjectVersionNumber());
        messageTransactionRepository.updateOptional(messageTransaction,
                MessageTransaction.FIELD_TRX_STATUS_CODE, MessageTransaction.FIELD_TRANSACTION_MESSAGE);
    }
}
