package org.hzero.starter.sms.service;

import java.util.List;
import java.util.Map;

import org.hzero.starter.sms.entity.SmsConfig;
import org.hzero.starter.sms.entity.SmsMessage;
import org.hzero.starter.sms.entity.SmsReceiver;

/**
 * 短信发送方法
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 16:54
 */
public abstract class SmsService {

    /**
     * 模板参数
     */
    protected ThreadLocal<List<String>> templateArgs = new ThreadLocal<>();

    public List<String> getTemplateArgs() {
        return templateArgs.get();
    }

    public SmsService setTemplateArgs(List<String> templateArgs) {
        this.templateArgs.set(templateArgs);
        return this;
    }

    public void remove(){
        templateArgs.remove();
    }

    /**
     * 获取服务类型
     *
     * @return 服务类型
     */
    public abstract String serverType();

    /**
     * 短信发送
     *
     * @param receiverAddressList 接收人地址
     * @param smsConfig           短信配置
     * @param message             消息内容
     * @param args                参数
     */
    public abstract void smsSend(List<SmsReceiver> receiverAddressList, SmsConfig smsConfig, SmsMessage message, Map<String, String> args);
}
