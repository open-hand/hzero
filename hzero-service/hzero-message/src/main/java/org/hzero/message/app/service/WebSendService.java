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
public interface WebSendService {
    /**
     * 发送消息
     *
     * @param messageSender 消息发送
     * @return 消息
     * @see org.hzero.message.infra.constant.HmsgConstant
     */
    Message sendMessage(MessageSender messageSender);

    /**
     * 重新发送消息
     *
     * @param message 消息内容
     * @return 消息
     * @see org.hzero.message.infra.constant.HmsgConstant
     */
    Message resendMessage(UserMessageDTO message);

    /**
     * 发送消息
     *
     * @param messageType        消息类型
     * @param userId             用户ID
     * @param messageId          消息ID
     * @param fromTenantId       消息来源租户
     * @param targetUserTenantId 消息目标租户
     * @param subject            消息标题
     */
    void saveUserMessage(String messageType, Long userId, long messageId, long fromTenantId, long targetUserTenantId, String subject);
}
