package org.hzero.boot.message.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 消息参数构建器
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 9:17
 */
public class MessageArg {
    private MessageArg() {
    }

    public static MessageArgBuilder builder() {
        return new MessageArgBuilder();
    }

    public static MessageArgBuilder builder(String key, String value) {
        return new MessageArgBuilder(key, value);
    }

    public static class MessageArgBuilder {
        private Map<String, String> args;

        private MessageArgBuilder() {
            args = new HashMap<>(16);
        }

        private MessageArgBuilder(String key, String value) {
            this.args = new HashMap<>(16);
            this.args.put(key, value);
        }


        public MessageArgBuilder add(String key, String value) {
            this.args.put(key, value);
            return this;
        }

        public Map<String, String> build() {
            return args;
        }
    }
}
