package org.hzero.starter.call.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hzero.starter.call.configuration.CallConfigProperties;
import org.hzero.starter.call.constant.CallConstant;
import org.hzero.starter.call.entity.CallConfig;
import org.hzero.starter.call.entity.CallMessage;
import org.hzero.starter.call.entity.CallReceiver;
import org.hzero.starter.call.support.JdCallSupporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 京东语音消息
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:45
 */
@Component
public class JdCallServiceImpl implements CallService {


    private final CallConfigProperties configProperties;

    @Autowired
    public JdCallServiceImpl(CallConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public String serverType() {
        return "JINGDONG";
    }

    @Override
    public void callSend(List<CallReceiver> receiverAddressList, CallConfig callConfig, CallMessage message, Map<String, String> args) {
        List<String> telephoneList;
        if (configProperties.getCall().isFakeAction() && StringUtils.hasText(configProperties.getCall().getFakeAccount())) {
            telephoneList = Collections.singletonList(configProperties.getCall().getFakeAccount());
        } else if (configProperties.getCall().isFakeAction()) {
            return;
        } else {
            telephoneList = receiverAddressList.stream().filter(item -> Objects.equals(item.getIdd(), CallConstant.DEFAULT_IDD))
                    .map(CallReceiver::getPhone).collect(Collectors.toList());
        }
        JdCallSupporter.sendCall(callConfig, telephoneList, message.getContent());
    }
}
