package org.hzero.oauth.security.sso;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import org.hzero.oauth.security.event.AuthorizeEvent;
import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
@Order(0)
@Component
public class SsoAuthorizeListener implements ApplicationListener<AuthorizeEvent> {

    @Autowired(required = false)
    private final List<SsoAuthorizeSuccessHandler> ssoAuthorizeSuccessHandlerList = new ArrayList<>();

    @Override
    public void onApplicationEvent(AuthorizeEvent event) {
        if (CollectionUtils.isEmpty(ssoAuthorizeSuccessHandlerList)) {
            return;
        }

        HttpServletRequest request = event.getServletRequest();
        for (SsoAuthorizeSuccessHandler handler : ssoAuthorizeSuccessHandlerList) {
            if (handler.requiresHandle(request)) {
                handler.onAuthorizeSuccessHandler(request, event.getAuthorizationRequest(), event.getAccessToken());
            }
        }
    }
}
