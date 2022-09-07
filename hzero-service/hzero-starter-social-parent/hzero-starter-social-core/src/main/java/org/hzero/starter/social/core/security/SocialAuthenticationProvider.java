/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hzero.starter.social.core.security;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.util.Assert;

import org.hzero.starter.social.core.common.connect.SocialUserData;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.exception.UserUnbindException;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;

/**
 * 自定义规则获取用户信息
 *
 * @author bojiangzhou 2019/08/30
 */
public abstract class SocialAuthenticationProvider implements AuthenticationProvider {

    private SocialUserProviderRepository userProviderRepository;
    private SocialUserDetailsService userDetailsService;

    public SocialAuthenticationProvider(SocialUserProviderRepository userProviderRepository,
                                        SocialUserDetailsService userDetailsService) {
        this.userProviderRepository = userProviderRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Authenticate user based on {@link SocialAuthenticationToken}
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, "unsupported authentication type");
        Assert.isTrue(!authentication.isAuthenticated(), "already authenticated");
        SocialAuthenticationToken authToken = (SocialAuthenticationToken) authentication;

        Connection<?> connection = authToken.getConnection();
        String providerId = Provider.realProviderId(authToken.getProviderId());
        String providerUserId = connection.getKey().getProviderUserId();
        String providerUnionId = connection.createData().getProviderUnionId();

        // 通过 providerId 查询
        String username = toUsernameByProviderUserId(providerId, providerUserId, authToken);
        if (StringUtils.isBlank(username)) {
            // 通过 unionId 查询
            username = toUsernameAndAutoBindByUnionId(providerId, providerUnionId, connection);
            if (StringUtils.isBlank(username)) {
                throw new UserUnbindException(SocialErrorCode.PROVIDER_NOT_BIND);
            }
        }

        UserDetails userDetails = retrieveUser(username, authToken);
        if (userDetails == null) {
            throw new UsernameNotFoundException(SocialErrorCode.USER_NOT_FOUND);
        }

        return createSuccessAuthentication(connection, userDetails, authToken);
    }

    /**
     * 处理用户信息
     *
     * @param username 用户名
     * @param authentication SocialAuthenticationToken
     */
    protected abstract UserDetails retrieveUser(String username, SocialAuthenticationToken authentication) throws AuthenticationException;

    /**
     * 获取系统绑定的用户ID
     * 
     * @param providerId 三方应用
     * @param providerUserId 三方应用账户
     * @param authentication SocialAuthenticationToken
     * @return 绑定的用户名
     */
    protected String toUsernameByProviderUserId(String providerId, String providerUserId, SocialAuthenticationToken authentication) {
        if (StringUtils.isBlank(providerUserId)) {
            return null;
        }
        String username = userProviderRepository.findUsernameByProviderId(providerId, providerUserId);
        return StringUtils.defaultIfBlank(username, null);
    }

    /**
     * 通过 unionId 获取用户，不存在则绑定自动根据 unionId 绑定
     *
     * @param providerId 三方应用
     * @param providerUnionId 三方应用账户 unionId
     * @param connection Connection
     * @return 绑定的用户名
     */
    protected String toUsernameAndAutoBindByUnionId(String providerId, String providerUnionId, Connection<?> connection) {
        if (StringUtils.isBlank(providerUnionId)) {
            return null;
        }
        List<String> usernames = userProviderRepository.findUsernameByUnionId(providerId, providerUnionId);
        if (CollectionUtils.isEmpty(usernames)) {
            return null;
        }
        if (usernames.stream().distinct().count() < usernames.size()) {
            // 同一个 username 不可能绑定多个相同的 union_id
            throw new IllegalStateException("one user bind multi different union_id.");
        }
        String username = usernames.get(0);
        ConnectionData data = connection.createData();
        if (StringUtils.isBlank(data.getDisplayName())) {
            throw new IllegalArgumentException("social user's open_name should not be null.");
        }

        SocialUserData socialUser = new SocialUserData(data);
        userProviderRepository.createUserBind(username, providerId, connection.getKey().getProviderUserId(), socialUser);

        return username;
    }

    protected Authentication createSuccessAuthentication(Connection<?> connection, UserDetails userDetails, SocialAuthenticationToken authToken) {
        return new SocialAuthenticationToken(connection, userDetails, authToken.getProviderAccountData(), userDetails.getAuthorities());
    }

    public SocialUserProviderRepository getUserProviderRepository() {
        return userProviderRepository;
    }

    public SocialUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }
}
