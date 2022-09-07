package org.hzero.boot.message.constant;

import org.hzero.common.HZeroService;

/**
 * 消息客户端常量
 *
 * @author shuangfei.zhu@hand-china.com 2020/05/12 16:35
 */
public class HmsgBootConstant {

    private HmsgBootConstant() {
    }


    public static class CacheKey {
        private CacheKey() {
        }

        /**
         * 最新已发布的通知公告：顺序
         */
        public static final String PUBLISHED_NOTICE_ORDER = HZeroService.Message.CODE + ":published_notice_order";
        /**
         * 最新已发布的通知公告：公告内容
         */
        public static final String PUBLISHED_NOTICE = HZeroService.Message.CODE + ":published_notice";

    }

    public static class ThirdPlatformType {
        private ThirdPlatformType(){}
        /**
         * 微信
         */
        public static final String WX = "WX";
        /**
         * 钉钉
         */
        public static final String DD = "DD";
    }

    public static class MessageType {
        private MessageType() {
        }

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
         * 企业微信消息
         */
        public static final String WC_E = "WC_E";
        /**
         * 微信公众号消息
         */
        public static final String WC_O = "WC_O";
        /**
         * 钉钉消息
         */
        public static final String DT = "DT";
    }
}
