package org.hzero.boot.message.service;

import org.hzero.boot.message.entity.*;
import org.hzero.boot.message.feign.MessageRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

/**
 * <p>
 * 异步消息发送服务
 * </p>
 *
 * @author qingsheng.chen 2018/11/20 星期二 16:19
 */
public class MessageAsyncService {
    private static final Logger logger = LoggerFactory.getLogger(MessageAsyncService.class);
    private final MessageRemoteService messageRemoteService;

    public MessageAsyncService(MessageRemoteService messageRemoteService) {
        this.messageRemoteService = messageRemoteService;
    }

    /**
     * 发送站内消息
     *
     * @param messageSender 消息发送内容
     */
    @Async
    public void sendWebMessage(MessageSender messageSender) {
        messageRemoteService.sendWebMessage(messageSender.getTenantId(), messageSender);
    }

    /**
     * 发送邮件消息
     *
     * @param messageSender 消息内容
     */
    @Async
    public void sendEmail(MessageSender messageSender) {
        messageRemoteService.sendEmail(messageSender.getTenantId(), messageSender);
    }

    /**
     * 发送短信消息
     *
     * @param messageSender 消息内容
     */
    @Async
    public void sendSms(MessageSender messageSender) {
        messageRemoteService.sendSms(messageSender.getTenantId(), messageSender);
    }

    /**
     * 发送消息
     *
     * @param messageSender 发送消息内容
     */
    @Async
    public void sendMessage(MessageSender messageSender) {
        messageRemoteService.sendMessage(messageSender.getTenantId(), messageSender);
        logger.info("Send Message Async");
    }

    /**
     * 发送消息
     *
     * @param sender 发送消息内容
     */
    @Async
    public void sendAllMessage(Long tenantId, AllSender sender) {
        messageRemoteService.sendAllMessage(tenantId, sender);
        logger.info("Send Message Async");
    }

    /**
     * 发送微信公众号消息
     *
     * @param weChatSender 消息内容
     */
    @Async
    public void sendWeChatOfficial(WeChatSender weChatSender) {
        messageRemoteService.sendWeChatOfficial(weChatSender.getTenantId(), weChatSender);
    }

    /**
     * 发送微信应用消息
     *
     * @param weChatSender 消息内容
     */
    @Async
    public void sendWeChatEnterprise(WeChatSender weChatSender) {
        messageRemoteService.sendWeChatEnterprise(weChatSender.getTenantId(), weChatSender);
    }

    /**
     * 发送钉钉应用消息
     *
     * @param dingTalkSender 消息内容
     */
    @Async
    public void sendDingTalkMessage(DingTalkSender dingTalkSender) {
        messageRemoteService.sendDingTalk(dingTalkSender.getTenantId(), dingTalkSender);
    }

    /**
     * 发送WebHook消息
     *
     * @param webHookSender 消息内容
     */
    @Async
    public void sendWebHookMessage(WebHookSender webHookSender) {
        messageRemoteService.sendWebHookMessage(webHookSender.getTenantId(), webHookSender);
    }
}
