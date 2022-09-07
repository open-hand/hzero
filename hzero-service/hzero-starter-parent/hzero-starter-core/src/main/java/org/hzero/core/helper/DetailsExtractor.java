package org.hzero.core.helper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import io.choerodon.core.oauth.CustomTokenConverter;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.resource.filter.JwtTokenExtractor;

import org.hzero.core.properties.CoreProperties;

/**
 * 解析JwtToken
 *
 * @author bojiangzhou 2018/10/09
 */
public class DetailsExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DetailsExtractor.class);

    private ResourceServerTokenServices tokenServices;
    private CoreProperties properties;

    public DetailsExtractor(CoreProperties properties) {
        this.properties = properties;
        this.tokenServices = tokenServices();
    }

    public CustomUserDetails extractDetails(HttpServletRequest request) {
        String jwtToken = JwtTokenExtractor.extractToken(request);
        if (StringUtils.isBlank(jwtToken)) {
            LOGGER.info("can't extract jwt_token from request header.");
            return null;
        }
        OAuth2Authentication auth = tokenServices.loadAuthentication(jwtToken);
        if (auth == null) {
            LOGGER.info("can't load authentication from jwt_token. jwt_toke is {}", jwtToken);
        } else {
            if (auth.getDetails() instanceof CustomUserDetails) {
                return (CustomUserDetails) auth.getDetails();
            }
        }
        return null;
    }

    public CustomUserDetails extractDetails(ServerHttpRequest request) {
        String jwtToken = JwtTokenExtractor.extractToken(request);
        if (StringUtils.isBlank(jwtToken)) {
            LOGGER.info("can't extract jwt_token from request header.");
            return null;
        }
        OAuth2Authentication auth = tokenServices.loadAuthentication(jwtToken);
        if (auth == null) {
            LOGGER.info("can't load authentication from jwt_token. jwt_toke is {}", jwtToken);
        } else {
            if (auth.getDetails() instanceof CustomUserDetails) {
                return (CustomUserDetails) auth.getDetails();
            }
        }
        return null;
    }

    /**
     * DefaultTokenService Bean
     *
     * @return DefaultTokenService对象
     */
    private DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * 返回converter
     *
     * @return converter
     */
    private JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setAccessTokenConverter(new CustomTokenConverter());
        converter.setSigningKey(properties.getOauthJwtKey());
        try {
            converter.afterPropertiesSet();
        } catch (Exception e) {
            LOGGER.warn("error.ResourceServerConfiguration.accessTokenConverter {}", e.getMessage());
        }
        return converter;
    }

}
