package org.hzero.starter.sms.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.starter.sms.configuration.SmsConfigProperties;
import org.hzero.starter.sms.constant.SmsConstant;
import org.hzero.starter.sms.entity.SmsConfig;
import org.hzero.starter.sms.entity.SmsMessage;
import org.hzero.starter.sms.entity.SmsReceiver;
import org.hzero.starter.sms.exception.SendMessageException;
import org.hzero.starter.sms.support.AliyunSmsSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:45
 */
@Component
public class AliyunSmsServiceImpl extends SmsService {

    private static final String OK = "OK";

    private final SmsConfigProperties configProperties;
    private final ObjectMapper objectMapper;

    @Autowired
    public AliyunSmsServiceImpl(SmsConfigProperties configProperties,
                                ObjectMapper objectMapper) {
        this.configProperties = configProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String serverType() {
        return "ALIYUN";
    }

    @Override
    public void smsSend(List<SmsReceiver> receiverAddressList, SmsConfig smsConfig, SmsMessage message, Map<String, String> args) {
        List<String> telephoneList;
        if (configProperties.getSms().isFakeAction() && StringUtils.hasText(configProperties.getSms().getFakeAccount())) {
            telephoneList = Collections.singletonList(configProperties.getSms().getFakeIdd()
                    .replace(SmsConstant.IDD_PREFIX, SmsConstant.ALIYUN_PREFIX) + configProperties.getSms().getFakeAccount());
        } else if (configProperties.getSms().isFakeAction()) {
            return;
        } else {
            telephoneList = receiverAddressList.stream()
                    .map(item -> StringUtils.hasText(item.getIdd()) ?
                            item.getIdd().replace(SmsConstant.IDD_PREFIX, SmsConstant.ALIYUN_PREFIX) + item.getPhone() :
                            SmsConstant.DEFAULT_IDD.replace(SmsConstant.IDD_PREFIX, SmsConstant.ALIYUN_PREFIX) + item.getPhone())
                    .collect(Collectors.toList());
        }
        try {
            SendSmsResponse sendSmsResponse = AliyunSmsSupporter
                    .sendSms(AliyunSmsSupporter.acsClient(smsConfig), smsConfig, message.getExternalCode(), telephoneList, args, objectMapper);
            if (sendSmsResponse.getCode() == null || !OK.equals(sendSmsResponse.getCode())) {
                throw new SendMessageException(objectMapper.writeValueAsString(sendSmsResponse));
            }
        } catch (ClientException | JsonProcessingException e) {
            throw new CommonException(e);
        }
    }
}
