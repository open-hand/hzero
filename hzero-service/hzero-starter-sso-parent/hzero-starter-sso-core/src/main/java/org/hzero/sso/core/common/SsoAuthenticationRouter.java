package org.hzero.sso.core.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

/**
 * 单点认证服务路由器，决定跳转到哪个单点认证服务器去认证
 *
 * @author bojiangzhou 2020/08/12
 */
public interface SsoAuthenticationRouter {

    void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)
            throws IOException, ServletException;
}