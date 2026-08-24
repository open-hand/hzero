package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.boot.message.entity.Miniprogram;
import org.hzero.boot.message.entity.WeChatFont;
import org.hzero.core.base.BaseConstants;

/**
 * 微信消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 19:45
 */
public interface WeChatMessageSender {

    /**
     * 发送微信公众号消息
     *
     * @param serverCode          服务编码
     * @param messageTemplateCode 消息模板编码
     * @param userList            接收人列表
     * @param data                参数
     * @return 发送结果
     */
    default Message sendWeChatOfficialMessage(String serverCode, String messageTemplateCode, List<String> userList, Map<String, WeChatFont> data) {
        return sendWeChatOfficialMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, userList, data);
    }

    /**
     * 发送微信公众号消息
     *
     * @param serverCode          公众号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param userList            接收人列表
     * @param data                参数
     * @param url                 跳转地址
     * @param miniprogram         小程序参数
     * @return 发送结果
     */
    default Message sendWeChatOfficialMessage(String serverCode, String messageTemplateCode, String lang, List<String> userList, Map<String, WeChatFont> data, String url, Miniprogram miniprogram) {
        return sendWeChatOfficialMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, userList, data, url, miniprogram);
    }

    /**
     * 发送微信公众号消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          公众号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param userList            接收人列表
     * @param data                参数
     * @return 发送结果
     */
    Message sendWeChatOfficialMessage(long tenantId, String serverCode, String messageTemplateCode, List<String> userList, Map<String, WeChatFont> data);

    /**
     * 发送微信公众号消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          公众号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param userList            接收人列表
     * @param data                参数
     * @param url                 跳转地址
     * @param miniprogram         小程序参数
     * @return 发送结果
     */
    Message sendWeChatOfficialMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, List<String> userList, Map<String, WeChatFont> data, String url, Miniprogram miniprogram);

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param agentId             企业应用ID
     * @param userList            接收人列表
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWeChatEnterpriseMessage(String serverCode, String messageTemplateCode, Long agentId, List<String> userList, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, agentId, userList, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param agentId             企业应用ID
     * @param userList            接收人列表
     * @param partyList           企业微信部门ID列表
     * @param tagList             本企业的标签ID列表，最多支持100个。
     * @param safe                企业微信表示是否是保密消息，0表示否，1表示是，默认0
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWeChatEnterpriseMessage(String serverCode, String messageTemplateCode, String lang, Long agentId, List<String> userList, List<String> partyList, List<String> tagList, Integer safe, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, agentId, userList, partyList, tagList, safe, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param agentId             企业应用ID
     * @param userList            接收人列表
     * @param args                参数
     * @return 发送结果
     */
    Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, Long agentId, List<String> userList, Map<String, String> args);

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param agentId             企业应用ID
     * @param userList            接收人列表
     * @param partyList           企业微信部门ID列表
     * @param tagList             本企业的标签ID列表，最多支持100个。
     * @param safe                企业微信表示是否是保密消息，0表示否，1表示是，默认0
     * @param args                参数
     * @return 发送结果
     */
    Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, Long agentId, List<String> userList, List<String> partyList, List<String> tagList, Integer safe, Map<String, String> args);

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWeChatEnterpriseMessage(String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverTypeCode, agentId, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    default Message sendWeChatEnterpriseMessage(String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendWeChatEnterpriseMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverTypeCode, agentId, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args);

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          企业微信号配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    Message sendWeChatEnterpriseMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args);
}
