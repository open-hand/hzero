package org.hzero.gateway.helper.impl.reactive;

import static org.hzero.core.variable.RequestVariableHolder.ACCESS_TOKEN;
import static org.hzero.core.variable.RequestVariableHolder.HEADER_AUTH;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import org.hzero.core.util.ServerRequestUtils;
import org.hzero.gateway.helper.api.reactive.ReactiveAuthenticationHelper;
import org.hzero.gateway.helper.entity.CheckRequest;
import org.hzero.gateway.helper.entity.CheckResponse;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.entity.ResponseContext;
import org.hzero.gateway.helper.impl.HelperChain;

/**
 * @author XCXCXCXCX
 * @date 2019/9/4
 */
public class DefaultReactiveAuthenticationHelper implements ReactiveAuthenticationHelper {

    private static final String BEARER_PREFIX = "Bearer ";

    private final HelperChain chain;

    public DefaultReactiveAuthenticationHelper(HelperChain chain) {
        this.chain = chain;
    }

    @Override
    public ResponseContext authentication(ServerWebExchange exchange) throws IOException {
        RequestContext requestContext = RequestContext.initRequestContext(new CheckRequest(BEARER_PREFIX.toLowerCase() + parse(exchange.getRequest()),
                exchange.getRequest().getURI().getPath(), exchange.getRequest().getMethod().name().toLowerCase()), new CheckResponse());
        requestContext.setServletRequest(exchange.getRequest());
        return chain.doFilter(requestContext);
    }

    private String parse(final ServerHttpRequest req) {
        String accessToken = ServerRequestUtils.resolveHeader(req, HEADER_AUTH);
        if(StringUtils.isEmpty(accessToken)){
            accessToken = ServerRequestUtils.resolveParam(req, ACCESS_TOKEN);
        }
        if(org.springframework.util.StringUtils.startsWithIgnoreCase(accessToken, BEARER_PREFIX)) {
            accessToken = accessToken.substring(BEARER_PREFIX.length());
        }
        return accessToken;
    }

}
