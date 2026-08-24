package org.hzero.sso.core.common;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import org.hzero.core.message.MessageAccessor;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * 认证失败处理器
 *
 * @author bojiangzhou 2020/08/18
 */
public class SsoAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {

    public SsoAuthenticationFailureHandler() {
        super("/login");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        Domain domain = SsoContextHolder.getDomain();
        if (domain != null && domain.getDomainUrl() != null) {
            String errorMessage = MessageAccessor.getMessage(exception.getMessage()).desc();
            String domainUrl = domain.getDomainUrl();
            if (domainUrl.indexOf("?") > 0) {
                domainUrl = domainUrl + "&errorMessage=" + URLEncoder.encode(errorMessage, Charsets.UTF_8.name());
            } else {
                domainUrl = domainUrl + "?errorMessage=" + URLEncoder.encode(errorMessage, Charsets.UTF_8.name());
            }
            response.sendRedirect(domainUrl);
        } else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }

}
