package org.hzero.oauth.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

/**
 * 认证成功事件
 *
 * @author bojiangzhou 2020/08/24
 */
public class AuthorizeEvent extends ApplicationEvent {
    private static final long serialVersionUID = 3306977780488580184L;

    private final HttpServletRequest servletRequest;
    private final AuthorizationRequest authorizationRequest;
    private final OAuth2AccessToken accessToken;

    public AuthorizeEvent(HttpServletRequest servletRequest, AuthorizationRequest authorizationRequest, OAuth2AccessToken accessToken) {
        super(accessToken);
        this.servletRequest = servletRequest;
        this.authorizationRequest = authorizationRequest;
        this.accessToken = accessToken;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public AuthorizationRequest getAuthorizationRequest() {
        return authorizationRequest;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

}
