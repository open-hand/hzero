package org.hzero.oauth.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;

/**
 * 登录成功事件
 *
 * @author bojiangzhou 2020/08/24
 */
public class LoginEvent extends ApplicationEvent {
    private static final long serialVersionUID = -4864790878637685278L;

    private final HttpServletRequest servletRequest;

    public LoginEvent(HttpServletRequest servletRequest) {
        super(servletRequest);
        this.servletRequest = servletRequest;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }
}
