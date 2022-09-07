package org.hzero.boot.message.feign.fallback;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.*;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 消息远程服务失败回调
 * </p>
 *
 * @author qingsheng.chen 2018/8/8 星期三 10:05
 */
@Component
public class MessageRemoteImpl implements MessageRemoteService {
    private static final Logger logger = LoggerFactory.getLogger(MessageRemoteImpl.class);

    @Override
    public ResponseEntity<String> resendMessage(Long organizationId, long transactionId) {
        logger.error("Error resend message, params[tenantId = {}, transactionId = {}]", organizationId, transactionId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> queryMessageTemplate(long tenantId, String templateCode, String lang) {
        logger.error("Error get message Template, params[tenantId = {}, templateCode = {}, lang = {}]", templateCode, templateCode, lang);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> queryReceiver(long tenantId, String typeCode, Map<String, Object> args) {
        logger.error("Error get receiver, params[tenantId = {}, typeCode = {}, args = {}]", tenantId, typeCode, args);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> queryOpenReceiver(long tenantId, String thirdPlatformType, String typeCode, Map<String, String> args) {
        logger.error("Error get receiver, params[tenantId = {}, messageType = {}, typeCode = {}, args = {}]", tenantId, thirdPlatformType, typeCode, args);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendWebMessage(long tenantId, MessageSender messageSender) {
        logger.error("Error send web message, params[messageSender = {}]", messageSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendEmail(long tenantId, MessageSender messageSender) {
        logger.error("Error send email, params[messageSender = {}]", messageSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendSms(long tenantId, MessageSender messageSender) {
        logger.error("Error send sms, params[messageSender = {}]", messageSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> sendMessage(long tenantId, MessageSender messageSender) {
        logger.error("Error send message, params[tenantId = {}, templateCode = {}, lang = {}, typeCodeList = {}, receiverAddressList = {}, args = {}]", messageSender.getTenantId(), messageSender.getMessageCode(), messageSender.getLang(), messageSender.getTypeCodeList(), messageSender.getReceiverAddressList(), messageSender.getArgs());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> sendAllMessage(long tenantId, AllSender sender) {
        logger.error("Error send message, params[tenantId = {}, sender = {}]", tenantId, sender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> listTemplateServerLine(Long tenantId, String messageCode) {
        logger.error("Error list Template Server Line, params[tenantId = {}， messageCode = {}]", tenantId, messageCode);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<String> getNoticeDetailsById(Long noticeId) {
        logger.error("Error get notice details, params[noticeId = {} ]", noticeId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendWeChatOfficial(Long organizationId, WeChatSender weChatSender) {
        logger.error("Error send weChat official msg, params[weChatSender = {}]", weChatSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendWeChatEnterprise(Long organizationId, WeChatSender weChatSender) {
        logger.error("Error send weChat enterprise msg, params[weChatSender = {}]", weChatSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendDingTalk(Long organizationId, DingTalkSender dingTalkSender) {
        logger.error("Error send weChat enterprise msg, params[dingTalkSender = {}]", dingTalkSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Override
    public ResponseEntity<MessageTransmission> sendWebHookMessage(Long organizationId, WebHookSender webHookSender) {
        logger.error("Error send webHook msg, params[webHookSender = {}]", webHookSender);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
