package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 短信发送
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 19:51
 */
public interface SmsSender {

    /**
     * 发送短信
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    default Message sendSms(String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args) {
        return sendSms(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverGroupCode, args);
    }

    /**
     * 发送短信
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    default Message sendSms(String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args) {
        return sendSms(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverList, args);
    }


    /**
     * 发送短信
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    default Message sendSms(String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args) {
        return sendSms(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverGroupCode, args);
    }

    /**
     * 发送短信
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    default Message sendSms(String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args) {
        return sendSms(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverList, args);
    }

    /**
     * 发送短信
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args);

    /**
     * 发送短信
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    Message sendSms(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args);


    /**
     * 发送短信
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @return 发送结果
     */
    Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args);

    /**
     * 发送短信
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @return 发送结果
     */
    Message sendSms(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args);
}
