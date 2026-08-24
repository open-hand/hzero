package org.hzero.oauth.security.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 * 登出成功事件
 *
 * @author bojiangzhou 2020/08/24
 */
public class LogoutEvent extends ApplicationEvent {
    private static final long serialVersionUID = 7477787184984693637L;

    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final Authentication authentication;

    public LogoutEvent(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Authentication authentication) {
        super(servletRequest);
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;
        this.authentication = authentication;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
