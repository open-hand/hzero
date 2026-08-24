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

import static org.hzero.starter.social.core.common.constant.SocialConstant.*;
import static org.hzero.starter.social.core.exception.SocialErrorCode.OPEN_ID_ALREADY_BIND_OTHER_USER;
import static org.hzero.starter.social.core.exception.SocialErrorCode.USER_ALREADY_BIND;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.security.SocialAuthenticationFailureHandler;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import org.hzero.core.util.TokenUtils;
import org.hzero.starter.social.core.common.connect.SocialUserData;
import org.hzero.starter.social.core.common.constant.ChannelEnum;
import org.hzero.starter.social.core.common.constant.SocialConstant;
import org.hzero.starter.social.core.exception.RejectAuthorizationException;
import org.hzero.starter.social.core.exception.UserBindException;
import org.hzero.starter.social.core.exception.UserUnbindException;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialProviderRepository;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;
import org.hzero.starter.social.core.security.holder.SocialSessionHolder;

/**
 * 跳转认证地址：/open/qq
 * 回调地址：/open/qq/callback，
 * 通过 Provider 返回三方应用信息
 *
 * @author bojiangzhou 2019/08/30
 */
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocialAuthenticationFilter.class);

    private static final String DEFAULT_FAILURE_URL = "/login";
    private static final String DEFAULT_FILTER_PROCESSES_URL = "/open/**";
    private static final String DEFAULT_BIND_URL = "/bind";

    private String bindUrl = DEFAULT_BIND_URL;
    private boolean attemptBind = false;
    private boolean updateBind = true;
    private boolean enableHttps = false;
    private String filterProcessesUrl = DEFAULT_FILTER_PROCESSES_URL;
    private String filterProcessesUrlPrefix;

    private TokenStore tokenStore;
    private SocialAuthenticationServiceLocator authServiceLocator;
    private SimpleUrlAuthenticationFailureHandler delegateAuthenticationFailureHandler;
    private SocialUserProviderRepository userProviderRepository;
    private SocialProviderRepository socialProviderRepository;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    public SocialAuthenticationFilter(AuthenticationManager authManager,
                                      TokenStore tokenStore,
                                      SocialUserProviderRepository userProviderRepository,
                                      SocialAuthenticationServiceLocator authServiceLocator,
                                      SocialProviderRepository socialProviderRepository) {
        super(DEFAULT_FILTER_PROCESSES_URL);
        filterProcessesUrlPrefix = DEFAULT_FILTER_PROCESSES_URL.replace("/**", "");
        setAuthenticationManager(authManager);
        this.tokenStore = tokenStore;
        this.authServiceLocator = authServiceLocator;
        this.socialProviderRepository = socialProviderRepository;
        this.userProviderRepository = userProviderRepository;

        this.delegateAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler(DEFAULT_FAILURE_URL);
        super.setAuthenticationFailureHandler(new SocialAuthenticationFailureHandler(delegateAuthenticationFailureHandler));
    }

    /**
     * bind user page
     */
    public void setBindUrl(String bindUrl) {
        this.bindUrl = bindUrl;
    }

    /**
     * if redirect to bind page when user not bind.
     */
    public void setAttemptBind(boolean attemptBind) {
        this.attemptBind = attemptBind;
    }

    /**
     * if update bind providerUserId
     */
    public void setUpdateBind(boolean updateBind) {
        this.updateBind = updateBind;
    }

    /**
     * if enable https
     */
    public void setEnableHttps(boolean enableHttps) {
        this.enableHttps = enableHttps;
    }

    /**
     * The URL to redirect to if authentication fails or if authorization is denied by the user.
     * 
     * @param defaultFailureUrl The failure URL. Defaults to "/signin" (relative to the servlet context).
     */
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        delegateAuthenticationFailureHandler.setDefaultFailureUrl(defaultFailureUrl);
    }

    public void setPostLoginUrl(String postLoginUrl) {
        AuthenticationSuccessHandler successHandler = getSuccessHandler();
        if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
            AbstractAuthenticationTargetUrlRequestHandler h = (AbstractAuthenticationTargetUrlRequestHandler) successHandler;
            h.setDefaultTargetUrl(postLoginUrl);
        } else {
            throw new IllegalStateException("can't set postLoginUrl on unknown successHandler, type is " + successHandler.getClass().getName());
        }
    }

    public void setAlwaysUsePostLoginUrl(boolean alwaysUsePostLoginUrl) {
        AuthenticationSuccessHandler successHandler = getSuccessHandler();
        if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
            AbstractAuthenticationTargetUrlRequestHandler h = (AbstractAuthenticationTargetUrlRequestHandler) successHandler;
            h.setAlwaysUseDefaultTargetUrl(alwaysUsePostLoginUrl);
        } else {
            throw new IllegalStateException("can't set alwaysUsePostLoginUrl on unknown successHandler, type is " + successHandler.getClass().getName());
        }
    }

    public void setPostFailureUrl(String postFailureUrl) {
        AuthenticationFailureHandler failureHandler = getFailureHandler();
        if (failureHandler instanceof SimpleUrlAuthenticationFailureHandler) {
            SimpleUrlAuthenticationFailureHandler h = (SimpleUrlAuthenticationFailureHandler) failureHandler;
            h.setDefaultFailureUrl(postFailureUrl);
        } else {
            throw new IllegalStateException("can't set postFailureUrl on unknown failureHandler, type is " + failureHandler.getClass().getName());
        }
    }

    /**
     * Sets a strategy to use when persisting information that is to survive past the boundaries of a
     * request. The default strategy is to set the data as attributes in the HTTP Session.
     * 
     * @param sessionStrategy the session strategy.
     */
    public void setSessionStrategy(SessionStrategy sessionStrategy) {
        this.sessionStrategy = sessionStrategy;
    }

    public SocialProviderRepository getSocialProviderRepository() {
        return socialProviderRepository;
    }

    public SocialAuthenticationServiceLocator getAuthServiceLocator() {
        return authServiceLocator;
    }

    /**
     * 判断是否拦截请求 - 请求认证地址：/open/qq?channel=pc - 回调地址：/open/qq/callback?xxx
     */
    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!super.requiresAuthentication(request, response)) {
            return false;
        }
        String providerId = getRequestedProviderId(request);
        if (providerId == null) {
            return false;
        }
        Set<String> authProviders = getAuthServiceLocator().registeredAuthenticationProviderIds();

        return !authProviders.isEmpty() && authProviders.contains(providerId);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (detectRejection(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("A rejection was detected. Failing authentication.");
            }
            throw new RejectAuthorizationException("Authentication failed because user rejected authorization.");
        }
        String providerId = getRequestedProviderId(request);

        SocialAuthenticationService<?> authService = getAuthServiceLocator().getAuthenticationService(providerId);
        Authentication auth = attemptAuthService(authService, request, response);
        if (auth == null) {
            throw new AuthenticationServiceException("authentication failed");
        }

        return auth;
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
        this.filterProcessesUrl = filterProcessesUrl;
        this.filterProcessesUrlPrefix = filterProcessesUrl.replace("/**", "");
    }

    /**
     * 判断用户是否拒绝授权，只有回调时才有这种情况
     */
    protected boolean detectRejection(HttpServletRequest request) {
        String uri = request.getRequestURI();
        Set<?> parameterKeys = request.getParameterMap().keySet();
        // 回调接口
        if (uri.contains(DEFAULT_CALLBACK_SUFFIX)) {
            return parameterKeys.size() > 0
                    && !parameterKeys.contains("oauth_token")
                    && !parameterKeys.contains("code")
                    && !parameterKeys.contains("scope");
        }
        return false;
    }

    protected Authentication attemptAuthService(final SocialAuthenticationService<?> authService, final HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!callbackRequest(request)) {
            // state 参数
            String state = UUID.randomUUID().toString();
            request.setAttribute(PARAM_STATE, state);
            // 缓存用户 access_token
            String accessToken = TokenUtils.getToken(request);
            if (StringUtils.isNotBlank(accessToken)) {
                SocialSessionHolder.add(request, PREFIX_ACCESS_TOKEN, state, accessToken);
            }
            String bindRedirectUrl = request.getParameter(PARAM_BIND_REDIRECT_URI);
            if (StringUtils.isNotBlank(bindRedirectUrl)) {
                SocialSessionHolder.add(request, PREFIX_REDIRECT_URL, state, bindRedirectUrl);
            }
        }

        request.setAttribute("enableHttps", enableHttps);
        final SocialAuthenticationToken token = authService.getAuthToken(request, response);
        if (token == null) {
            return null;
        }

        Assert.notNull(token.getConnection(), "token connection is null.");

        Authentication auth = getAuthentication(request);
        if (auth == null || !auth.isAuthenticated()) {
            // 用户未登录，校验三方账号绑定的本地账号并进行登录
            return doAuthentication(authService, request, token);
        } else {
            // 用户已登录，绑定当前登录账号
            addConnection(authService, request, token, auth);
            return null;
        }
    }

    protected Authentication doAuthentication(SocialAuthenticationService<?> authService, HttpServletRequest request, SocialAuthenticationToken token) {
        try {
            if (!authService.getConnectionCardinality().isAuthenticatePossible()) {
                return null;
            }

            token.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication success = getAuthenticationManager().authenticate(token);
            Assert.isInstanceOf(UserDetails.class, success.getPrincipal(), "unexpected principle type");
            return success;
        } catch (UserUnbindException e) {
            if (attemptBind && bindUrl != null) {
                // tokenStore ConnectionData in session and redirect to bind page
                ProviderBindHelper.setConnection(request, token.getConnection());
                //sessionStrategy.setAttribute(new ServletWebRequest(request), ProviderBindHelper.SESSION_ATTRIBUTE, new ProviderBindHelper(token.getConnection()));
                throw new SocialAuthenticationRedirectException(buildBindUrl(request));
            }
            throw e;
        }
    }

    protected Authentication getAuthentication(HttpServletRequest request) {
        if (callbackRequest(request)) {
            String state = request.getParameter(PARAM_STATE);
            request.setAttribute(PARAM_STATE, state);
            String accessToken = SocialSessionHolder.get(request, PREFIX_ACCESS_TOKEN, state);
            if (StringUtils.isNotBlank(accessToken)) {
                Authentication authentication = tokenStore.readAuthentication(accessToken);
                if (authentication != null) {
                    authentication.setAuthenticated(true);
                    return authentication;
                }
            }
        }
        return SecurityContextHolder.getContext().getAuthentication();
    }

    protected void addConnection(final SocialAuthenticationService<?> authService, HttpServletRequest request, SocialAuthenticationToken token, Authentication auth) {
        Object o = auth.getPrincipal();
        String userId = null;
        if (o instanceof UserDetails) {
            UserDetails self = (UserDetails) o;
            userId = self.getUsername();
        }
        ConnectionData connectionData = token.getConnection().createData();
        if (userId == null || connectionData == null) {
            return;
        }

        Connection<?> connection = addConnection(authService, userId, connectionData);
        String redirectUrl = SocialSessionHolder.remove(request, PREFIX_REDIRECT_URL, request.getParameter(PARAM_STATE));
        if (redirectUrl == null) {
            // use default instead
            redirectUrl = authService.getConnectionAddedRedirectUrl(request, connection);
        }
        throw new SocialAuthenticationRedirectException(redirectUrl);
    }

    protected Connection<?> addConnection(SocialAuthenticationService<?> authService, String userId, ConnectionData data) {
        String username = userProviderRepository.findUsernameByProviderId(data.getProviderId(), data.getProviderUserId());
        if (username != null && StringUtils.equals(username, userId)) {
            // already bind
            throw new UserBindException(USER_ALREADY_BIND);
        }
        if (username != null && !StringUtils.equals(username, userId)) {
            // already bind other user
            throw new UserBindException(OPEN_ID_ALREADY_BIND_OTHER_USER);
        }

        List<SocialUserData> providerUsers = userProviderRepository.findProviderUser(data.getProviderId(), userId);
        if (CollectionUtils.isNotEmpty(providerUsers)) {
            // 如果 provider 下没有 unionId 或 只要有一个 unionId 相同就认为已绑定
            if (providerUsers.stream().allMatch(p -> StringUtils.isBlank(p.getProviderUnionId()))
                    || providerUsers.stream().anyMatch(p -> StringUtils.equals(p.getProviderUnionId(), data.getProviderUnionId()))) {
                // already bind
                throw new UserBindException(USER_ALREADY_BIND);
            }
        }

        // bind
        SocialUserData socialUserData = new SocialUserData(data);
        userProviderRepository.createUserBind(userId, data.getProviderId(), data.getProviderUserId(), socialUserData);

        Connection<?> connection = authService.getConnectionFactory().createConnection(data);
        connection.sync();
        return connection;
    }

    protected String getRequestedProviderId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // uri must start with context path
        uri = uri.substring(request.getContextPath().length());
        // remaining uri must start with filterProcessesUrl
        if (!uri.startsWith(filterProcessesUrlPrefix)) {
            return null;
        }
        uri = uri.substring(filterProcessesUrlPrefix.length());

        // /filterProcessesUrl/providerId/callback
        String providerId;
        if (uri.startsWith("/")) {
            providerId = StringUtils.split(uri, "/")[0];
        } else {
            return null;
        }
        String channel = request.getParameter(SocialConstant.PARAM_CHANNEL);
        if (StringUtils.isBlank(channel)) {
            channel = ChannelEnum.pc.name();
            logger.warn("Param channel is blank, use default pc channel.");
        }
        return Provider.uniqueProviderId(providerId, channel);
    }

    protected boolean callbackRequest(HttpServletRequest request) {
        return request.getRequestURI().endsWith("/" + DEFAULT_CALLBACK_SUFFIX) && request.getParameter(PARAM_STATE) != null;
    }

    protected String buildBindUrl(HttpServletRequest request) {
        if (bindUrl.startsWith("http://") || bindUrl.startsWith("https://")) {
            return bindUrl;
        }
        String returnUrl;
        if (!bindUrl.startsWith("/")) {
            returnUrl = ServletUriComponentsBuilder.fromContextPath(request).path("/" + bindUrl).build().toUriString();
        } else {
            returnUrl = ServletUriComponentsBuilder.fromContextPath(request).path(bindUrl).build().toUriString();
        }

        if (enableHttps && returnUrl.startsWith("http://")) {
            returnUrl = returnUrl.replace("http://", "https://");
        }
        return returnUrl;
    }

}
