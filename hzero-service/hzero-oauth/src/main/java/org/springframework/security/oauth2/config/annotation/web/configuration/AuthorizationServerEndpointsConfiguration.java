/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.springframework.security.oauth2.config.annotation.web.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration.TokenKeyEndpointRegistrar;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.*;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

/**
 * @author Dave Syer
 *
 */
@Configuration
@Import(TokenKeyEndpointRegistrar.class)
public class AuthorizationServerEndpointsConfiguration {

	private AuthorizationServerEndpointsConfigurer endpoints = new AuthorizationServerEndpointsConfigurer();

	@Autowired
	private List<AuthorizationServerConfigurer> configurers = Collections.emptyList();

	@PostConstruct
	public void init() {
		for (AuthorizationServerConfigurer configurer : configurers) {
			try {
				configurer.configure(endpoints);
			} catch (Exception e) {
				throw new IllegalStateException("Cannot configure enpdoints", e);
			}
		}
	}

	@Bean
	public AuthorizationEndpoint authorizationEndpoint() throws Exception {
		AuthorizationEndpoint authorizationEndpoint = new AuthorizationEndpoint();
		FrameworkEndpointHandlerMapping mapping = getEndpointsConfigurer().getFrameworkEndpointHandlerMapping();
		authorizationEndpoint.setUserApprovalPage(extractPath(mapping, "/oauth/confirm_access"));
		authorizationEndpoint.setProviderExceptionHandler(exceptionTranslator());
		authorizationEndpoint.setErrorPage(extractPath(mapping, "/oauth/error"));
		authorizationEndpoint.setTokenGranter(tokenGranter());
		authorizationEndpoint.setClientDetailsService(endpoints.getClientDetailsService());
		authorizationEndpoint.setAuthorizationCodeServices(authorizationCodeServices());
		authorizationEndpoint.setOAuth2RequestFactory(oauth2RequestFactory());
		authorizationEndpoint.setOAuth2RequestValidator(oauth2RequestValidator());
		authorizationEndpoint.setUserApprovalHandler(userApprovalHandler());
		authorizationEndpoint.setRedirectResolver(redirectResolver());
		return authorizationEndpoint;
	}

	@Bean
	public TokenEndpoint tokenEndpoint() throws Exception {
		TokenEndpoint tokenEndpoint = new TokenEndpoint();
		tokenEndpoint.setClientDetailsService(endpoints.getClientDetailsService());
		tokenEndpoint.setProviderExceptionHandler(exceptionTranslator());
		tokenEndpoint.setTokenGranter(tokenGranter());
		tokenEndpoint.setOAuth2RequestFactory(oauth2RequestFactory());
		tokenEndpoint.setOAuth2RequestValidator(oauth2RequestValidator());
		tokenEndpoint.setAllowedRequestMethods(allowedTokenEndpointRequestMethods());
		return tokenEndpoint;
	}

	@Bean
	public CheckTokenEndpoint checkTokenEndpoint() {
		CheckTokenEndpoint endpoint = new CheckTokenEndpoint(getEndpointsConfigurer().getResourceServerTokenServices());
		endpoint.setAccessTokenConverter(getEndpointsConfigurer().getAccessTokenConverter());
		endpoint.setExceptionTranslator(exceptionTranslator());
		return endpoint;
	}

	@Bean
	public WhitelabelApprovalEndpoint whitelabelApprovalEndpoint() {
		return new WhitelabelApprovalEndpoint();
	}

	@Bean
	public WhitelabelErrorEndpoint whitelabelErrorEndpoint() {
		return new WhitelabelErrorEndpoint();
	}

	@Bean
	public FrameworkEndpointHandlerMapping oauth2EndpointHandlerMapping() throws Exception {
		return getEndpointsConfigurer().getFrameworkEndpointHandlerMapping();
	}

	@Bean
	public FactoryBean<ConsumerTokenServices> consumerTokenServices() throws Exception {
		return new AbstractFactoryBean<ConsumerTokenServices>() {

			@Override
			public Class<?> getObjectType() {
				return ConsumerTokenServices.class;
			}

			@Override
			protected ConsumerTokenServices createInstance() throws Exception {
				return getEndpointsConfigurer().getConsumerTokenServices();
			}
		};
	}

	/**
	 * This needs to be a <code>@Bean</code> so that it can be
	 * <code>@Transactional</code> (in case the token store supports them). If
	 * you are overriding the token services in an
	 * {@link AuthorizationServerConfigurer} consider making it a
	 * <code>@Bean</code> for the same reason (assuming you need transactions,
	 * e.g. for a JDBC token store).
	 * 
	 * @return an AuthorizationServerTokenServices
	 */
	@Bean
	public FactoryBean<AuthorizationServerTokenServices> defaultAuthorizationServerTokenServices() {
		return new AuthorizationServerTokenServicesFactoryBean(endpoints);
	}

	public AuthorizationServerEndpointsConfigurer getEndpointsConfigurer() {
		if (!endpoints.isTokenServicesOverride()) {
			try {
				endpoints.tokenServices(endpoints.getDefaultAuthorizationServerTokenServices());
			}
			catch (Exception e) {
				throw new BeanCreationException("Cannot create token services", e);
			}
		}
		return endpoints;
	}

	private Set<HttpMethod> allowedTokenEndpointRequestMethods() {
		return getEndpointsConfigurer().getAllowedTokenEndpointRequestMethods();
	}

	private OAuth2RequestFactory oauth2RequestFactory() throws Exception {
		return getEndpointsConfigurer().getOAuth2RequestFactory();
	}

	private UserApprovalHandler userApprovalHandler() throws Exception {
		return getEndpointsConfigurer().getUserApprovalHandler();
	}

	private OAuth2RequestValidator oauth2RequestValidator() throws Exception {
		return getEndpointsConfigurer().getOAuth2RequestValidator();
	}

	private AuthorizationCodeServices authorizationCodeServices() throws Exception {
		return getEndpointsConfigurer().getAuthorizationCodeServices();
	}

	private WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator() {
		return getEndpointsConfigurer().getExceptionTranslator();
	}

	private RedirectResolver redirectResolver() {
		return getEndpointsConfigurer().getRedirectResolver();
	}

	private TokenGranter tokenGranter() throws Exception {
		return getEndpointsConfigurer().getTokenGranter();
	}

	private String extractPath(FrameworkEndpointHandlerMapping mapping, String page) {
		String path = mapping.getPath(page);
		if (path.contains(":")) {
			return path;
		}
		return "forward:" + path;
	}

	protected static class AuthorizationServerTokenServicesFactoryBean
			extends AbstractFactoryBean<AuthorizationServerTokenServices> {

		private AuthorizationServerEndpointsConfigurer endpoints;
		
		protected AuthorizationServerTokenServicesFactoryBean() {
		}

		public AuthorizationServerTokenServicesFactoryBean(
				AuthorizationServerEndpointsConfigurer endpoints) {
					this.endpoints = endpoints;
		}

		@Override
		public Class<?> getObjectType() {
			return AuthorizationServerTokenServices.class;
		}

		@Override
		protected AuthorizationServerTokenServices createInstance() throws Exception {
			return endpoints.getDefaultAuthorizationServerTokenServices();
		}
	}

	@Component
	protected static class TokenKeyEndpointRegistrar implements BeanDefinitionRegistryPostProcessor {

		private BeanDefinitionRegistry registry;

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,
					JwtAccessTokenConverter.class, false, false);
			if (names.length > 0) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TokenKeyEndpoint.class);
				builder.addConstructorArgReference(names[0]);
				registry.registerBeanDefinition(TokenKeyEndpoint.class.getName(), builder.getBeanDefinition());
			}
		}

		@Override
		public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
			this.registry = registry;
		}

	}

}
