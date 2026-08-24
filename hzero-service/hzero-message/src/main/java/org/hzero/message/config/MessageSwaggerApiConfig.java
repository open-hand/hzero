package org.hzero.message.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2018/7/30 星期一 14:26
 */
@Configuration
public class MessageSwaggerApiConfig {
    public static final String EMAIL_FILTER = "Email Filter";
    public static final String EMAIL_FILTER_SITE = "Email Filter(Site Level)";
    public static final String EMAIL_SERVER = "Email Server";
    public static final String EMAIL_SERVER_SITE = "Email Server(Site Level)";
    public static final String MESSAGE = "Message";
    public static final String MESSAGE_SITE = "Message(Site Level)";
    public static final String MESSAGE_EVENT = "Message event";
    public static final String MESSAGE_EVENT_SITE = "Message event(Site Level)";
    public static final String MESSAGE_GENERATOR = "Message Generator";
    public static final String MESSAGE_GENERATOR_SITE = "Message Generator(Site Level)";
    public static final String MESSAGE_RECEIVER = "Message Receiver";
    public static final String MESSAGE_RECEIVER_SITE = "Message Receiver(Site Level)";
    public static final String MESSAGE_TEMPLATE = "Message Template";
    public static final String MESSAGE_TEMPLATE_SITE = "Message Template(Site Level)";
    public static final String RECEIVE_CONFIG = "Receive Config";
    public static final String RECEIVE_CONFIG_SITE = "Receive Config(Site Level)";
    public static final String RECEIVER_TYPE = "Receiver Type";
    public static final String RECEIVER_TYPE_SITE = "Receiver Type(Site Level)";
    public static final String REL_MESSAGE = "Relation Send Message";
    public static final String REL_MESSAGE_SITE = "Relation Send Message(Site Level)";
    public static final String EMAIL_MESSAGE = "Send Email Message";
    public static final String EMAIL_MESSAGE_SITE = "Send Email Message(Site Level)";
    public static final String SMS_MESSAGE = "Send SMS Message";
    public static final String SMS_MESSAGE_SITE = "Send SMS Message(Site Level)";
    public static final String WEB_MESSAGE = "Send Web Message";
    public static final String WEB_MESSAGE_SITE = "Send Web Message(Site Level)";
    public static final String SMS_SERVER = "SMS Server";
    public static final String SMS_SERVER_SITE = "SMS Server(Site Level)";
    public static final String TEMPLATE_ARG = "Template Arg";
    public static final String TEMPLATE_ARG_SITE = "Template Arg(Site Level)";
    public static final String TEMPLATE_SERVER = "Template Maintain";
    public static final String TEMPLATE_SERVER_SITE = "Template Maintain(Site Level)";
    public static final String USER_MESSAGE = "User Message";
    public static final String USER_RECEIVE_CONFIG = "User Receive Config";
    public static final String NOTICE = "Notice";
    public static final String NOTICE_V2 = "Notice V2";
    public static final String NOTICE_SITE = "Notice(Site Level)";
    public static final String NOTICE_PUBLISH = "Notice_Publish";
    public static final String NOTICE_PUBLISH_SITE = "Notice_Publish(Site Level)";
    public static final String NOTICE_RECEIVER = "Notice_Receiver";
    public static final String NOTICE_RECEIVER_SITE = "Notice_Receiver(Site Level)";
    public static final String WE_CHAT_OFFICIAL = "WeChat Official";
    public static final String WE_CHAT_OFFICIAL_SITE = "WeChat Official(Site Level)";
    public static final String WE_CHAT_MESSAGE = "Send WeChat Message";
    public static final String WE_CHAT_MESSAGE_SITE = "Send WeChat Message(Site Level)";
    public static final String WE_CHAT_ENTERPRISE = "WeChat EnterPrise";
    public static final String WE_CHAT_ENTERPRISE_SITE = "WeChat EnterPrise(Site Level)";
    public static final String DING_TALK_SERVER = "DingTalk Server";
    public static final String DING_TALK_SERVER_SITE = "DingTalk Server(Site Level)";
    public static final String DING_TALK_MESSAGE = "Send DingTalk Message";
    public static final String DING_TALK_MESSAGE_SITE = "Send DingTalk Message(Site Level)";
    public static final String CALL_SERVER = "Call Server";
    public static final String CALL_SERVER_SITE = "Call Server(Site Level)";
    public static final String CALL_MESSAGE = "SMS Server";
    public static final String CALL_MESSAGE_SITE = "SMS Server(Site Level)";
    public static final String WEBHOOK_MESSAGE = "WebHook Server";
    public static final String WEBHOOK_MESSAGE_SITE = "WebHook Server(Site Level)";
    public static final String SEND_WEBHOOK = "Send WebHook Message";
    public static final String SEND_WEBHOOK_SITE = "Send WebHook Message(Site Level)";
    public static final String TEMPLATE_SERVER_WH = "WebHook Template Server Config";
    public static final String TEMPLATE_SERVER_WH_SITE = "WebHook Template Server Config(Site Level)";

    @Autowired
    public MessageSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(NOTICE, "公告"),
                new Tag(NOTICE_V2, "公告(V2)"),
                new Tag(NOTICE_SITE, "公告(平台级)"),
                new Tag(NOTICE_PUBLISH, "公告发布记录"),
                new Tag(NOTICE_RECEIVER, "公告接收记录"),
                new Tag(EMAIL_FILTER, "邮箱账户黑白名单"),
                new Tag(EMAIL_FILTER_SITE, "邮箱账户黑白名单(平台级)"),
                new Tag(EMAIL_SERVER, "邮箱服务管理"),
                new Tag(EMAIL_SERVER_SITE, "邮箱服务管理(平台级)"),
                new Tag(MESSAGE, "消息信息管理"),
                new Tag(MESSAGE_SITE, "消息信息管理(平台级)"),
                new Tag(MESSAGE_EVENT, "消息事件"),
                new Tag(MESSAGE_EVENT_SITE, "消息事件(平台级)"),
                new Tag(MESSAGE_GENERATOR, "消息内容生成"),
                new Tag(MESSAGE_GENERATOR_SITE, "消息内容生成(平台级)"),
                new Tag(MESSAGE_RECEIVER, "消息接收人获取"),
                new Tag(MESSAGE_RECEIVER_SITE, "消息接收人获取(平台级)"),
                new Tag(MESSAGE_TEMPLATE, "消息模板管理"),
                new Tag(MESSAGE_TEMPLATE_SITE, "消息模板管理(平台级)"),
                new Tag(RECEIVE_CONFIG, "接收配置"),
                new Tag(RECEIVE_CONFIG_SITE, "接收配置(平台级)"),
                new Tag(RECEIVER_TYPE, "接收者类型"),
                new Tag(RECEIVER_TYPE_SITE, "接收者类型(平台级)"),
                new Tag(REL_MESSAGE, "关联发送消息"),
                new Tag(REL_MESSAGE_SITE, "关联发送消息(平台级)"),
                new Tag(EMAIL_MESSAGE, "邮箱消息发送"),
                new Tag(EMAIL_MESSAGE_SITE, "邮箱消息发送(平台级)"),
                new Tag(SMS_MESSAGE, "短信消息发送"),
                new Tag(SMS_MESSAGE_SITE, "短信消息发送(平台级)"),
                new Tag(WEB_MESSAGE, "站内消息发送"),
                new Tag(WEB_MESSAGE_SITE, "站内消息发送(平台级)"),
                new Tag(SMS_SERVER, "短信服务管理"),
                new Tag(SMS_SERVER_SITE, "短信服务管理(平台级)"),
                new Tag(TEMPLATE_ARG, "消息模板参数管理"),
                new Tag(TEMPLATE_ARG_SITE, "消息模板参数管理(平台级)"),
                new Tag(TEMPLATE_SERVER, "邮箱账户与模板关系维护"),
                new Tag(TEMPLATE_SERVER_SITE, "邮箱账户与模板关系维护(平台级)"),
                new Tag(USER_MESSAGE, "用户消息"),
                new Tag(USER_RECEIVE_CONFIG, "用户接收配置"),
                new Tag(NOTICE_PUBLISH, "公告发布记录"),
                new Tag(NOTICE_PUBLISH_SITE, "公告发布记录(平台级)"),
                new Tag(NOTICE_RECEIVER, "公告接收记录"),
                new Tag(NOTICE_RECEIVER_SITE, "公告接收记录(平台级)"),
                new Tag(WE_CHAT_OFFICIAL, "微信公众号配置"),
                new Tag(WE_CHAT_OFFICIAL_SITE, "微信公众号配置(平台级)"),
                new Tag(WE_CHAT_MESSAGE, "微信消息发送"),
                new Tag(WE_CHAT_MESSAGE_SITE, "微信消息发送(平台级)"),
                new Tag(WE_CHAT_ENTERPRISE, "企业微信配置维护"),
                new Tag(WE_CHAT_ENTERPRISE_SITE, "企业微信配置维护(平台级)"),
                new Tag(DING_TALK_SERVER, "钉钉配置"),
                new Tag(DING_TALK_SERVER_SITE, "钉钉配置(平台级)"),
                new Tag(DING_TALK_MESSAGE, "钉钉消息发送"),
                new Tag(DING_TALK_MESSAGE_SITE, "钉钉消息发送(平台级)"),
                new Tag(CALL_SERVER, "语音服务配置"),
                new Tag(CALL_SERVER_SITE, "语音服务配置(平台级)"),
                new Tag(CALL_MESSAGE, "语音消息"),
                new Tag(CALL_MESSAGE_SITE, "语音消息(平台级)"),
                new Tag(WEBHOOK_MESSAGE, "WEBHOOK 消息"),
                new Tag(WEBHOOK_MESSAGE_SITE, "WEBHOOK 消息(平台级)"),
                new Tag(TEMPLATE_SERVER_WH, "WEBHOOK 消息发送配置"),
                new Tag(TEMPLATE_SERVER_WH_SITE, "WEBHOOK 消息发送配置(平台级)")
        );
    }
}
