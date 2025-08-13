package org.hzero.oauth.security.social;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import org.hzero.core.message.MessageAccessor;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.util.LoginUtil;
import org.hzero.oauth.security.util.RequestUtil;
import org.hzero.starter.social.core.common.constant.SocialConstant;
import org.hzero.starter.social.core.exception.UserUnbindException;
import org.hzero.starter.social.core.security.SocialFailureHandler;
import org.hzero.starter.social.core.security.holder.SocialSessionHolder;

/**
 *
 * @author bojiangzhou 2019/09/03
 */
public class CustomSocialFailureHandler implements SocialFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomSocialFailureHandler.class);

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final SecurityProperties securityProperties;

    public CustomSocialFailureHandler(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession();
        String exMsg = MessageAccessor.getMessage(exception.getMessage(), LoginUtil.getLanguageLocale()).desc();
        logger.debug("social authenticated failed, ex={}", exMsg, exception);
        if (exception instanceof UserUnbindException) {
            session.setAttribute(SecurityAttributes.SECURITY_LAST_EXCEPTION, exMsg);
        }
        String redirectUrl = SocialSessionHolder.get(request, SocialConstant.PREFIX_REDIRECT_URL, request.getParameter(SocialConstant.PARAM_STATE));
        if (redirectUrl == null) {
            redirectUrl = RequestUtil.getBaseURL(request) + securityProperties.getLogin().getPage();
        } else {
            redirectUrl += "#social_error_message=" + URLEncoder.encode(exMsg, Charsets.UTF_8.displayName());
        }
        logger.debug("social auth failed, redirect to {}", redirectUrl);
        redirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
