package org.hzero.starter.sms.support;

import java.util.List;
import java.util.Map;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.starter.sms.entity.SmsConfig;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 阿里云短信发送支持
 * </p>
 *
 * @author qingsheng.chen 2019/1/21 星期一 20:31
 */
public class AliyunSmsSupporter {

    /**
     * 短信API产品名称
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 短信API产品域名
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    /**
     * 地区
     */
    private static final String REGION_ID = "cn-hangzhou";

    private AliyunSmsSupporter() {
    }

    public static IAcsClient acsClient(SmsConfig smsConfig) {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        IClientProfile profile = DefaultProfile.getProfile(REGION_ID, smsConfig.getAccessKey(), smsConfig.getAccessKeySecret());
        try {
            DefaultProfile.addEndpoint(smsConfig.getEndPoint(), REGION_ID, PRODUCT, DOMAIN);
        } catch (ClientException e) {
            throw new CommonException("Unable to generate Acs Client", e);
        }
        return new DefaultAcsClient(profile);
    }

    public static SendSmsResponse sendSms(IAcsClient acsClient, SmsConfig smsConfig, String externalCode, List<String> receiverAddress, Map<String, String> args, ObjectMapper objectMapper) throws ClientException, JsonProcessingException {
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(StringUtils.collectionToCommaDelimitedString(receiverAddress));
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(smsConfig.getSignName());
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(externalCode);
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(objectMapper.writeValueAsString(args));
        return acsClient.getAcsResponse(request);
    }
}
