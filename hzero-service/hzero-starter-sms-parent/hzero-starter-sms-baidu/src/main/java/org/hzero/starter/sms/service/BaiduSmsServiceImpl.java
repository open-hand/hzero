package org.hzero.starter.sms.service;

import java.util.*;

import com.baidubce.services.sms.model.SendMessageItem;
import com.baidubce.services.sms.model.SendMessageV3Response;
import org.hzero.starter.sms.configuration.SmsConfigProperties;
import org.hzero.starter.sms.constant.SmsConstant;
import org.hzero.starter.sms.entity.SmsConfig;
import org.hzero.starter.sms.entity.SmsMessage;
import org.hzero.starter.sms.entity.SmsReceiver;
import org.hzero.starter.sms.exception.SendMessageException;
import org.hzero.starter.sms.support.BaiduSmsSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:45
 */
@Component
public class BaiduSmsServiceImpl extends SmsService {

    private final SmsConfigProperties configProperties;

    @Autowired
    public BaiduSmsServiceImpl(SmsConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public String serverType() {
        return "BAIDU";
    }

    @Override
    public void smsSend(List<SmsReceiver> receiverAddressList, SmsConfig smsConfig, SmsMessage message, Map<String, String> args) {
        List<String> telephoneList = new ArrayList<>();
        if (configProperties.getSms().isFakeAction() && StringUtils.hasText(configProperties.getSms().getFakeAccount())) {
            telephoneList = Collections.singletonList(configProperties.getSms().getFakeAccount());
        } else if (configProperties.getSms().isFakeAction()) {
            return;
        } else {
            for (SmsReceiver item : receiverAddressList) {
                String idd = item.getIdd();
                if (!StringUtils.hasLength(idd) || Objects.equals(idd, SmsConstant.DEFAULT_IDD)) {
                    telephoneList.add(item.getPhone());
                } else {
                    // 港澳台及国际手机号需要拼接国际冠码
                    telephoneList.add(idd + item.getPhone());
                }
            }
        }
        SendMessageV3Response response = BaiduSmsSupporter.sendSms(BaiduSmsSupporter.smsClient(smsConfig), smsConfig, message.getExternalCode(), telephoneList, args);
        if (response == null) {
            throw new SendMessageException("baidu sms send failed!");
        }
        if (!response.isSuccess()) {
            throw new SendMessageException(String.format("baidu sms send failed! code: [%s] , message:  [%s]", response.getCode(), response.getMessage()));
        }
        // 判断每个手机的发送结果，有一个失败就标记为失败
        boolean flag = true;
        StringBuilder sb  = new StringBuilder();
        for(SendMessageItem item : response.getData()) {
            if (!"1000".equals(item.getCode())) {
                flag = false;
                sb.append(String.format("mobile: %s send failed! code: %s , message: %s ;", item.getMobile(), item.getCode(), item.getMessage()));
            }
        }
        if (!flag) {
            throw new SendMessageException(String.format("baidu sms send failed! detailMessage:  [%s]", sb.toString()));
        }
    }
}
