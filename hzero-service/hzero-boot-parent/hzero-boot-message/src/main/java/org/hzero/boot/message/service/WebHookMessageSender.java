package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;

/**
 * WebHook 消息发送API接口
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/08 10:53
 */
public interface WebHookMessageSender {

    /**
     * 发送WebHook应用消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverTypeCode    接收组编码(仅钉钉webhook类型时需传递，且接收组下仅需维护手机号即可)
     * @param lang                语言
     * @param args                消息参数
     * @return 调用结果
     */
    default Message sendWebHookMessage(String messageTemplateCode, String serverCode, String receiverTypeCode, String lang, Map<String, String> args) {
        return sendWebHookMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, serverCode, receiverTypeCode, lang, args);
    }

    /**
     * 发送WebHook应用消息
     *
     * @param tenantId            租户Id
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverTypeCode    接收组编码(仅钉钉webhook类型时需传递，且接收组下仅需维护手机号即可)
     * @param lang                语言
     * @param args                消息参数
     * @return 调用结果
     */
    Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, String receiverTypeCode, String lang, Map<String, String> args);

    /**
     * 发送WebHook应用消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverTypeCode    接收组编码(仅钉钉webhook类型时需传递，且接收组下仅需维护手机号即可)
     * @param args                消息参数
     * @return 调用结果
     */
    default Message sendWebHookMessage(String messageTemplateCode, String serverCode, String receiverTypeCode, Map<String, String> args) {
        return sendWebHookMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, serverCode, receiverTypeCode, args);
    }

    /**
     * 发送WebHook应用消息
     *
     * @param tenantId            租户Id
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverTypeCode    接收组编码(仅钉钉webhook类型时需传递，且接收组下仅需维护手机号即可)
     * @param args                消息参数
     * @return 调用结果
     */
    Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, String receiverTypeCode, Map<String, String> args);

    /**
     * 发送WebHook应用消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverAddressList 接收人列表(仅钉钉webhook类型时需传递，且接收人仅需传递手机号信息即可)
     * @param lang                语言
     * @param args                消息参数
     * @return 调用结果
     */
    default Message sendWebHookMessage(String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, String lang, Map<String, String> args) {
        return sendWebHookMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, serverCode, receiverAddressList, lang, args);
    }

    /**
     * 发送WebHook应用消息
     *
     * @param tenantId            租户Id
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverAddressList 接收人列表(仅钉钉webhook类型时需传递，且接收人仅需传递手机号信息即可)
     * @param lang                语言
     * @param args                消息参数
     * @return 调用结果
     */
    Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, String lang, Map<String, String> args);


    /**
     * 发送WebHook应用消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverAddressList 接收人列表(仅钉钉webhook类型时需传递，且接收人仅需传递手机号信息即可)
     * @param args                消息参数
     * @return 调用结果
     */
    default Message sendWebHookMessage(String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, Map<String, String> args) {
        return sendWebHookMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, serverCode, receiverAddressList, args);
    }

    /**
     * 发送WebHook应用消息
     *
     * @param tenantId            租户Id
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param receiverAddressList 接收人列表(仅钉钉webhook类型时需传递，且接收人仅需传递手机号信息即可)
     * @param args                消息参数
     * @return 调用结果
     */
    Message sendWebHookMessage(Long tenantId, String messageTemplateCode, String serverCode, List<Receiver> receiverAddressList, Map<String, String> args);

    /**
     * 发送WebHook应用消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param serverCode          WebHook配置编码
     * @param args                消息参数
     * @return 调用结果
     */
    default Message sendWebHookMessage(String messageTemplateCode, String serverCode, Map<String, String> args) {
        return sendWebHookMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, serverCode, args);
    }

    /**
     * 发送WebHook应用消息
     *
     * @param tenantId    租户Id
     * @param messageCode 消息模板编码
     * @param serverCode  WebHook配置编码
     * @param args        消息参数
     * @return 调用结果
     */
    Message sendWebHookMessage(Long tenantId, String messageCode, String serverCode, Map<String, String> args);
}
