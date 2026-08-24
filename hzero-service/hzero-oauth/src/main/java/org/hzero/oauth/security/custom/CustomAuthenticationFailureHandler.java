package org.hzero.oauth.security.custom;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.message.MessageAccessor;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.LoginType;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

/**
 * 登录失败处理器
 *
 * @author bojiangzhou 2019/02/25
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler, Serializable {
    private static final long serialVersionUID = 8528419529380369200L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    private LoginRecordService loginRecordService;
    private SecurityProperties securityProperties;
    private AuditLoginService auditLoginService;

    private String usernameParameter;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public CustomAuthenticationFailureHandler(LoginRecordService loginRecordService,
                                              SecurityProperties securityProperties, AuditLoginService auditLoginService) {
        this.loginRecordService = loginRecordService;
        this.securityProperties = securityProperties;
        this.auditLoginService = auditLoginService;

        this.usernameParameter = securityProperties.getLogin().getUsernameParameter();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        LOGGER.debug("Login authentication failure, exception is:", exception);

        String username = request.getParameter(usernameParameter);

        HttpSession session = request.getSession();
        User loginUser = loginRecordService.getLocalLoginUser();
        session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USERNAME, username);

        if (loginUser != null) {
            session.setAttribute(SecurityAttributes.FIELD_ORGANIZATION_ID, loginUser.getOrganizationId());
            session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USER, loginUser);
            session.setAttribute(SecurityAttributes.SECURITY_LOGIN_USER_ID, loginUser.getId());
        } else {
            loginUser = new User();
            loginUser.setLoginName(username);
        }

        LOGGER.debug("user login failed, username={}, exMsg={}", username, exception.getMessage());

        String message = getExceptionMessage(session, exception);
        session.setAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION, message);
        // 捕获异常，异步记录登录失败日志
        auditLoginService.addLogFailureRecord(request, loginUser, message);

        // 跳转到密码过期页面
        if (StringUtils.equals(exception.getMessage(), LoginExceptions.PASSWORD_EXPIRED.value())) {
            redirectStrategy.sendRedirect(request, response, getExpirePage(request));
            return;
        }
        // 跳转到强制修改密码页面
        if (StringUtils.equals(exception.getMessage(), LoginExceptions.PASSWORD_FORCE_MODIFY.value())) {
            redirectStrategy.sendRedirect(request, response, getForceModifyPage(request));
            return;
        }

        // 跳转到登录页面
        String URL = RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPage();
        redirectStrategy.sendRedirect(request, response, URL + "?type=" + LoginType.ACCOUNT.code());
    }

    protected String getExpirePage(HttpServletRequest request) {
        return RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPassExpiredPage();
    }

    /**
     * 获取强制修改密码的页面路径
     *
     * @param request 请求对象
     * @return 强制修改密码页面路径
     */
    protected String getForceModifyPage(HttpServletRequest request) {
        return RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPassForceModifyPage();
    }

    private String getExceptionMessage(HttpSession session, AuthenticationException exception) {
        String message = null;
        Object[] parameters = null;
        if (exception instanceof CustomAuthenticationException) {
            CustomAuthenticationException ex = (CustomAuthenticationException) exception;
            parameters = ex.getParameters();
        }

        String lang = (String) session.getAttribute(SecurityAttributes.FIELD_LANG);
        if (StringUtils.isBlank(lang) || !lang.contains("_")) {
            lang = securityProperties.getDefaultLanguage();
        }
        String[] langs = lang.split("_");
        if (langs.length > 1) {
            message = MessageAccessor.getMessage(exception.getMessage(), parameters, new Locale(langs[0], langs[1])).desc();
        } else {
            message = MessageAccessor.getMessage(exception.getMessage(), parameters, LoginUtil.getLanguageLocale()).desc();
        }

        return message;
    }

    protected LoginRecordService getLoginRecordService() {
        return loginRecordService;
    }

    protected SecurityProperties getSecurityProperties() {
        return securityProperties;
    }
}