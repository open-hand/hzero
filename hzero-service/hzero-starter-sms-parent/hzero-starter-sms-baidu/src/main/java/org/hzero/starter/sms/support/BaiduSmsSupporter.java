package org.hzero.starter.sms.support;

import java.util.List;
import java.util.Map;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV3Request;
import com.baidubce.services.sms.model.SendMessageV3Response;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.sms.entity.SmsConfig;

/**
 * 百度云短信支持
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:45
 */
public class BaiduSmsSupporter {

    private BaiduSmsSupporter() {
    }

    /**
     * 百度短信
     */
    public static SmsClient smsClient(SmsConfig smsConfig) {
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(smsConfig.getAccessKey(), smsConfig.getAccessKeySecret()));
        // Endpoint没有设置，使用百度的默认配置
        if (StringUtils.isNotBlank(smsConfig.getEndPoint())) {
            config.setEndpoint(smsConfig.getEndPoint());
        }
        return new SmsClient(config);
    }

    /**
     * 百度发送短信  单条发送
     */
    public static SendMessageV3Response sendSms(SmsClient smsClient, SmsConfig smsConfig, String externalCode, List<String> receiverAddress, Map<String, String> args) {
        SendMessageV3Request request = new SendMessageV3Request();
        // 短信签名
        request.setSignatureId(smsConfig.getSignName());
        // 模板编码
        request.setTemplate(externalCode);
        // 短信参数
        request.setContentVar(args);
        // 多个手机号之间以英文逗号分隔，一次请求最多支持200个手机号。国际/港澳台号码请按照E.164规范表示，例如台湾手机号以+886开头，”+“不能省略。
        request.setMobile(StringUtils.join(receiverAddress, ","));
        return smsClient.sendMessage(request);
    }
}
