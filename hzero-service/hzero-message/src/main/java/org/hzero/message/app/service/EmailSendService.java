package org.hzero.message.app.service;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.message.api.dto.UserMessageDTO;
import org.hzero.message.domain.entity.Message;

/**
 * <p>
 * 消息发送接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/3 星期五 9:30
 */
public interface EmailSendService {
    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     * @return 消息发送
     * @see org.hzero.message.infra.constant.HmsgConstant
     */
    Message sendMessage(MessageSender messageSender);

    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     * @param tryTimes      重试次数
     * @return 消息发送
     */
    Message sendMessage(MessageSender messageSender, Integer tryTimes);

    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     * @param tryTimes      重试次数
     */
    void asyncSendMessage(MessageSender messageSender, Integer tryTimes);

    /**
     * 发送消息
     *
     * @param message 消息内容
     * @return 消息
     * @see org.hzero.message.infra.constant.HmsgConstant
     */
    Message resendMessage(UserMessageDTO message);
}
