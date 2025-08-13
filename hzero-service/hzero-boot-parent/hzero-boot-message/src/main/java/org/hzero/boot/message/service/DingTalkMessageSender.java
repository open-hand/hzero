package org.hzero.boot.message.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.message.entity.Message;
import org.hzero.core.base.BaseConstants;

/**
 * 钉钉消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2019/11/18 15:02
 */
public interface DingTalkMessageSender {

    /**
     * 发送钉钉应用消息
     *
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param args                消息参数
     * @param agentId             企业应用ID
     * @param userIdList          接收人用户列表
     * @return 发送结果
     */
    default Message sendDingTalkMessage(String serverCode, String messageTemplateCode, Map<String, String> args, Long agentId, List<String> userIdList) {
        return sendDingTalkMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, args, agentId, userIdList);
    }

    /**
     * 发送钉钉应用消息
     *
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param args                消息参数
     * @param agentId             企业应用ID
     * @param userIdList          接收人用户列表
     * @param deptIdList          部门列表
     * @param toAllUser           发送至所有人
     * @return 发送结果
     */
    default Message sendDingTalkMessage(String serverCode, String messageTemplateCode, String lang, Map<String, String> args, Long agentId, List<String> userIdList, List<String> deptIdList, boolean toAllUser) {
        return sendDingTalkMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, args, agentId, userIdList, deptIdList, toAllUser);
    }

    /**
     * 发送钉钉应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param args                消息参数
     * @param agentId             企业应用ID
     * @param userIdList          接收人用户列表
     * @return 发送结果
     */
    Message sendDingTalkMessage(Long tenantId, String serverCode, String messageTemplateCode, Map<String, String> args, Long agentId, List<String> userIdList);

    /**
     * 发送钉钉应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言（为空取默认语言）
     * @param args                消息参数
     * @param agentId             企业应用ID
     * @param userIdList          接收人用户列表
     * @param deptIdList          部门列表
     * @param toAllUser           发送至所有人
     * @return 发送结果
     */
    Message sendDingTalkMessage(Long tenantId, String serverCode, String messageTemplateCode, String lang, Map<String, String> args, Long agentId, List<String> userIdList, List<String> deptIdList, boolean toAllUser);

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    default Message sendDingTalkMessage(String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendDingTalkMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, receiverTypeCode, agentId, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    default Message sendDingTalkMessage(String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args) {
        return sendDingTalkMessage(BaseConstants.DEFAULT_TENANT_ID, serverCode, messageTemplateCode, lang, receiverTypeCode, agentId, args);
    }

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    Message sendDingTalkMessage(long tenantId, String serverCode, String messageTemplateCode, String receiverTypeCode, Long agentId, Map<String, String> args);

    /**
     * 发送企业微信应用消息
     *
     * @param tenantId            租户ID（平台默认0）
     * @param serverCode          钉钉配置编码
     * @param messageTemplateCode 消息模板编码
     * @param lang                语言
     * @param receiverTypeCode    消息接收配置编码
     * @param agentId             企业应用ID
     * @param args                参数
     * @return 发送结果
     */
    Message sendDingTalkMessage(long tenantId, String serverCode, String messageTemplateCode, String lang, String receiverTypeCode, Long agentId, Map<String, String> args);
}
