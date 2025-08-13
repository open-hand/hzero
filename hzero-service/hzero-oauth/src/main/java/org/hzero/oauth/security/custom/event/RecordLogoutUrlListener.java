package org.hzero.oauth.security.custom.event;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.hzero.oauth.security.event.AuthorizeEvent;
import org.hzero.oauth.security.service.LoginRecordService;

/**
 * @author bojiangzhou 2020/08/24
 */
@Order(100)
@Component
public class RecordLogoutUrlListener implements ApplicationListener<AuthorizeEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordLogoutUrlListener.class);

    @Autowired
    private LoginRecordService loginRecordService;

    @Override
    public void onApplicationEvent(@Nonnull AuthorizeEvent event) {
        String token = event.getAccessToken().getValue();

        if (loginRecordService.existsLogoutUrl(token)) {
            return;
        }

        HttpServletRequest request = event.getServletRequest();
        String logoutRedirectUrl = request.getParameter("redirect_uri");
        if (StringUtils.isBlank(logoutRedirectUrl)) {
            return;
        }

        LOGGER.debug("record logoutUrl, token: [{}], logoutRedirectUrl: [{}]", token, logoutRedirectUrl);

        loginRecordService.recordLogoutUrl(token, logoutRedirectUrl);
    }
}
