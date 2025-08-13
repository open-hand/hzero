package org.hzero.message.app.service;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * <p>
 * 短信发送接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 13:46
 */
public interface SmsSendService {

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
