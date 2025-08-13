package org.hzero.message.app.service;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * 语音消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/27 14:19
 */
public interface CallSendService {

    /**
     * 发送消息
     *
     * @param messageSender 消息
     * @return 消息
     */
    Message sendMessage(MessageSender messageSender);

    /**
     * 发送消息
     *
     * @param messageSender 消息
     */
    void asyncSendMessage(MessageSender messageSender);

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return 消息
     */
    Message resendMessage(UserMessageDTO message);
}
