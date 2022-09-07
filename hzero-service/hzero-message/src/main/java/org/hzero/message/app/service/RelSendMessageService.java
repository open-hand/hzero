package org.hzero.message.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;

/**
 * 不区分短信邮件 发送消息
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/18 16:09
 */
public interface RelSendMessageService {

    /**
     * 发送消息 （备注：WebHook消息支持通用结构发送）
     *
     * @param messageSender 消息发送
     * @return 发送结果
     */
    Map<String, Integer> relSendMessage(MessageSender messageSender);

    /**
     * 发送消息 (返回发送结果，若服务端开启了异步发送则不返回)
     *
     * @param messageSender 消息发送
     * @return 发送结果
     */
    List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(MessageSender messageSender);

    /**
     * 发送微信消息
     *
     * @param weChatSender 消息发送
     * @return 发送结果
     */
    Map<String, Integer> relSendMessage(WeChatSender weChatSender);

    /**
     * 发送微信消息
     *
     * @param weChatSender 消息发送
     * @return 发送结果
     */
    List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(WeChatSender weChatSender);

    /**
     * 发送钉钉消息
     *
     * @param dingTalkSender 消息发送
     * @return 发送结果
     */
    Map<String, Integer> relSendMessage(DingTalkSender dingTalkSender);

    /**
     * 发送钉钉消息
     *
     * @param dingTalkSender 消息发送
     * @return 发送结果
     */
    List<org.hzero.message.domain.entity.Message> relSendMessageReceipt(DingTalkSender dingTalkSender);
}
