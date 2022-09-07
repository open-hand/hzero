package org.hzero.oauth.security.token;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;

import org.hzero.starter.social.core.common.constant.ChannelEnum;
import org.hzero.starter.social.core.common.constant.SocialConstant;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialProviderRepository;

/**
 *
 * @author bojiangzhou 2019/06/02
 */
public class OpenLoginTokenService extends LoginTokenService implements ApplicationContextAware {

    private SocialAuthenticationServiceLocator authServiceLocator;
    private final SocialProviderRepository socialProviderRepository;
    private ApplicationContext applicationContext;

    public OpenLoginTokenService(TokenGranter tokenGranter,
                                 ClientDetailsService clientDetailsService,
                                 OAuth2RequestFactory oAuth2RequestFactory,
                                 AuthenticationProvider authenticationProvider,
                                 SocialProviderRepository socialProviderRepository) {
        super(tokenGranter, clientDetailsService, oAuth2RequestFactory, authenticationProvider);
        this.socialProviderRepository = socialProviderRepository;
    }

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request) {
        String providerId = request.getParameter(SocialConstant.PARAM_PROVIDER);
        String channel = StringUtils.defaultIfBlank(request.getParameter(SocialConstant.PARAM_CHANNEL), ChannelEnum.pc.name());
        String providerUserId = request.getParameter(SocialConstant.PARAM_OPEN_ID);
        String providerUnionId = request.getParameter(SocialConstant.PARAM_UNION_ID);
        String openAccessToken = request.getParameter(SocialConstant.PARAM_OPEN_ACCESS_TOKEN); // 用于验证三方用户已授权

        Assert.notNull(providerId, "param [provider] not be null.");
        Assert.notNull(channel, "param [channel] not be null.");
        Assert.notNull(providerUserId, "param [open_id] not be null.");
        Assert.notNull(openAccessToken, "param [open_access_token] not be null.");

        String uniqueProviderId = Provider.uniqueProviderId(providerId, channel);
        ConnectionData connectionData = new ConnectionData(uniqueProviderId, providerUserId, null, null, null, openAccessToken, null, null, null, providerUnionId);

        Set<String> authProviders = getAuthServiceLocator().registeredAuthenticationProviderIds();

        if (authProviders.isEmpty() || !authProviders.contains(uniqueProviderId)) {
            throw new ProviderNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        SocialAuthenticationService<?> authService = getAuthServiceLocator().getAuthenticationService(uniqueProviderId);
        Connection connection = authService.getConnectionFactory().createConnection(connectionData);
        connection.sync();
        if (!StringUtils.equals(connection.getProviderUnionId(), providerUnionId) && !StringUtils.equals(connection.getKey().getProviderUserId(), providerUserId)) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }

        return new SocialAuthenticationToken(connection, null);
    }

    public SocialAuthenticationServiceLocator getAuthServiceLocator() {
        if (this.authServiceLocator == null) {
            this.authServiceLocator = applicationContext.getBean(SocialAuthenticationServiceLocator.class);
            Assert.notNull(this.authServiceLocator,"OpenLoginTokenService depends on SocialAuthenticationServiceLocator. " +
                    "No single bean of that type found in application context.");
        }
        return this.authServiceLocator;
    }

    public SocialProviderRepository getSocialProviderRepository() {
        return socialProviderRepository;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }
}
