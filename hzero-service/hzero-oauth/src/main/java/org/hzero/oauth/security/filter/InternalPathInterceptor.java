package org.hzero.oauth.security.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.resource.filter.JwtTokenExtractor;

import org.hzero.core.properties.CoreProperties;

@Component
public class InternalPathInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalPathInterceptor.class);
    private final ResourceServerTokenServices tokenServices;

    @Autowired
    public InternalPathInterceptor(CoreProperties properties) {
        this.tokenServices = createTokenService(properties);
    }

    private ResourceServerTokenServices createTokenService(CoreProperties properties) {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(properties.getOauthJwtKey());
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.warn("error.ResourceServerConfiguration.accessTokenConverter {}", e.getMessage());
        }
        defaultTokenServices.setTokenStore(new JwtTokenStore(converter));
        return defaultTokenServices;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查请求头是否包含 Jwt_Token
        if (!isValidJwt(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\": \"error\", \"message\": \"The accessed interface is an internal interface and access is prohibited\"}");
            return false;
        }
        return true;
    }

    private boolean isValidJwt(HttpServletRequest request) {
        String jwtTokenValue = JwtTokenExtractor.extractToken(request);
        if (StringUtils.isBlank(jwtTokenValue)) {
            return false;
        }
        CustomUserDetails details = null;
        try {
            OAuth2Authentication auth = tokenServices.loadAuthentication(jwtTokenValue);
            if (auth.getDetails() instanceof CustomUserDetails) {
                details = (CustomUserDetails) auth.getDetails();
            }
        } catch (Exception e) {
            return false;
        }
        return details != null;
    }
}
