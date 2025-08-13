package org.hzero.gateway.helper.service.impl;

import io.choerodon.core.oauth.CustomClientDetails;
import io.choerodon.core.oauth.CustomUserDetails;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.TokenConstants;
import org.hzero.core.util.CommonExecutor;
import org.hzero.gateway.helper.config.RedisAuthenticateProperties;
import org.hzero.gateway.helper.domain.CustomUserDetailsWithResult;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.token.ReadonlyTokenServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 废柴 2020/8/6 11:23
 */
@Service
@EnableConfigurationProperties(RedisAuthenticateProperties.class)
public class RedisGetUserDetailsServiceImpl extends GetUserDetailsServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(RedisGetUserDetailsServiceImpl.class);
    private static final String TOKEN_PREFIX = TokenConstants.HEADER_BEARER.toLowerCase() + " ";
    private final ReadonlyTokenServices tokenServices;
    private final RedisAuthenticateProperties redisAuthenticateProperties;
    private final ThreadPoolExecutor executor;


    public RedisGetUserDetailsServiceImpl(@Qualifier("helperRestTemplate") RestTemplate restTemplate,
                                          DiscoveryClient discoveryClient,
                                          ReadonlyTokenServices tokenServices,
                                          RedisAuthenticateProperties redisAuthenticateProperties) {
        super(restTemplate, discoveryClient);
        this.tokenServices = tokenServices;
        this.redisAuthenticateProperties = redisAuthenticateProperties;
        this.executor = CommonExecutor.buildThreadFirstExecutor(redisAuthenticateProperties.getCorePoolSize(),
                redisAuthenticateProperties.getMaximumPoolSize(),
                redisAuthenticateProperties.getKeepAliveTime().getSeconds(),
                TimeUnit.SECONDS,
                redisAuthenticateProperties.getQueueCapacity(),
                redisAuthenticateProperties.getCallerName());
    }

    @Override
    public CustomUserDetailsWithResult getUserDetails(String accessToken) {
        if (!redisAuthenticateProperties.isEnable()) {
            return super.getUserDetails(accessToken);
        }
        try {
            OAuth2Authentication oAuth2Authentication = tokenServices.loadAuthentication(removePrefix(accessToken));
            if (oAuth2Authentication != null
                    && oAuth2Authentication.isAuthenticated()
                    && oAuth2Authentication.getPrincipal() != null) {
                if (redisAuthenticateProperties.isCallOauthServiceOnSuccess()) {
                    callOAuth(accessToken);
                }
                Object principal = oAuth2Authentication.getPrincipal();
                if (principal instanceof CustomUserDetails) {
                    return new CustomUserDetailsWithResult(writeClient2User((CustomUserDetails) principal, oAuth2Authentication.getOAuth2Request()), CheckState.SUCCESS_PASS_SITE);
                }
                if (principal instanceof CustomClientDetails) {
                    return new CustomUserDetailsWithResult(convertClient2User((CustomClientDetails) principal), CheckState.SUCCESS_PASS_SITE);
                }
            }
        } catch (Exception e) {
            logger.error("[AccessToken] An error occurred while obtaining user information.", e);
            if (redisAuthenticateProperties.isCallOauthServiceOnExpired()) {
                callOAuth(accessToken);
            }
            return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
                    "Access_token is expired or invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
        }
        if (redisAuthenticateProperties.isCallOauthServiceOnFailure()) {
            callOAuth(accessToken);
        }
        return new CustomUserDetailsWithResult(CheckState.PERMISSION_ACCESS_TOKEN_EXPIRED,
                "Access_token is expired or invalid, Please re-login and set correct access_token by HTTP header 'Authorization'");
    }

    private CustomUserDetails writeClient2User(CustomUserDetails user, OAuth2Request oAuth2Request) {
        if (oAuth2Request != null) {
            user.setClientName(oAuth2Request.getClientId());
        }
        return user;
    }

    private CustomUserDetails convertClient2User(CustomClientDetails clientDetails) {
        CustomUserDetails user = new CustomUserDetails(BaseConstants.ANONYMOUS_USER_NAME, "unknown password", Collections.emptyList());
        user.setUserId(BaseConstants.ANONYMOUS_USER_ID);
        user.setClientId(clientDetails.getId());
        user.setLanguage(ANONYMOUS_LANGUAGE);
        user.setClientScope(clientDetails.getScope());
        user.setOrganizationId(clientDetails.getOrganizationId());
        user.setRoleId(clientDetails.getCurrentRoleId());
        user.setTenantId(clientDetails.getCurrentTenantId());
        user.setRoleIds(clientDetails.getRoleIds());
        user.setTenantIds(clientDetails.getTenantIds());
        user.setClientName(clientDetails.getClientId());
        user.setClientResourceIds(clientDetails.getResourceIds());
        user.setClientAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
        user.setClientRegisteredRedirectUri(clientDetails.getRegisteredRedirectUri());
        user.setClientAutoApproveScopes(clientDetails.getAutoApproveScopes());
        user.setClientAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
        user.setClientRefreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds());
        user.setTimeZone(StringUtils.hasText(clientDetails.getTimeZone()) ? clientDetails.getTimeZone() : ANONYMOUS_TIME_ZONE);
        user.setApiEncryptFlag(clientDetails.getApiEncryptFlag());
        user.setApiReplayFlag(clientDetails.getApiReplayFlag());
        return user;
    }

    private String removePrefix(String accessToken) {
        return accessToken.toLowerCase().startsWith(TOKEN_PREFIX) ? accessToken.substring(TOKEN_PREFIX.length()) : accessToken;
    }

    public void callOAuth(String accessToken) {
        executor.submit(() -> super.getUserDetailsImplicit(accessToken));
    }
}

