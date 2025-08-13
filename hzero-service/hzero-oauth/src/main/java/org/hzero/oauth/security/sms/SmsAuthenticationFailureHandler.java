package org.hzero.oauth.security.sms;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import org.hzero.core.message.MessageAccessor;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginType;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;

/**
 * 登录失败处理器
 *
 * @author bojiangzhou 2019/02/25
 */
public class SmsAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsAuthenticationFailureHandler.class);

    private final LoginRecordService loginRecordService;
    private final SecurityProperties securityProperties;
    private final AuditLoginService auditLoginService;

    private final String mobileParameter;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public SmsAuthenticationFailureHandler(LoginRecordService loginRecordService,
                                           SecurityProperties securityProperties, AuditLoginService auditLoginService) {
        this.loginRecordService = loginRecordService;
        this.securityProperties = securityProperties;
        this.auditLoginService = auditLoginService;

        this.mobileParameter = securityProperties.getLogin().getMobileParameter();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String mobile = request.getParameter(mobileParameter);
        String message = exception.getMessage();

        HttpSession session = request.getSession();
        User loginUser = loginRecordService.getLocalLoginUser();
        session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME, mobile);

        if (loginUser != null) {
            session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USER, loginUser);
        }

        LOGGER.debug("user login failed(sms), username={}, exMsg={}", mobile, message);

        if (loginUser == null) {
            loginUser = new User();
            loginUser.setPhone(mobile);
        }
        Object[] parameters = null;
        if (exception instanceof CustomAuthenticationException) {
            CustomAuthenticationException ex = (CustomAuthenticationException) exception;
            parameters = ex.getParameters();
        }

        // 捕获异常，异步记录登录失败日志
        auditLoginService.addLogFailureRecord(request, loginUser,
                MessageAccessor.getMessage(exception.getMessage(), parameters, LoginUtil.getLanguageLocale()).desc());
        session.setAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION, MessageAccessor.getMessage(exception.getMessage(), parameters, LoginUtil.getLanguageLocale()).desc());

        String URL = RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPage();
        redirectStrategy.sendRedirect(request, response, URL + "?type=" + LoginType.SMS.code() + "&" + SecurityAttributes.SECURITY_LOGIN_USERNAME + "=" + mobile);
    }
}
