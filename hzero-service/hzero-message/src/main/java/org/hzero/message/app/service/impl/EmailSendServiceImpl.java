package org.hzero.message.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.mail.MessagingException;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.app.service.EmailSendService;
import org.hzero.message.app.service.EmailServerService;
import org.hzero.message.app.service.MessageGeneratorService;
import org.hzero.message.app.service.MessageReceiverService;
import org.hzero.message.config.MessageConfigProperties;
import org.hzero.message.domain.entity.*;
import org.hzero.message.domain.repository.EmailFilterRepository;
import org.hzero.message.domain.repository.MessageReceiverRepository;
import org.hzero.message.domain.repository.MessageRepository;
import org.hzero.message.domain.repository.MessageTransactionRepository;
import org.hzero.message.domain.service.IMessageLangService;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.message.infra.exception.SendMessageException;
import org.hzero.message.infra.supporter.EmailSupporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 邮箱消息发送实现
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 10:27
 */
@Service
public class EmailSendServiceImpl extends AbstractSendService implements EmailSendService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendServiceImpl.class);

    private final MessageRepository messageRepository;
    private final EmailServerService emailServerService;
    private final IMessageLangService messageLangService;
    private final EmailFilterRepository emailFilterRepository;
    private final MessageReceiverService messageReceiverService;
    private final MessageConfigProperties messageConfigProperties;
    private final MessageGeneratorService messageGeneratorService;
    private final MessageReceiverRepository messageReceiverRepository;
    private final MessageTransactionRepository messageTransactionRepository;

    @Autowired
    public EmailSendServiceImpl(MessageRepository messageRepository,
                                EmailServerService emailServerService,
                                IMessageLangService messageLangService,
                                EmailFilterRepository emailFilterRepository,
                                MessageReceiverService messageReceiverService,
                                MessageConfigProperties messageConfigProperties,
                                MessageGeneratorService messageGeneratorService,
                                MessageReceiverRepository messageReceiverRepository,
                                MessageTransactionRepository messageTransactionRepository) {
        this.messageRepository = messageRepository;
        this.emailServerService = emailServerService;
        this.messageLangService = messageLangService;
        this.emailFilterRepository = emailFilterRepository;
        this.messageReceiverService = messageReceiverService;
        this.messageConfigProperties = messageConfigProperties;
        this.messageGeneratorService = messageGeneratorService;
        this.messageReceiverRepository = messageReceiverRepository;
        this.messageTransactionRepository = messageTransactionRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(MessageSender messageSender) {
        return sendMessage(messageSender, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(MessageSender messageSender, Integer tryTimes) {
        Message result = null;
        if (messageConfigProperties.isAsync()) {
            ApplicationContextHelper.getContext().getBean(EmailSendService.class).asyncSendMessage(messageSender, tryTimes);
        } else {
            // 兼容甄云的多语言处理方式，按照语言分组
            List<MessageSender> senderList = messageLangService.getLang(messageSender);
            for (MessageSender sender : senderList) {
                result = sendMessageWithLang(sender, tryTimes);
            }
        }
        return result;
    }

    @Override
    @Async("commonAsyncTaskExecutor")
    public void asyncSendMessage(MessageSender messageSender, Integer tryTimes) {
        // 兼容甄云的多语言处理方式，按照语言分组
        List<MessageSender> senderList = messageLangService.getLang(messageSender);
        for (MessageSender sender : senderList) {
            sendMessageWithLang(sender, tryTimes);
        }
    }

    private Message sendMessageWithLang(MessageSender messageSender, Integer tryTimes) {
        // 生成消息记录
        Message message = createMessage(messageSender, HmsgConstant.MessageType.EMAIL);
        try {
            // 获取消息内容
            message = messageGeneratorService.generateMessage(messageSender, message);
            // 邮件附件
            message.setAttachmentList(messageSender.getAttachmentList());
            // 抄送
            message.setCcList(messageSender.getCcList());
            // 密送
            message.setBccList(messageSender.getBccList());
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
            // 获取邮箱配置
            EmailServer emailServer = emailServerService.getEmailServer(messageSender.getTenantId(), messageSender.getServerCode());
            validServer(emailServer, messageSender.getTenantId(), messageSender.getServerCode());
            // 覆盖默认的邮件重试次数
            if (tryTimes != null) {
                emailServer.setTryTimes(tryTimes);
            }
            // 发送消息
            messageRepository.updateByPrimaryKeySelective(message);
            sendMessage(messageSender.getReceiverAddressList(), message, emailServer, messageSender.getBatchSend());
            messageRepository.updateByPrimaryKeySelective(message.setSendFlag(BaseConstants.Flag.YES));
            MessageTransaction transaction = new MessageTransaction()
                    .setMessageId(message.getMessageId())
                    .setTrxStatusCode(HmsgConstant.TransactionStatus.S)
                    .setTenantId(message.getTenantId());
            messageTransactionRepository.insertSelective(transaction);
            message.setTransactionId(transaction.getTransactionId());
        } catch (Exception e) {
            logger.error("Send email failed [{} -> {}] : {}", message.getServerCode(), messageSender.getReceiverAddressList(), e.toString());
            failedProcess(message, e);
        }
        return message;
    }

    private void sendMessage(List<Receiver> receiverAddressList, Message message, EmailServer emailServer, Integer batchSend) {
        JavaMailSender javaMailSender = EmailSupporter.javaMailSender(emailServer);
        int sendCnt = (emailServer.getTryTimes() == null ? 0 : emailServer.getTryTimes()) + 1;
        List<String> emailList = receiverAddressList.stream()
                .map(Receiver::getEmail)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        // 判断黑白名单
        List<EmailFilter> emailFilterList = emailFilterRepository.select(new EmailFilter().setServerId(emailServer.getServerId()));
        String filterStrategy = emailServer.getFilterStrategy();
        List<String> usableList = new ArrayList<>();
        emailList.forEach(email -> {
                    boolean flag = filterEmail(filterStrategy, emailFilterList, email);
                    if (flag) {
                        messageReceiverRepository.insertSelective(new MessageReceiver()
                                .setMessageId(message.getMessageId())
                                .setTenantId(message.getTenantId())
                                .setReceiverAddress(email)
                                .setFilterFlag(BaseConstants.Flag.NO));
                        usableList.add(email);
                    } else {
                        messageReceiverRepository.insertSelective(new MessageReceiver()
                                .setMessageId(message.getMessageId())
                                .setTenantId(message.getTenantId())
                                .setReceiverAddress(email)
                                .setFilterFlag(BaseConstants.Flag.YES));
                    }
                }
        );
        // 在密送、抄送列表中过滤黑白名单
        List<String> ccList = message.getCcList();
        List<String> bccList = message.getBccList();
        if (!CollectionUtils.isEmpty(ccList)){
            ccList =  ccList.stream()
                    .filter(cc -> filterEmail(filterStrategy, emailFilterList, cc))
                    .collect(Collectors.toList());
            message.setCcList(ccList);
        }
        if (!CollectionUtils.isEmpty(bccList)){
            bccList = bccList.stream()
                    .filter(bcc -> filterEmail(filterStrategy, emailFilterList, bcc))
                    .collect(Collectors.toList());
            message.setBccList(bccList);
        }
        while (sendCnt > 0) {
            sendCnt--;
            try {
                if (CollectionUtils.isEmpty(usableList)) {
                    throw new CommonException(MessageAccessor.getMessage(HmsgConstant.ErrorCode.NULL_EMAIL_LIST).desc());
                }
                sendEmail(usableList, message, emailServer, javaMailSender, batchSend);
                break;
            } catch (MessagingException e) {
                logger.error("Error send email {}", message);
                if (sendCnt == 0) {
                    throw new CommonException(e);
                }
            }
        }
    }

    /**
     * 黑白名单筛选
     *
     * @param filterStrategy  筛选策略
     * @param emailFilterList 名单
     * @param email           当前邮箱
     */
    private boolean filterEmail(String filterStrategy, List<EmailFilter> emailFilterList, String email) {
        if (filterStrategy == null) {
            filterStrategy = "";
        }
        switch (filterStrategy) {
            case HmsgConstant.FilterStrategy.BLACK:
                for (EmailFilter item : emailFilterList) {
                    String address = item.getAddress();
                    if (address.contains(BaseConstants.Symbol.AT)) {
                        if (Objects.equals(email, address)) {
                            return false;
                        }
                    } else {
                        if (email.endsWith(address)) {
                            return false;
                        }
                    }
                }
                return true;
            case HmsgConstant.FilterStrategy.WHITE:
                for (EmailFilter item : emailFilterList) {
                    String address = item.getAddress();
                    if (address.contains(BaseConstants.Symbol.AT)) {
                        if (Objects.equals(email, address)) {
                            return true;
                        }
                    } else {
                        if (email.endsWith(address)) {
                            return true;
                        }
                    }
                }
                return false;
            default:
                return true;
        }
    }

    private void sendEmail(List<String> receiverAddressList, Message message, EmailServer emailServer, JavaMailSender javaMailSender, Integer batchSend) throws MessagingException {
        if (messageConfigProperties.getEmail().isFakeAction() && StringUtils.hasText(messageConfigProperties.getEmail().getFakeAccount())) {
            EmailSupporter.sendEmail(javaMailSender, emailServer, message, Collections.singletonList(messageConfigProperties.getEmail().getFakeAccount()), batchSend);
        } else if (!messageConfigProperties.getEmail().isFakeAction()) {
            EmailSupporter.sendEmail(javaMailSender, emailServer, message, receiverAddressList, batchSend);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message resendMessage(UserMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getMessageReceiverList())) {
            return message;
        }
        // 发送消息
        try {
            EmailServer emailServer = emailServerService.getEmailServer(message.getTenantId(), message.getServerCode());
            validServer(emailServer, message.getTenantId(), message.getServerCode());
            // 重新生成邮件消息内容
            Message messageContent = messageGeneratorService.generateMessage(message.getTenantId(), message.getTemplateCode(), message.getLang(), message.getArgs());
            if (messageContent != null) {
                message.setPlainContent(messageContent.getPlainContent());
            }
            sendEmail(message.getMessageReceiverList().stream()
                            .map(MessageReceiver::getReceiverAddress).collect(Collectors.toList()),
                    message, emailServer, EmailSupporter.javaMailSender(emailServer), BaseConstants.Flag.YES);
            successProcessUpdate(message);
        } catch (Exception e) {
            logger.error("Send email failed [{} -> {}] : {}", message.getServerCode(), message.getMessageReceiverList(), e.toString());
            failedProcessUpdate(message, e);
        }
        return message;
    }

    private void validServer(EmailServer emailServer, long tenantId, String serverCode) {
        if (emailServer == null || BaseConstants.Flag.NO.equals(emailServer.getEnabledFlag())) {
            throw new SendMessageException(String.format("Email server not found or not enabled : tenantId = [%d] , serverCode = [%s]", tenantId, serverCode));
        }
    }
}
