package org.hzero.scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 告警邮件配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/24 15:33
 */
@Component
@ConfigurationProperties(prefix = "hzero.scheduler")
public class SchedulerConfiguration {

    /**
     * 串行任务锁定自动失效时间，单位:秒
     */
    private Integer lockTime = 300;

    /**
     * 告警邮件配置
     */
    private AlarmEmail alarmEmail = new AlarmEmail();

    public Integer getLockTime() {
        return lockTime;
    }

    public SchedulerConfiguration setLockTime(Integer lockTime) {
        this.lockTime = lockTime;
        return this;
    }

    public AlarmEmail getAlarmEmail() {
        return alarmEmail;
    }

    public SchedulerConfiguration setAlarmEmail(AlarmEmail alarmEmail) {
        this.alarmEmail = alarmEmail;
        return this;
    }

    public static class AlarmEmail {
        /**
         * 消息发送模板编码
         */
        private String messageCode = "HSDR.SCHEDULER_ALARM";

        public String getMessageCode() {
            return messageCode;
        }

        public void setMessageCode(String messageCode) {
            this.messageCode = messageCode;
        }
    }
}
