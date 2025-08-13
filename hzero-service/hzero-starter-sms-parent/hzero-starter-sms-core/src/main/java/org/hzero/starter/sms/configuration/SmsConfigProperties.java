package org.hzero.starter.sms.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通用短信配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 15:49
 */
@Component
@ConfigurationProperties(prefix = "hzero.message")
public class SmsConfigProperties {

    private Sms sms = new Sms();

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

    public Sms getSms() {
        return sms;
    }

    public SmsConfigProperties setSms(Sms sms) {
        this.sms = sms;
        return this;
    }
}
