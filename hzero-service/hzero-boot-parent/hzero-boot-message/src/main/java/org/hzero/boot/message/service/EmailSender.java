package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Attachment;
import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.base.BaseConstants;

/**
 * <p>
 * 邮件发送
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 19:51
 */
public interface EmailSender {

    /**
     * 发送邮件
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    default Message sendEmail(String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        return sendEmail(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverGroupCode, args, attachments);
    }

    /**
     * 发送邮件
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    default Message sendEmail(String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        return sendEmail(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverList, args, attachments);
    }


    /**
     * 发送邮件
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    default Message sendEmail(String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments) {
        return sendEmail(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverGroupCode, args, attachments);
    }

    /**
     * 发送邮件
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    default Message sendEmail(String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments) {
        return sendEmail(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverList, args, attachments);
    }

    /**
     * 发送邮件
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args, Attachment... attachments);

    /**
     * 发送邮件(指定抄送，密送)
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param ccList              抄送
     * @param bccList             密送
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String receiverGroupCode, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments);

    /**
     * 发送邮件
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments);

    /**
     * 发送邮件(指定抄送，密送)
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverList        接收人列表
     * @param args                参数
     * @param ccList              抄送
     * @param bccList             密送
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, List<Receiver> receiverList, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments);


    /**
     * 发送邮件
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args, Attachment... attachments);

    /**
     * 发送邮件(指定抄送，密送)
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverGroupCode   接收人组编码
     * @param args                参数
     * @param ccList              抄送
     * @param bccList             密送
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverGroupCode, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments);

    /**
     * 发送邮件
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args, Attachment... attachments);

    /**
     * 发送邮件(指定抄送，密送)
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param receiverList        接收人列表
     * @param args                参数
     * @param ccList              抄送
     * @param bccList             密送
     * @param attachments         附件
     * @return 发送结果
     */
    Message sendEmail(long tenantId, String serverCode, String messageTemplateCode, String lang, List<Receiver> receiverList, Map<String, String> args, List<String> ccList, List<String> bccList, Attachment... attachments);

    /**
     * 不使用消息模板，发送邮件
     *
     * @param tenantId     租户
     * @param serverCode   邮箱账户编码
     * @param subject      标题
     * @param content      内容
     * @param receiverList 接收人
     * @param ccList       抄送地址
     * @param bccList      密送地址
     * @param attachments  附件
     * @return 发送结果
     */
    Message sendCustomEmail(Long tenantId, String serverCode, String subject, String content, List<Receiver> receiverList, List<String> ccList, List<String> bccList, Attachment... attachments);
}
