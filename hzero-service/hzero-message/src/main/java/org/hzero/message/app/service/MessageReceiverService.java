package org.hzero.message.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;

/**
 * 消息接受方应用服务
 *
 * @author xianzhi.chen@hand-china.com 2018-07-27 11:30:58
 */
public interface MessageReceiverService {
    /**
     * 获取消息接收人
     *
     * @param tenantId 租户ID
     * @param typeCode 接收人类型编码
     * @param args     自定义参数
     * @return 消息接收人
     */
    List<Receiver> queryReceiver(long tenantId, String typeCode, Map<String, String> args);

    /**
     * 获取消息接收人
     *
     * @param messageSender 消息发送
     * @return 消息发送
     */
    MessageSender queryReceiver(MessageSender messageSender);

    /**
     * 获取三方消息接收人
     *
     * @param tenantId          租户ID
     * @param typeCode          接收人类型编码
     * @param thirdPlatformType 三方平台类型
     * @param args              自定义参数
     * @return 消息接收人
     */
    List<Receiver> queryOpenReceiver(long tenantId, String typeCode, String thirdPlatformType, Map<String, String> args);
}
