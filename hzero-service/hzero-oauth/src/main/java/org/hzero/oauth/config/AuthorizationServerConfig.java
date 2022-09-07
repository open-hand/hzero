package org.hzero.oauth.config;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.custom.CustomClientDetailsService;
import org.hzero.oauth.security.custom.CustomRedirectResolver;
import org.hzero.oauth.security.custom.granter.CustomAuthorizationCodeTokenGranter;
import org.hzero.oauth.security.custom.granter.CustomClientCredentialsTokenGranter;
import org.hzero.oauth.security.service.ClientDetailsWrapper;

/**
 * @author bojiangzhou
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomClientDetailsService customClientDetailsService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private ClientDetailsWrapper clientDetailsWrapper;

    /**
     * 用来配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)。
     * <p>
     * authenticationManager: 注入一个AuthenticationManager后，password grant将打开
     * userDetailsService: 如果注入了一个UserDetailsService,refresh token grant将对用户状态进行校验，以保证用户处于激活状态
     * authorizationCodeServices: 这个属性是用来设置授权码服务的（即 AuthorizationCodeServices 的实例对象），主要用于 "authorization_code" 授权码类型模式。
     * CustomTokenStore extends JdbcTokenStore: 令牌会被保存进关系型数据库
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource))
                .tokenStore(tokenStore)
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .redirectResolver(new CustomRedirectResolver())
                .setClientDetailsService(customClientDetailsService)
        ;

        endpoints.tokenGranter(tokenGranter(endpoints));
    }

    /**
     * 配置客户端详情服务，客户端详情信息在这里进行初始化
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(customClientDetailsService);
    }

    /**
     * 用来配置令牌端点(Token Endpoint)的安全约束
     * allowFormAuthenticationForClients:为了注册 clientCredentialsTokenEndpointFilter
     * ( clientCredentialsTokenEndpointFilter:
     * 解析request中的client_id和client_secret;构造成UsernamePasswordAuthenticationToken,
     * 然后通过UserDetailsService查询作简单的认证,一般是针对password模式和client_credentials
     * )
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        return new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(getDefaultTokenGranters(endpoints));
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
    }

    private List<TokenGranter> getDefaultTokenGranters(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> tokenGranters = new ArrayList<>();
        // 使用自定义的 AuthorizationCodeTokenGranter
        // 不检查 clientId 一致性
        if (securityProperties.isNotCheckClientEquals()) {
            tokenGranters.add(new CustomAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                    endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        } else {
            tokenGranters.add(new AuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                    endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        }

        tokenGranters.add(new RefreshTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));

        tokenGranters.add(new ImplicitTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));

        ClientCredentialsTokenGranter credentialsTokenGranter = new CustomClientCredentialsTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), clientDetailsWrapper);

        credentialsTokenGranter.setAllowRefresh(securityProperties.isCredentialsAllowRefresh());
        tokenGranters.add(credentialsTokenGranter);

        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, endpoints.getTokenServices(),
                    endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        }
        return tokenGranters;
    }
}
