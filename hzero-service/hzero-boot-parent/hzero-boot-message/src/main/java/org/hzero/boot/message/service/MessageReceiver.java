package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 消息接收人接口
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 14:19
 */
public interface MessageReceiver {
    /**
     * 获取消息接收人
     *
     * @param receiverTypeCode 消息接收人编码
     * @param args             自定义参数
     * @return 消息接收人
     */
    default List<Receiver> receiver(String receiverTypeCode, Map<String, String> args) {
        return receiver(BaseConstants.DEFAULT_TENANT_ID, receiverTypeCode, args);
    }

    /**
     * 获取消息接收人
     *
     * @param tenantId         租户ID
     * @param receiverTypeCode 消息接收人编码
     * @param args             自定义参数
     * @return 消息接收人
     */
    List<Receiver> receiver(long tenantId, String receiverTypeCode, Map<String, String> args);

    /**
     * 获取消息接收人
     *
     * @param tenantId         租户ID
     * @param receiverTypeCode 消息接收人编码
     * @param objectArgs       自定义参数
     * @return 消息接收人
     */
    List<Receiver> receiverWithObjectArgs(long tenantId, String receiverTypeCode, Map<String, Object> objectArgs);

    /**
     * 获取第三方消息接收人
     *
     * @param tenantId         租户ID
     * @param messageType      消息类型
     * @param receiverTypeCode 消息接收配置编码
     * @param args             自定义参数
     * @return 消息接收人
     */
    List<Receiver> openReceiver(long tenantId, String messageType, String receiverTypeCode, Map<String, String> args);

}
