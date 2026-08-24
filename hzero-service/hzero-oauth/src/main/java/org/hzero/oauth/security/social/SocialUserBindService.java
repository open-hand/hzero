package org.hzero.oauth.security.social;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.TokenUtils;
import org.hzero.oauth.domain.vo.AuthenticationResult;
import org.hzero.starter.social.core.common.connect.SocialUserData;
import org.hzero.starter.social.core.common.constant.ChannelEnum;
import org.hzero.starter.social.core.common.constant.SocialConstant;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;

/**
 * 三方用户绑定服务
 *
 * @author bojiangzhou 2019/11/06
 */
@Component
public class SocialUserBindService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialUserBindService.class);

    private SocialAuthenticationServiceLocator authServiceLocator;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private SocialUserProviderRepository socialUserProviderRepository;

    public AuthenticationResult bindOpenAccount(HttpServletRequest request) {
        String providerId = request.getParameter(SocialConstant.PARAM_PROVIDER);
        String channel = StringUtils.defaultIfBlank(request.getParameter(SocialConstant.PARAM_CHANNEL), ChannelEnum.pc.name());
        String providerUserId = request.getParameter(SocialConstant.PARAM_OPEN_ID);
        String providerUnionId = request.getParameter(SocialConstant.PARAM_UNION_ID);
        String openAccessToken = request.getParameter(SocialConstant.PARAM_OPEN_ACCESS_TOKEN); // 用于验证三方用户已授权

        String accessToken = TokenUtils.getToken(request);

        Assert.notNull(providerId, "param [provider] not be null.");
        Assert.notNull(channel, "param [channel] not be null.");
        Assert.notNull(providerUserId, "param [open_id] not be null.");
        Assert.notNull(openAccessToken, "param [open_access_token] not be null.");
        Assert.notNull(accessToken, "param [access_token] not be null.");

        String uniqueProviderId = Provider.uniqueProviderId(providerId, channel);
        ConnectionData connectionData = new ConnectionData(uniqueProviderId, providerUserId, null, null, null, openAccessToken, null, null, null, providerUnionId);

        Set<String> authProviders = getAuthServiceLocator().registeredAuthenticationProviderIds();

        if (authProviders.isEmpty() || !authProviders.contains(uniqueProviderId)) {
            throw new ProviderNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        SocialAuthenticationService<?> authService = getAuthServiceLocator().getAuthenticationService(uniqueProviderId);
        Connection<?> connection = authService.getConnectionFactory().createConnection(connectionData);
        connection.sync();
        if (!StringUtils.equals(connection.getProviderUnionId(), providerUnionId) && !StringUtils.equals(connection.getKey().getProviderUserId(), providerUserId)) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }
        connectionData = connection.createData();
        SocialUserData socialUserData = new SocialUserData(connectionData);
        OAuth2Authentication authentication = tokenStore.readAuthentication(accessToken);
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            LOGGER.info("bind open user but access_token not found authentication, access_token={}, socialUser={}", accessToken, socialUserData);
            throw new NotLoginException();
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        socialUserProviderRepository.createUserBind(details.getUsername(), providerId, providerUserId, socialUserData);

        return new AuthenticationResult().authenticateSuccess();
    }

    public SocialAuthenticationServiceLocator getAuthServiceLocator() {
        if (this.authServiceLocator == null) {
            this.authServiceLocator = applicationContext.getBean(SocialAuthenticationServiceLocator.class);
            Assert.notNull(this.authServiceLocator,"OpenLoginTokenService depends on SocialAuthenticationServiceLocator. " +
                    "No single bean of that type found in application context.");
        }
        return this.authServiceLocator;
    }

}
