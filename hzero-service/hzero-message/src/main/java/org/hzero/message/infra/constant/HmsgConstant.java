package org.hzero.message.infra.constant;

/**
 * <p>
 * 消息服务常量类
 * </p>
 *
 * @author qingsheng.chen 2018/8/1 星期三 19:16
 */
public class HmsgConstant {
    private HmsgConstant() {
    }

    /**
     * 服务名称
     */
    public static final String APP_CODE = "hmsg";

    public static final String DEFAULT_IDD = "+86";

    public static final String IDD_PREFIX = "+";

    public static final String ALIYUN_PREFIX = "00";
    /**
     * 站内消息缓存KEY + tenantId + userId
     */
    public static final String USER_MESSAGE_KEY = APP_CODE + ":message:web:";
    /**
     * 站内消息索引
     */
    public static final String USER_MESSAGE_INDEX = ":index";

    /**
     * 站内消息索引 + userId
     */
    public static final String USER_MESSAGE_ZSET = APP_CODE + ":message:web:index:";

    /**
     * 消息模板内容匹配占位符
     */
    public static final String TEMPLATE_CONTENT_REGEX = "\\$\\{.*?\\}";
    public static final String TEMPLATE_SECRET_CONTENT_REGEX = "\\$\\{\\{.*?}}";

    /**
     * 消息类型
     */
    public static final class MessageType {
        private MessageType() {
        }

        /**
         * 消息类型值集编码
         */
        public static final String LOV = "HMSG.MESSAGE_TYPE";
        /**
         * 站内消息
         */
        public static final String WEB = "WEB";
        /**
         * 邮件
         */
        public static final String EMAIL = "EMAIL";
        /**
         * 短信
         */
        public static final String SMS = "SMS";
        /**
         * 语音
         */
        public static final String CALL = "CALL";
        /**
         * 企业微信
         */
        public static final String WC_E = "WC_E";
        /**
         * 微信公众号
         */
        public static final String WC_O = "WC_O";
        /**
         * 钉钉
         */
        public static final String DT = "DT";
        /**
         * WebHook
         */
        public static final String WEB_HOOK = "WEB_HOOK";
    }

    /**
     * 接收组类型模式
     */
    public static final class ReceiverTypeMode {
        private ReceiverTypeMode() {
        }

        /**
         * 接收组类型模式值集编码
         */
        public static final String LOV = "HMSG.RECEIVER.TYPE_MODE";
        /**
         * URL
         */
        public static final String URL = "URL";
        /**
         * 用户组
         */
        public static final String USER_GROUP = "USER_GROUP";
        /**
         * 组织
         */
        public static final String UNIT = "UNIT";
        /**
         * 外部用户
         */
        public static final String EXT_USER = "EXT_USER";
        /**
         * 用户
         */
        public static final String USER = "USER";
    }

    /**
     * 用户消息类型
     */
    public static final class UserMessageType {
        private UserMessageType() {
        }

        /**
         * 消息类型值集编码
         */
        public static final String LOV = "HMSG.USER.MESSAGE_TYPE";
        /**
         * 站内消息
         */
        public static final String MSG = "MSG";
        /**
         * 系统通知
         */
        public static final String NOTICE = "NOTICE";

        /**
         * 系统公告
         */
        public static final String ANNOUNCE = "ANNOUNCE";

    }

    /**
     * 接收者类型编码
     */
    public static final class ReceiverRecordTypeCode {
        private ReceiverRecordTypeCode() {
        }

        /**
         * 接收者类型集编码
         */
        public static final String LOV = "HMSG.NOTICE.RECEIVER_RECORD_TYPE";
        /**
         * 租户
         */
        public static final String TENANT = "TENANT";

        /**
         * 组织部门
         */
        public static final String UNIT = "UNIT";

        /**
         * 用户组
         */
        public static final String USER_GROUP = "USER_GROUP";

        /**
         * 用户
         */
        public static final String USER = "USER";

        /**
         * 全局
         */
        public static final String ALL = "ALL";
        /**
         * 角色
         */
        public static final String ROLE = "ROLE";
    }

    /**
     * 公告接收类型编码
     */
    public static final class NoticeReceiveTypeCode {
        private NoticeReceiveTypeCode() {
        }

        /**
         * 消息类型值集编码
         */
        public static final String LOV = "HMSG.NOTICE.RECERVER_TYPE";
        /**
         * 系统通知
         */
        public static final String SYS_NOTIFY = "NOTIFY";
        /**
         * 系统公告
         */
        public static final String ANNOUNCE = "ANNOUNCE";
    }

    /**
     * 发布状态
     */
    public static final class PublishedStatus {
        private PublishedStatus() {
        }

        /**
         * 通过
         */
        public static final String PUBLISHED = "PUBLISHED";
        /**
         * 失败
         */
        public static final String DRAFT = "DRAFT";
    }

    /**
     * 事务状态
     */
    public static final class TransactionStatus {
        private TransactionStatus() {
        }

        /**
         * 就绪
         */
        public static final String P = "P";
        /**
         * 通过
         */
        public static final String S = "S";
        /**
         * 失败
         */
        public static final String F = "F";
    }

    public static final class SmsServerType {
        private SmsServerType() {
        }

        /**
         * 阿里云
         */
        public static final String ALIYUN = "ALIYUN";
        /**
         * 腾讯云
         */
        public static final String QCLOUD = "QCLOUD";
        /**
         * 百度云
         */
        public static final String BAIDU = "BAIDU";
    }


    public static final class ErrorCode {

        private ErrorCode() {
        }

        /**
         * 模板服务关联：租户不匹配
         */
        public static final String TENANT_NO_MATCH = "hmsg.error.tenant_no_match";
        /**
         * 模板服务关联：重复模板关联
         */
        public static final String REPEAT_TEMPLATE_ASSOCIATION = "hmsg.repeat.template_association";
        /**
         * 模板服务关联：重复服务关联
         */
        public static final String REPEAT_SERVER_ASSOCIATION = "hmsg.repeat.server_association";
        /**
         * 接收者类型不存在
         */
        public static final String RECEIVER_TYPE_NOT_EXIST = "hmsg.error.receiver_type_not_exist";
        /**
         * 消息模板不存在
         */
        public static final String TEMPLATE_SERVER_NOT_EXIST = "hmsg.error.template_server_not_exist";
        /**
         * 消息模板重复
         */
        public static final String TEMPLATE_REPEAT = "hmsg.error.template_repeat";

        /**
         * 消息事件不可用
         */
        public static final String MESSAGE_EVENT_NOT_AVAILABLE = "hmsg.error.message_event_not_available";

        /**
         * 接收配置编码重复
         */
        public static final String RECEIVE_CODE_REPEAT = "hmsg.error.receive_code_repeat";
        public static final String RECEIVE_CONFIG_NO_MORE = "hmsg.error.receive_config_no_more";

        /**
         * 无法获取用户信息
         */
        public static final String USER_DETAIL_NOT_FOUND = "hmsg.error.user_detail_not_found";

        /**
         * 删除失败
         */
        public static final String DELETE_FAILED = "hmsg.error.delete_failed";
        public static final String DELETE_CONFIG_FAILED = "hmsg.error.delete_config";

        /**
         * 接收类型错误
         */
        public static final String RECEIVE_TYPE_ERROR = "hmsg.error.receive_type_error";

        /**
         * 获取父节点失败
         */
        public static final String INVALID_PARENT_NODE = "hmsg.error.invalid_parent_node";

        /**
         * 接收类型错误
         */
        public static final String RECEIVE_TYPE_MORE = "hmsg.error.receive_type_more";

        /**
         * 接受类型过滤后为空
         */
        public static final String RECEIVE_TYPE_NULL = "hmsg.error.receive_type_null";

        /**
         * 百度短信错误
         */
        public static final String BAIDU_SMS = "hmsg.error.baidu_sms";
        /**
         * 邮箱接收人为空
         */
        public static final String NULL_EMAIL_LIST = "hmsg.error.null_emailList";
        public static final String SEND_SOCKET = "hmsg.error.send_socket";
        public static final String GET_TEMPLATE = "hmsg.error.get_template";
        public static final String GET_TOKEN = "hmsg.error.get_token";
        /**
         * 模板未关联账户
         */
        public static final String TEMPLATE_NO_SERVER = "hmsg.error.template.no_server";
        public static final String NO_RECEIVER = "hmsg.error.no_receiver";

        /**
         * webhook 地址重复
         */
        public static final String WEBHOOK_ADDRESS_REPEAT = "hmsg.error.webhook.address_repeat";
        /**
         * webhook WebHook不存在
         */
        public static final String WEBHOOK_NOT_EXISTS = "hmsg.error.webhook.not_exists";
        /**
         * webhook WebHook类型非法
         */
        public static final String WEBHOOK_TYPE_ILLEGAL = "hmsg.error.webhook.type_illegal";


        public static final String GLOBAL_CONFIG_NOT_EXIST = "hmsg.error.global_receiver_config.not_exist";
        public static final String CATEGORY_CODE_REPEAT = "hmsg.error.category_code.repeat";
        public static final String MISSING_RECIPIENT = "hmsg.error.missing_recipient";
        /**
         * 模板参数过长
         */
        public static final String TEMPLATE_ARG_TOO_LONG = "hmsg.error.template_arg.too_long";
        public static final String WEBHOOK_JSON_SECRET_NOT_MATCH = "hmsg.error.json_secret.not_match";
    }

    public static final class WebSocket {
        private WebSocket() {
        }

        /**
         * 站内消息长连接的key
         */
        public static final String KEY = "hzero-web";

        public static final String TENANT_ID = "tenantId";

        public static final String NUMBER = "number";
    }

    public static final class ReceiveConfig {
        private ReceiveConfig() {
        }

        public static final String OVERALL = "OVERALL";

        public static final Long OVERALL_PARENT_ID = 0L;
        public static final String OVERALL_PARENT_CODE = null;
    }

    public static final class CacheKey {
        /**
         * 最新已发布的通知公告：顺序
         */
        public static final String PUBLISHED_NOTICE_ORDER = APP_CODE + ":published_notice_order";
        /**
         * 最新已发布的通知公告：公告内容
         */
        public static final String PUBLISHED_NOTICE = APP_CODE + ":published_notice";

        /**
         * 门户模板
         */
        public static final String PORTAL_CONFIG = APP_CODE + ":portal_config:";
    }

    /**
     * 邮箱策略
     */
    public static final class FilterStrategy {
        private FilterStrategy() {
        }

        public static final String BLACK = "BLACK";
        public static final String WHITE = "WHITE";
    }

    public static final class WeChatAuthType {
        private WeChatAuthType() {
        }

        public static final String WE_CHAT = "WeChat";
        public static final String THIRD = "Third";
    }

    public static final class DingTalkAuthType {
        private DingTalkAuthType() {
        }

        public static final String DING_TALK = "DingTalk";
        public static final String THIRD = "Third";
    }

    public static final class WebHookServerType {
        private WebHookServerType() {
        }

        public static final String WE_CHAT = "WeChat";
        public static final String DING_TALK = "DingTalk";
        public static final String JSON = "Json";
    }

    /**
     * 接收者账户类型
     */
    public static final class ReceiverAccountType {
        private ReceiverAccountType() {
        }

        public static final String EMAIL = "EMAIL";

        public static final String PHONE = "PHONE";
    }

    public static final class TemplateEditType {
        private TemplateEditType (){}

        public static final String MARKDOWN = "MD";
        public static final String RICH_TEXT = "RT";
    }

}
