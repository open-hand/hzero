package org.hzero.oauth.security.custom.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import org.hzero.core.util.TokenUtils;
import org.hzero.oauth.security.event.LogoutEvent;

/**
 * @author bojiangzhou 2020/08/24
 */
@Order
@Component
public class LogoutClearListener implements ApplicationListener<LogoutEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutClearListener.class);

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void onApplicationEvent(LogoutEvent event) {
        HttpServletRequest request = event.getServletRequest();

        String token = TokenUtils.getToken(request);
        if (token != null) {
            LOGGER.debug("logout clear access token :{} ", token);
            tokenStore.removeAccessToken(new DefaultOAuth2AccessToken(token));
            tokenStore.removeRefreshToken(new DefaultOAuth2RefreshToken(token));
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
