package org.hzero.oauth.security.secheck;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 二次校验登录认证过滤器
 * <p>
 * 参考 {@link UsernamePasswordAuthenticationFilter}
 *
 * @author bergturing 2020/08/25
 */
public class SecCheckAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SEC_CHECK_TYPE_KEY = "secCheckType";
    private static final String SEC_CHECK_DEFAULT_LOGIN_PROCESS_URL = "/login/secCheck";

    private String secCheckTypeParameter = SEC_CHECK_TYPE_KEY;
    private boolean postOnly = true;

    /**
     * 仅匹配 [POST /login/secCheck]
     */
    public SecCheckAuthenticationFilter() {
        super(new AntPathRequestMatcher(SEC_CHECK_DEFAULT_LOGIN_PROCESS_URL, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String secCheckType = this.obtainSecCheckType(request);

        if (secCheckType == null) {
            secCheckType = "";
        }

        secCheckType = secCheckType.trim();

        SecCheckAuthenticationToken authRequest = new SecCheckAuthenticationToken(secCheckType);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainSecCheckType(HttpServletRequest request) {
        return request.getParameter(this.secCheckTypeParameter);
    }

    protected void setDetails(HttpServletRequest request, SecCheckAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    public final String getSecCheckTypeParameter() {
        return secCheckTypeParameter;
    }

    public void setSecCheckTypeParameter(String secCheckTypeParameter) {
        Assert.hasText(secCheckTypeParameter, "Sender parameter must not be empty or null");
        this.secCheckTypeParameter = secCheckTypeParameter;
    }

}
