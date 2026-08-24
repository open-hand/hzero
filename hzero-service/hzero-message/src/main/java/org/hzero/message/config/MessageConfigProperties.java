package org.hzero.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 消息配置属性
 * </p>
 *
 * @author qingsheng.chen 2018/8/13 星期一 9:48
 */
@Component
@ConfigurationProperties(prefix = "hzero.message")
public class MessageConfigProperties {

    /**
     * 铃铛展示的未读消息数量
     */
    private long maxUnreadMessageCount = 5L;

    /**
     * 异步发送消息
     */
    private boolean async = false;

    private Sms sms = new Sms();
    private Email email = new Email();

    public static class Sms {
        /**
         * 伪装动作：如果为真则不会发送短信到目标用户
         */
        private boolean fakeAction = false;
        /**
         * 伪装账号
         * 如果有值且fakeAction为真，则所有短信都会被拦截发送至该伪装账号
         * 如果无值且fakeAction为真，则不会发生发送短信的动作，但是会返回发送成功
         */
        private String fakeAccount;
        private String fakeIdd = "+86";

        public boolean isFakeAction() {
            return fakeAction;
        }

        public Sms setFakeAction(boolean fakeAction) {
            this.fakeAction = fakeAction;
            return this;
        }

        public String getFakeAccount() {
            return fakeAccount;
        }

        public Sms setFakeAccount(String fakeAccount) {
            this.fakeAccount = fakeAccount;
            return this;
        }

        public String getFakeIdd() {
            return fakeIdd;
        }

        public Sms setFakeIdd(String fakeIdd) {
            this.fakeIdd = fakeIdd;
            return this;
        }
    }

    public static class Email {
        /**
         * 伪装动作：如果为真则不会发送邮件到目标用户
         */
        private boolean fakeAction = false;
        /**
         * 伪装账号
         * 如果有值且fakeAction为真，则所有邮件都会被拦截发送至该伪装账号
         * 如果无值且fakeAction为真，则不会发生发送邮件的动作，但是会返回发送成功
         */
        private String fakeAccount;

        public boolean isFakeAction() {
            return fakeAction;
        }

        public Email setFakeAction(boolean fakeAction) {
            this.fakeAction = fakeAction;
            return this;
        }

        public String getFakeAccount() {
            return fakeAccount;
        }

        public Email setFakeAccount(String fakeAccount) {
            this.fakeAccount = fakeAccount;
            return this;
        }
    }

    public long getMaxUnreadMessageCount() {
        return maxUnreadMessageCount;
    }

    public MessageConfigProperties setMaxUnreadMessageCount(long maxUnreadMessageCount) {
        this.maxUnreadMessageCount = maxUnreadMessageCount;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public MessageConfigProperties setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public Sms getSms() {
        return sms;
    }

    public MessageConfigProperties setSms(Sms sms) {
        this.sms = sms;
        return this;
    }

    public Email getEmail() {
        return email;
    }

    public MessageConfigProperties setEmail(Email email) {
        this.email = email;
        return this;
    }
}
