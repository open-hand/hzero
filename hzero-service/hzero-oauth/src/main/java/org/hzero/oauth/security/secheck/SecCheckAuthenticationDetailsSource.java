package org.hzero.oauth.security.secheck;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义获取 AuthenticationDetails 用于封装传进来的二次校验验证码
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new SecCheckAuthenticationDetails(request);
    }
}
