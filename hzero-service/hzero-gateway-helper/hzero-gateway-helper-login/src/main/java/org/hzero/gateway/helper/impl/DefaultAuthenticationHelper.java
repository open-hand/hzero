package org.hzero.gateway.helper.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.TokenUtils;
import org.hzero.gateway.helper.api.AuthenticationHelper;
import org.hzero.gateway.helper.entity.CheckRequest;
import org.hzero.gateway.helper.entity.CheckResponse;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.entity.ResponseContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hzero.core.variable.RequestVariableHolder.HEADER_BEARER;

/**
 * 默认鉴权 helper
 */
public class DefaultAuthenticationHelper implements AuthenticationHelper {

    private static final String TOKEN_PREFIX = StringUtils.lowerCase(HEADER_BEARER + " ");

    private HelperChain chain;

    public DefaultAuthenticationHelper(HelperChain chain) {
        this.chain = chain;
    }

    @Override
    public ResponseContext authentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext requestContext = RequestContext.initRequestContext(new CheckRequest(TOKEN_PREFIX + TokenUtils.getToken(request),
                request.getRequestURI(), request.getMethod().toLowerCase()), new CheckResponse());
        requestContext.setServletRequest(request);
        return chain.doFilter(requestContext);
    }

}