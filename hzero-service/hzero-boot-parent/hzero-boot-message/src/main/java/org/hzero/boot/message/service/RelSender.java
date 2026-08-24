package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.*;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 关联发送
 * </p>
 *
 * @author qingsheng.chen 2018/10/7 星期日 17:04
 */
public interface RelSender {
    /**
     * 发送消息
     *
     * @param messageCode  消息代码
     * @param receiverList 接收人列表
     * @param args         参数
     * @param attachments  附件
     */
    default void sendMessage(String messageCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, receiverList, args, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode       消息代码
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param attachments       附件
     */
    default void sendMessage(String messageCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, receiverGroupCode, args, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode  消息代码
     * @param receiverList 接收人列表
     * @param args         参数
     * @param typeCodeList 发送方式
     * @param attachments  附件
     */
    default void sendMessage(String messageCode, List<Receiver> receiverList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, receiverList, args, typeCodeList, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode       消息代码
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param typeCodeList      发送方式
     * @param attachments       附件
     */
    default void sendMessage(String messageCode, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, receiverGroupCode, args, typeCodeList, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode  消息代码
     * @param lang         语言
     * @param receiverList 接收人列表
     * @param args         参数
     * @param attachments  附件
     */
    default void sendMessage(String messageCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, lang, receiverList, args, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode       消息代码
     * @param lang              语言
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param attachments       附件
     */
    default void sendMessage(String messageCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, lang, receiverGroupCode, args, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode  消息代码
     * @param lang         语言
     * @param receiverList 接收人列表
     * @param args         参数
     * @param typeCodeList 发送方式
     * @param attachments  附件
     */
    default void sendMessage(String messageCode, String lang, List<Receiver> receiverList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, lang, receiverList, args, typeCodeList, attachments);
    }

    /**
     * 发送消息
     *
     * @param messageCode       消息代码
     * @param lang              语言
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param typeCodeList      发送方式
     * @param attachments       附件
     */
    default void sendMessage(String messageCode, String lang, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, messageCode, lang, receiverGroupCode, args, typeCodeList, attachments);
    }


    /**
     * 发送消息
     *
     * @param tenantId     租户ID
     * @param messageCode  消息代码
     * @param receiverList 接收人列表
     * @param args         参数
     * @param attachments  附件
     */
    void sendMessage(long tenantId, String messageCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId          租户ID
     * @param messageCode       消息代码
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param attachments       附件
     */
    void sendMessage(long tenantId, String messageCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId     租户ID
     * @param messageCode  消息代码
     * @param receiverList 接收人列表
     * @param args         参数
     * @param typeCodeList 发送方式
     * @param attachments  附件
     */
    void sendMessage(long tenantId, String messageCode, List<Receiver> receiverList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId          租户ID
     * @param messageCode       消息代码
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param typeCodeList      发送方式
     * @param attachments       附件
     */
    void sendMessage(long tenantId, String messageCode, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId     租户ID
     * @param messageCode  消息代码
     * @param lang         语言
     * @param receiverList 接收人列表
     * @param args         参数
     * @param attachments  附件
     */
    void sendMessage(long tenantId, String messageCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId          租户ID
     * @param messageCode       消息代码
     * @param lang              语言
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param attachments       附件
     */
    void sendMessage(long tenantId, String messageCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId     租户ID
     * @param messageCode  消息代码
     * @param lang         语言
     * @param receiverList 接收人列表
     * @param args         参数
     * @param typeCodeList 发送方式
     * @param attachments  附件
     */
    void sendMessage(long tenantId, String messageCode, String lang, List<Receiver> receiverList, Map<String, String> args, List<String> typeCodeList, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param tenantId          租户ID
     * @param messageCode       消息代码
     * @param lang              语言
     * @param receiverGroupCode 接收组编码
     * @param args              参数
     * @param typeCodeList      发送方式
     * @param attachments       附件
     */
    void sendMessage(long tenantId, String messageCode, String lang, String receiverGroupCode, Map<String, String> args, List<String> typeCodeList, Attachment... attachments);

    /**
     * 发送消息
     *
     * @param messageSender 发送消息参数
     */
    void sendMessage(MessageSender messageSender);

    /**
     * 发送消息(有返回值)
     *
     * @param messageSender 发送对象
     * @return 发送结果
     */
    List<Message> sendMessageWithReceipt(MessageSender messageSender);

    /**
     * 关联发送消息
     *
     * @param receiverGroupCode 接收组编码
     * @param messageSender     发送消息参数
     * @param weChatSender      微信消息发送
     */
    default void sendMessage(String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, receiverGroupCode, messageSender, weChatSender);
    }

    /**
     * 关联发送消息
     *
     * @param receiverGroupCode 接收组编码
     * @param messageSender     发送消息参数
     * @param weChatSender      微信消息发送
     * @param dingTalkSender    钉钉消息发送
     */
    default void sendMessage(String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender) {
        sendMessage(BaseConstants.DEFAULT_TENANT_ID, receiverGroupCode, messageSender, weChatSender, dingTalkSender);
    }

    /**
     * 关联发送消息
     *
     * @param tenantId          租户ID
     * @param receiverGroupCode 接收组编码
     * @param messageSender     发送消息参数
     * @param weChatSender      微信消息发送
     */
    void sendMessage(Long tenantId, String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender);

    /**
     * 关联发送消息
     *
     * @param messageSender 发送消息参数
     * @param weChatSender  微信消息发送
     */
    void sendMessage(MessageSender messageSender, WeChatSender weChatSender);

    /**
     * 关联发送消息
     *
     * @param tenantId          租户ID
     * @param receiverGroupCode 接收组编码
     * @param messageSender     发送消息参数
     * @param weChatSender      微信消息发送
     * @param dingTalkSender    钉钉消息发送
     */
    void sendMessage(Long tenantId, String receiverGroupCode, MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender);

    /**
     * 关联发送消息
     *
     * @param messageSender  发送消息参数
     * @param weChatSender   微信消息发送
     * @param dingTalkSender 钉钉消息发送
     */
    void sendMessage(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender);

    /**
     * 关联发送消息(有返回值)
     *
     * @param messageSender  发送对象
     * @param weChatSender   微信对象
     * @param dingTalkSender 钉钉对象
     * @return 发送结果
     */
    List<Message> sendMessageWithReceipt(MessageSender messageSender, WeChatSender weChatSender, DingTalkSender dingTalkSender);
}
