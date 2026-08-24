package org.hzero.oauth.security.social;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.stereotype.Component;

import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.event.LoginEvent;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.starter.social.core.common.connect.SocialUserData;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;
import org.hzero.starter.social.core.security.ProviderBindHelper;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
@Component
public class OpenAccountBindListener implements ApplicationListener<LoginEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAccountBindListener.class);

    @Autowired
    private LoginRecordService loginRecordService;
    @Autowired
    private SocialUserProviderRepository userProviderRepository;

    @Override
    public void onApplicationEvent(LoginEvent event) {
        HttpServletRequest request = event.getServletRequest();
        Connection<?> connection = ProviderBindHelper.getConnection(request);
        if (connection == null) {
            return;
        }
        User user = loginRecordService.getLocalLoginUser();
        ConnectionData data = connection.createData();
        SocialUserData socialUserData = new SocialUserData(data);
        LOGGER.info("bind open user, username={}, socialUser={}", user.getLoginName(), socialUserData);
        userProviderRepository.createUserBind(user.getLoginName(), data.getProviderId(), data.getProviderUserId(), socialUserData);
        ProviderBindHelper.removeConnection(request);
    }
}
