package org.hzero.oauth.security.custom;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.secheck.SecCheckHelper;
import org.hzero.oauth.security.service.LoginRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理器
 *
 * @author bojiangzhou 2019/02/25
 */
public class CustomAuthenticationSuccessHandler extends BaseAuthenticationSuccessHandler {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private LoginRecordService loginRecordService;

    public CustomAuthenticationSuccessHandler(SecurityProperties securityProperties) {
        super(securityProperties);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (SecCheckHelper.redirectToSecCheck(request, response, this.getRedirectStrategy(),
                this.getSecurityProperties(), loginRecordService.getLocalLoginUser())) {
            // 已跳转到二次校验页面
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}