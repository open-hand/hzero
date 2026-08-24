package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.Message;

/**
 * 后端消息应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-08 15:04:28
 */
public interface ResponseMessageService {

    /**
     * 创建后端提示消息
     *
     * @param message 消息对象
     * @return Message
     */
    Message createMessage(Message message);

    /**
     * 修改后端消息信息
     *
     * @param message 消息实体
     * @return Message
     */
    Message updateMessage(Message message);

    /**
     * 删除后端消息信息
     *
     * @param message 消息实体
     */
    void deleteMessage(Message message);
}
