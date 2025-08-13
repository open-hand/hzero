package org.hzero.boot.message.constant;

/**
 * webSocket常量类
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 11:07
 */
public class WebSocketConstant {

    private WebSocketConstant() {
    }

    public static final String MESSAGE_HANDLER_PATH = "/socket/message/handler";

    public static final String REDIS_KEY = "websocket";

    public static final String CHANNEL = "hzero-webSocket";

    public static final String ALIVE = "$";

    public static final class SendType {
        private SendType() {
        }

        public static final String SESSION = "S";
        public static final String USER = "U";
        public static final String ALL = "A";
    }
}
