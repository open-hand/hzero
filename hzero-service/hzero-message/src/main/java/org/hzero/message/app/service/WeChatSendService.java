package org.hzero.message.app.service;

import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * 微信消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 13:36
 */
public interface WeChatSendService {

    /**
     * 发送公众号模板消息
     *
     * @param weChatSender 微信消息发送
     * @return 消息
     */
    Message sendOfficialMessage(WeChatSender weChatSender);

    /**
     * 发送公众号模板消息
     *
     * @param weChatSender 微信消息发送
     */
    void asyncSendOfficialMessage(WeChatSender weChatSender);

    /**
     * 重发公众号模板消息
     *
     * @param message 消息内容
     * @return 消息
     */
    Message resendOfficialMessage(UserMessageDTO message);

    /**
     * 发送消息
     *
     * @param weChatSender 微信消息发送
     * @return 消息
     */
    Message sendEnterpriseMessage(WeChatSender weChatSender);

    /**
     * 发送消息
     *
     * @param weChatSender 微信消息发送
     */
    void asyncSendEnterpriseMessage(WeChatSender weChatSender);

    /**
     * 重发消息
     *
     * @param message 消息内容
     * @return 消息
     */
    Message resendEnterpriseMessage(UserMessageDTO message);
}
