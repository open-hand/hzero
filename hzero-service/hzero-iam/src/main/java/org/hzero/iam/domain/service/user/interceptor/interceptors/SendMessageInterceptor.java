package org.hzero.iam.domain.service.user.interceptor.interceptors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.core.user.UserType;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserConfigRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.user.util.ConfigGetter;
import org.hzero.iam.domain.service.user.util.ProfileCode;
import org.hzero.iam.infra.constant.Constants;

/**
 * 发送消息
 *
 * @author bojiangzhou 2020/05/29
 */
@Component
public class SendMessageInterceptor implements HandlerInterceptor<User> {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserConfigRepository userConfigRepository;
    @Autowired
    protected ConfigGetter configGetter;
    @Autowired
    protected MessageClient messageClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageInterceptor.class);

    private static final String PARAM_INDEX_URL = "indexUrl";

    @Override
    public void interceptor(User user) {
        if (!configGetter.isTrue(user.getOrganizationId(), ProfileCode.IF_SEND_CREATE_USER)) {
            return;
        }
        Map<String, String> params = new HashMap<>(2);
        params.put(User.FIELD_LOGIN_NAME, user.getLoginName());
        params.put(User.FIELD_PASSWORD, user.getTmpPassword());
        params.put(User.FIELD_REAL_NAME, user.getRealName());
        params.put(User.FIELD_EMAIL, user.getEmail());
        params.put(User.FIELD_PHONE, user.getPhone());
        params.put(User.FIELD_NICKNAME, user.getNickname());
        params.put(Tenant.TENANT_NUM, user.getTenantNum());
        params.put(Tenant.TENANT_NAME, user.getTenantName());
        params.put(PARAM_INDEX_URL, configGetter.getValue(user.getOrganizationId(), ProfileCode.MSG_PARAM_INDEX_URL));

        ProfileCode msgCode = null;
        if (!StringUtils.equals(user.getUserType(), UserType.DEFAULT_USER_TYPE)) {
            msgCode = ProfileCode.MSG_CODE_CREATE_USER_APP;
        } else {
            msgCode = ProfileCode.MSG_CODE_CREATE_USER;
        }

        Receiver receiver = new Receiver()
                .setUserId(user.getId())
                .setTargetUserTenantId(user.getOrganizationId())
                .setEmail(user.getEmail())
                .setPhone(user.getPhone());

        LOGGER.debug("send user create message, messageCode={}, receiver={}, params={}", msgCode.name(), receiver, params);

        // 发送创建消息
        try {
            messageClient.async().sendMessage(
                    Constants.SITE_TENANT_ID,
                    configGetter.getValue(msgCode),
                    null,
                    Collections.singletonList(receiver),
                    params
            );
        } catch (Exception e) {
            LOGGER.error("send message error after create user, ex={}", e.getMessage());
        }
    }
}
