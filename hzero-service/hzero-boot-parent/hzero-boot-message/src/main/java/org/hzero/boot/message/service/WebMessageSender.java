package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 站内消息发送
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 19:51
 */
public interface WebMessageSender {
    /**
     * 发送站内消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWebMessage(String messageTemplateCode, String receiverGroupCode, Map<String, String> args) {
        return sendWebMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, receiverGroupCode, args);
    }

    /**
     * 发送站内消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWebMessage(String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args) {
        return sendWebMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, receiverList, args);
    }


    /**
     * 发送站内消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWebMessage(String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args) {
        return sendWebMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, lang, receiverGroupCode, args);
    }

    /**
     * 发送站内消息
     *
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWebMessage(String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args) {
        return sendWebMessage(BaseConstants.DEFAULT_TENANT_ID, messageTemplateCode, lang, receiverList, args);
    }

    /**
     * 发送站内消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    Message sendWebMessage(long tenantId, String messageTemplateCode, String receiverGroupCode, Map<String, String> args);

    /**
     * 发送站内消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    Message sendWebMessage(long tenantId, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args);


    /**
     * 发送站内消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    Message sendWebMessage(long tenantId, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args);

    /**
     * 发送站内消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    Message sendWebMessage(long tenantId, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args);
}
