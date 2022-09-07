package org.hzero.message.app.service;

import org.hzero.boot.message.entity.WebHookSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * <p>
 * 消息发送接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 9:30
 */
public interface WebHookSendService {

    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     * @return 消息
     */
    Message sendWebHookMessage(WebHookSender messageSender);

    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     */
    void asyncSendMessage(WebHookSender messageSender);

    /**
     * 重新发送消息
     *
     * @param message 消息内容
     * @return 消息
     */
    Message resendWebHookMessage(UserMessageDTO message);
}
