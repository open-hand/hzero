package org.hzero.oauth.security.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolver;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.session.SessionRepository;

import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.service.BaseUserService;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.boot.oauth.user.UserPostProcessor;
import org.hzero.core.captcha.CaptchaImageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.oauth.domain.repository.ClientRepository;
import org.hzero.oauth.domain.repository.UserRepository;
import org.hzero.oauth.domain.service.AuditLoginService;
import org.hzero.oauth.infra.constant.Constants;
import org.hzero.oauth.security.custom.*;
import org.hzero.oauth.security.resource.ResourceMatcher;
import org.hzero.oauth.security.resource.impl.MobileResourceMatcher;
import org.hzero.oauth.security.service.*;
import org.hzero.oauth.security.service.impl.*;
import org.hzero.oauth.security.util.LoginUtil;

/**
 * Oauth 服务配置
 *
 * @author bojiangzhou 2018/08/02
 */
@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
public class SecurityConfiguration {

    @Autowired
    private CaptchaImageHelper captchaImageHelper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    @Autowired
    private BaseClientRepository baseClientRepository;
    @Autowired
    private PasswordErrorTimesService passwordErrorTimesService;
    @Autowired
    private PasswordPolicyManager passwordPolicyManager;

    @Autowired
    private SessionRepository<?> sessionRepository;
    @Autowired
    private AuditLoginService auditLoginService;

    @Bean
    @ConditionalOnMissingBean(ResourceMatcher.class)
    @ConditionalOnProperty(prefix = SecurityProperties.PREFIX, name = "custom-resource-matcher", havingValue = "true")
    public ResourceMatcher resourceMatcher() {
        return new MobileResourceMatcher();
    }

    /**
     * 用户账户业务服务
     */
    @Bean
    @ConditionalOnMissingBean(UserAccountService.class)
    public UserAccountService userAccountService() {
        return new DefaultUserAccountService(this.userRepository, this.baseUserService, this.passwordPolicyManager,
                this.basePasswordPolicyRepository, this.baseClientRepository, this.securityProperties);
    }

    /**
     * 登录记录业务服务
     */
    @Bean
    @ConditionalOnMissingBean(LoginRecordService.class)
    public LoginRecordService loginRecordService() {
        return new DefaultLoginRecordService(baseUserService, passwordErrorTimesService, basePasswordPolicyRepository, redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsWrapper.class)
    public UserDetailsWrapper userDetailsWrapper() {
        return new DefaultUserDetailsWrapper(userRepository, redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(ClientDetailsWrapper.class)
    public ClientDetailsWrapper clientDetailsWrapper(ClientRepository clientRepository) {
        return new DefaultClientDetailsWrapper(clientRepository);
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsBuilder.class)
    public UserDetailsBuilder userDetailsBuilder(UserDetailsWrapper userDetailsWrapper,
                                                 @Autowired(required = false) List<UserPostProcessor> userPostProcessorList) {
        return new DefaultUserDetailsBuilder(userDetailsWrapper, userAccountService(), userPostProcessorList);
    }

    //
    // provider configuration
    // ------------------------------------------------------------------------------

    @Bean
    @ConditionalOnMissingBean(CustomAuthenticationDetailsSource.class)
    public CustomAuthenticationDetailsSource customAuthenticationDetailsSource() {
        return new CustomAuthenticationDetailsSource(captchaImageHelper);
    }

    @Bean
    @ConditionalOnMissingBean(CustomAuthenticationSuccessHandler.class)
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean(CustomAuthenticationFailureHandler.class)
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(loginRecordService(), securityProperties, auditLoginService);
    }

    @Bean
    @ConditionalOnMissingBean(CustomLogoutSuccessHandler.class)
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(tokenStore(), loginRecordService(), securityProperties, userAccountService());
    }

    @Bean
    @ConditionalOnMissingBean(CustomAuthenticationProvider.class)
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public UserDetailsService userDetailsService(UserAccountService userAccountService,
                                                 UserDetailsBuilder userDetailsBuilder,
                                                 LoginRecordService loginRecordService) {
        return new CustomUserDetailsService(userAccountService, userDetailsBuilder, loginRecordService);
    }

    @Bean
    @ConditionalOnMissingBean(CustomClientDetailsService.class)
    // @ConditionalOnMissingBean(ClientDetailsService.class) TODO why?
    public CustomClientDetailsService customClientDetailsService() {
        return new CustomClientDetailsService(baseClientRepository);
    }

    @Bean
    @ConditionalOnMissingBean(BearerTokenExtractor.class)
    public BearerTokenExtractor bearerTokenExtractor() {
        return new CustomBearerTokenExtractor();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationKeyGenerator.class)
    public AuthenticationKeyGenerator authenticationKeyGenerator() {
        return new CustomAuthenticationKeyGenerator(loginUtil);
    }

    @Bean
    @ConditionalOnMissingBean(TokenStore.class)
    public TokenStore tokenStore() {
        CustomRedisTokenStore redisTokenStore = new CustomRedisTokenStore(redisConnectionFactory, loginUtil, sessionRepository,
                securityProperties.isAccessTokenAutoRenewal());
        redisTokenStore.setAuthenticationKeyGenerator(authenticationKeyGenerator());
        redisTokenStore.setPrefix(Constants.CacheKey.ACCESS_TOKEN);
        return redisTokenStore;
    }


    @Bean
    public PortMapper portMapper() {
        PortMapperImpl portMapper = new PortMapperImpl();
        Map<String, String> portMap = securityProperties.getPortMapper().stream()
                .collect(Collectors.toMap(m -> String.valueOf(m.getSourcePort()), m -> String.valueOf(m.getMappingPort())));
        portMapper.setPortMappings(portMap);
        return portMapper;
    }

    @Bean
    public PortResolver portResolver() {
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper());
        return portResolver;
    }
}
