package org.hzero.message.app.service;

import org.hzero.boot.message.entity.DingTalkSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * 钉钉消息发送
 *
 * @author zifeng.ding@hand-china.com 2019/11/14 11:30
 */
public interface DingTalkSendService {

    /**
     * 发送钉钉模板消息
     *
     * @param dingTalkSender 钉钉消息发送对象
     * @return 消息
     */
    Message sendMessage(DingTalkSender dingTalkSender);

    /**
     * 发送钉钉模板消息
     *
     * @param dingTalkSender 钉钉消息发送对象
     */
    void asyncSendMessage(DingTalkSender dingTalkSender);

    /**
     * 重发消息
     *
     * @param message 发送消息
     * @return 返回内容
     */
    Message resendMessage(UserMessageDTO message);
}
