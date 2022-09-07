package org.hzero.oauth.security.sso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import org.hzero.oauth.security.event.LogoutEvent;
import org.hzero.sso.core.common.SsoClientLogoutHandler;

/**
 *
 * @author bojiangzhou 2020/08/26
 */
@Order(0)
public class SsoClientLogoutListener implements ApplicationListener<LogoutEvent> {

    @Autowired(required = false)
    private final List<SsoClientLogoutHandler> clientLogoutHandlerList = new ArrayList<>();

    @Override
    public void onApplicationEvent(@Nonnull LogoutEvent event) {
        for (SsoClientLogoutHandler handler : clientLogoutHandlerList) {
            handler.onLogoutSuccess(event.getServletRequest(), event.getServletResponse(), event.getAuthentication());
        }
    }
}
