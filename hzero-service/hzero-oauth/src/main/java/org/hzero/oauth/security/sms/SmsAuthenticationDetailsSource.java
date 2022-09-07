package org.hzero.oauth.security.sms;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * 自定义获取 AuthenticationDetails 用于封装传进来的短信验证码
 *
 * @author bojiangzhou 2019/02/25
 */
public class SmsAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new SmsAuthenticationDetails(request);
    }


}
