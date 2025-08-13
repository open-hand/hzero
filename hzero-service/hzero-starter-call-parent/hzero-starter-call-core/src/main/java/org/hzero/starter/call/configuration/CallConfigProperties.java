package org.hzero.starter.call.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通用语音消息配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 15:49
 */
@Component
@ConfigurationProperties(prefix = "hzero.message")
public class CallConfigProperties {

    private Call call = new Call();

    public static class Call {
        /**
         * 伪装动作：如果为真则不会发送消息到目标用户
         */
        private boolean fakeAction = false;
        /**
         * 伪装账号
         * 如果有值且fakeAction为真，则所有消息都会被拦截发送至该伪装账号
         * 如果无值且fakeAction为真，则不会发生发送消息的动作，但是会返回发送成功
         */
        private String fakeAccount;
        private String fakeIdd = "+86";

        public boolean isFakeAction() {
            return fakeAction;
        }

        public Call setFakeAction(boolean fakeAction) {
            this.fakeAction = fakeAction;
            return this;
        }

        public String getFakeAccount() {
            return fakeAccount;
        }

        public Call setFakeAccount(String fakeAccount) {
            this.fakeAccount = fakeAccount;
            return this;
        }

        public String getFakeIdd() {
            return fakeIdd;
        }

        public Call setFakeIdd(String fakeIdd) {
            this.fakeIdd = fakeIdd;
            return this;
        }
    }

    public Call getCall() {
        return call;
    }

    public CallConfigProperties setCall(Call call) {
        this.call = call;
        return this;
    }
}
