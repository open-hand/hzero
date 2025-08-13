package org.hzero.oauth.security.secheck;

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
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;

/**
 * 登录失败处理器
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecCheckAuthenticationFailureHandler.class);

    private final LoginRecordService loginRecordService;
    private final SecurityProperties securityProperties;
    private final AuditLoginService auditLoginService;

    private final String secCheckTypeParameter;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public SecCheckAuthenticationFailureHandler(LoginRecordService loginRecordService,
                                                SecurityProperties securityProperties, AuditLoginService auditLoginService) {
        this.loginRecordService = loginRecordService;
        this.securityProperties = securityProperties;
        this.auditLoginService = auditLoginService;

        this.secCheckTypeParameter = securityProperties.getLogin().getSecCheckTypeParameter();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String secCheckType = request.getParameter(this.secCheckTypeParameter);
        String message = exception.getMessage();

        HttpSession session = request.getSession();
        User loginUser = this.loginRecordService.getLocalLoginUser();
        session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME, secCheckType);

        if (loginUser != null) {
            session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USER, loginUser);
        }

        LOGGER.debug("Secondary Check Login Failed(SEC), Secondary Check Type={}, exMsg={}", secCheckType, message);

        if (loginUser == null) {
            loginUser = new User();
            Object attribute = session.getAttribute(SecCheckVO.SEC_CHECK_KEY);
            if (attribute instanceof SecCheckVO) {
                SecCheckVO secCheckVO = (SecCheckVO) attribute;
                loginUser.setPhone(secCheckVO.getPhone());
                loginUser.setEmail(secCheckVO.getEmail());
            }
        }
        Object[] parameters = null;
        if (exception instanceof CustomAuthenticationException) {
            CustomAuthenticationException ex = (CustomAuthenticationException) exception;
            parameters = ex.getParameters();
        }

        // 捕获异常，异步记录登录失败日志
        this.auditLoginService.addLogFailureRecord(request, loginUser,
                MessageAccessor.getMessage(exception.getMessage(), parameters, LoginUtil.getLanguageLocale()).desc());
        session.setAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION, MessageAccessor.getMessage(exception.getMessage(), parameters, LoginUtil.getLanguageLocale()).desc());

        // 重定向
        this.redirectStrategy.sendRedirect(request, response, RequestUtil.getBaseURL(request) +
                this.securityProperties.getLogin().getPassSecondaryCheckPage() + "?"
                + SecCheckVO.PARAMETER_SEC_CHECK_TYPE + "=" + secCheckType);
    }
}
