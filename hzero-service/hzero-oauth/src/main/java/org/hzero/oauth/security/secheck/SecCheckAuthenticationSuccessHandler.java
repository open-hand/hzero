package org.hzero.oauth.security.secheck;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.custom.BaseAuthenticationSuccessHandler;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 二次校验认证成功处理器
 *
 * @author bergturing 2021/06/18
 */
public class SecCheckAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    public SecCheckAuthenticationSuccessHandler(SecurityProperties securityProperties) {
        super(securityProperties);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
