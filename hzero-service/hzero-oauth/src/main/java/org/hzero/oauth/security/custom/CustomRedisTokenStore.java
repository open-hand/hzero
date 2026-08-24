package org.hzero.oauth.security.custom;

import org.apache.commons.lang3.StringUtils;
import org.hzero.autoconfigure.oauth.OauthAutoConfiguration;
import org.hzero.oauth.security.util.LoginUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.session.SessionRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author bojiangzhou
 * @author efenderbosch
 */
public class CustomRedisTokenStore extends RedisTokenStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRedisTokenStore.class);
    private static final int MAX_INACTIVE_INTERVAL_IN_SECONDS = 600;

    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    private static final String REFRESH = "refresh:";
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    private static final String UNAME_TO_ACCESS = "uname_to_access:";
    private static final String UNAME_TO_ACCESS_APP = "uname_to_access_app:";
    private static final String LNAME_TO_ACCESS = "lname_to_access:";
    private static final String LNAME_TO_ACCESS_APP = "lname_to_access_app:";
    private static final String ACCESS_TO_SESSION = "access_to_session:";
    private static final String OFFLINE_ACCESS = "offline_access:";
    private static final long REMOVED_ACCESS_EXPIRED_SECONDS = 12 * 3600;

    private final RedisConnectionFactory connectionFactory;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();
    private LoginUtil loginUtil;
    private SessionRepository sessionRepository;
    private boolean accessTokenAutoRenewal;

    private String prefix = "";

    public CustomRedisTokenStore(RedisConnectionFactory connectionFactory,
                                 LoginUtil loginUtil,
                                 SessionRepository sessionRepository,
                                 boolean accessTokenAutoRenewal) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory;
        this.loginUtil = loginUtil;
        this.sessionRepository = sessionRepository;
        this.accessTokenAutoRenewal = accessTokenAutoRenewal;
    }

    @Override
    public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
        this.authenticationKeyGenerator = authenticationKeyGenerator;
    }

    @Override
    public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public RedisConnection getConnection() {
        return connectionFactory.getConnection();
    }

    private byte[] serialize(Object object) {
        return serializationStrategy.serialize(object);
    }

    private byte[] serializeKey(String object) {
        return serialize(prefix + object);
    }

    private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
    }

    private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
    }

    private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
        return serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
    }

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }

    private String deserializeString(byte[] bytes) {
        return serializationStrategy.deserializeString(bytes);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = this.extractKey(authentication);
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
        if (accessToken != null && !key
                .equals(this.extractKey(readAuthentication(accessToken.getValue())))) {
            // Keep the stores consistent (maybe the same user is
            // represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }

        return accessToken;
    }

    public String extractKey(OAuth2Authentication authentication) {
        return this.authenticationKeyGenerator.extractKey(authentication);
    }

    /**
     * 判断是否允许多设备登录，不允许则删除其它的token
     */
    public void singleLoginController(OAuth2Authentication authentication) {
        String key = this.extractKey(authentication);
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
        if (!authentication.isClientOnly() && (loginUtil.isWebSingleLogin(authentication) || loginUtil.isAppSingleLogin(authentication))) {
            String username = authentication.getName();
            String clientId = authentication.getOAuth2Request().getClientId();
            String tokenValue = accessToken != null ? accessToken.getValue() : "";

            Set<String> tokens = findTokensByClientIdAndUserName(clientId, username, authentication);
            tokens.forEach(token -> {
                if (!StringUtils.equals(tokenValue, token)) {
                    saveOfflineAccessToken(token);
                    removeAccessToken(token);
                }
            });
        }
    }

    /**
     * 判断踢下线的 access_token 是否存在
     *
     * @param token access_token
     * @return exists
     */
    public boolean existsOfflineAccessToken(String token) {
        byte[] accessKey = serializeKey(OFFLINE_ACCESS + token);
        RedisConnection conn = getConnection();
        Boolean exists = null;
        try {
            exists = conn.exists(accessKey);
        } catch (UnsupportedOperationException e) {
            LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
        } finally {
            conn.close();
        }
        return exists != null && exists;
    }

    /**
     * 踢下线时保存移除的 access_token
     *
     * @param token access_token
     */
    public void saveOfflineAccessToken(String token) {
        byte[] accessKey = serializeKey(OFFLINE_ACCESS + token);
        byte[] serializedAccessToken = serialize(token);
        RedisConnection conn = getConnection();
        try {
            conn.set(accessKey, serializedAccessToken);
            conn.expire(accessKey, REMOVED_ACCESS_EXPIRED_SECONDS);
        } catch (UnsupportedOperationException e) {
            LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
        } finally {
            conn.close();
        }
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializeKey(AUTH + token));
        } finally {
            conn.close();
        }
        OAuth2Authentication auth = deserializeAuthentication(bytes);
        return auth;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return readAuthenticationForRefreshToken(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
        RedisConnection conn = getConnection();
        try {
            byte[] bytes = conn.get(serializeKey(REFRESH_AUTH + token));
            OAuth2Authentication auth = deserializeAuthentication(bytes);
            return auth;
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        byte[] serializedAccessToken = serialize(token);
        byte[] serializedAuth = serialize(authentication);
        byte[] accessKey = serializeKey(ACCESS + token.getValue());
        byte[] authKey = serializeKey(AUTH + token.getValue());
        byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + this.extractKey(authentication));

        // --------- access_token 与 sessionId 的关系 ---------
        byte[] accessSessionKey = serializeKey(ACCESS_TO_SESSION + token.getValue());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);
        byte[] serializedSessionId = null;
        if (session != null) {
            serializedSessionId = serialize(session.getId());
        }

        // --------- 移动端和web端分开存储 ---------
        String uKey = UNAME_TO_ACCESS;
        String lKey = LNAME_TO_ACCESS;
        if (loginUtil.isMobileDeviceLogin(authentication)) {
            uKey = UNAME_TO_ACCESS_APP;
            lKey = LNAME_TO_ACCESS_APP;
        }

        byte[] uKeyBytes = serializeKey(uKey + getApprovalKey(authentication));
        byte[] lKeyBytes = null;
        if (authentication.getUserAuthentication() != null) {
            lKeyBytes = serializeKey(lKey + authentication.getUserAuthentication().getName());
        }

        //byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());

        RedisConnection conn = getConnection();
        try {
            try {
                conn.openPipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
            conn.set(accessKey, serializedAccessToken);
            conn.set(authKey, serializedAuth);
            conn.set(authToAccessKey, serializedAccessToken);
            byte[] serializedTokenValue = serialize(token.getValue());
            if (!authentication.isClientOnly()) {
                conn.rPush(uKeyBytes, serializedTokenValue);
            }
            if (lKeyBytes != null) {
                conn.rPush(lKeyBytes, serializedTokenValue);
            }
            if (serializedSessionId != null) {
                conn.set(accessSessionKey, serializedSessionId);
            }
            //conn.rPush(clientId, serializedAccessToken);
            if (token.getExpiration() != null) {
                // 如果配置了自动下线 则过期时间为自动下线时间 否则取 token 过期时间
                int seconds = token.getExpiresIn();
                conn.expire(accessKey, seconds);
                conn.expire(authKey, seconds);
                conn.expire(authToAccessKey, seconds);
                //conn.expire(clientId, seconds);
                conn.expire(uKeyBytes, seconds);
                conn.expire(accessSessionKey, seconds);
                if (lKeyBytes != null) {
                    conn.expire(lKeyBytes, seconds);
                }
                if (session != null && accessTokenAutoRenewal) {
                    session.setMaxInactiveInterval(Math.min(OauthAutoConfiguration.MAX_INACTIVE_INTERVAL_IN_SECONDS, seconds));
                }
            }
            OAuth2RefreshToken refreshToken = token.getRefreshToken();
            if (refreshToken != null && refreshToken.getValue() != null) {
                byte[] refresh = serialize(token.getRefreshToken().getValue());
                byte[] auth = serialize(token.getValue());
                byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
                conn.set(refreshToAccessKey, auth);
                byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + token.getValue());
                conn.set(accessToRefreshKey, refresh);
                if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                    ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                    Date expiration = expiringRefreshToken.getExpiration();
                    if (expiration != null) {
                        int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L)
                                .intValue();
                        conn.expire(refreshToAccessKey, seconds);
                        conn.expire(accessToRefreshKey, seconds);
                    }
                }
            }
            try {
                conn.closePipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
        } finally {
            conn.close();
        }
    }

    public void renewalAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication, int accessTokenValiditySeconds) {
        // 未开启自动续期 并且 自动下线也未开启
        if (!accessTokenAutoRenewal) {
            return;
        }
        // 如果 refresh token 过期 则不允许续期
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        boolean refreshTokenExpired = refreshToken instanceof DefaultExpiringOAuth2RefreshToken
                && isExpired(((DefaultExpiringOAuth2RefreshToken) refreshToken).getExpiration());
        // 无操作下线时间 优先级大于 默认客户端超时时间(相当于一次 refresh token 操作)
        long seconds = accessTokenValiditySeconds;
        // 如果开启了无操作自动下线
        if (seconds > 0) {
            String uKey = UNAME_TO_ACCESS;
            String lKey = LNAME_TO_ACCESS;
            if (loginUtil.isMobileDeviceLogin(authentication)) {
                uKey = UNAME_TO_ACCESS_APP;
                lKey = LNAME_TO_ACCESS_APP;
            }
            // 重新保存 access_token
            byte[] accessKey = serializeKey(ACCESS + token.getValue());
            byte[] authKey = serializeKey(AUTH + token.getValue());
            byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
            //byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
            byte[] uKeyBytes = serializeKey(uKey + getApprovalKey(authentication));
            byte[] accessSessionKey = serializeKey(ACCESS_TO_SESSION + token.getValue());
            byte[] lKeyBytes = authentication.getUserAuthentication() == null ? null : serializeKey(lKey + authentication.getUserAuthentication().getName());
            RedisConnection conn = getConnection();
            try {
                try {
                    conn.openPipeline();
                } catch (UnsupportedOperationException e) {
                    LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
                }
                // 如果 refresh token 未过期 && 开启了 access token 续期 = 刷新 access token
                if (!refreshTokenExpired && accessTokenAutoRenewal) {
                    Date expireDate = new Date(System.currentTimeMillis() + (accessTokenValiditySeconds * 1000L));
                    ((DefaultOAuth2AccessToken) token).setExpiration(expireDate);
                    byte[] serializedAccessToken = serialize(token);
                    conn.set(accessKey, serializedAccessToken);
                    conn.set(authToAccessKey, serializedAccessToken);
                }
                conn.expire(accessKey, seconds);
                conn.expire(authKey, seconds);
                conn.expire(authToAccessKey, seconds);
                //conn.expire(clientId, seconds);
                conn.expire(uKeyBytes, seconds);
                conn.expire(accessSessionKey, seconds);
                if (lKeyBytes != null) {
                    conn.expire(lKeyBytes, seconds);
                }
                try {
                    conn.closePipeline();
                } catch (UnsupportedOperationException e) {
                    LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
                }
            } finally {
                conn.close();
            }
        }
    }

    private boolean isExpired(Date expiration) {
        return expiration != null && expiration.before(new Date());
    }

    private static String getApprovalKey(OAuth2Authentication authentication) {
        String userName = authentication.getUserAuthentication() == null ? ""
                : authentication.getUserAuthentication().getName();
        return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
    }

    private static String getApprovalKey(String clientId, String userName) {
        return clientId + (userName == null ? "" : ":" + userName);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        removeAccessToken(accessToken.getValue());
    }

    public void removeAccessTokenByLoginName(String clientId, String loginName) {
        Assert.isTrue(StringUtils.isNotBlank(clientId), "clientId should not be null.");
        Assert.isTrue(StringUtils.isNotBlank(loginName), "loginName should not be null.");
        String sourceKey = UNAME_TO_ACCESS + getApprovalKey(clientId, loginName);
        String sourceKeyApp = UNAME_TO_ACCESS_APP + getApprovalKey(clientId, loginName);
        //web
        removeAccessTokenBySourceKey(sourceKey);
        //app
        removeAccessTokenBySourceKey(sourceKeyApp);
    }

    public void removeAccessTokenByLoginName(String loginName) {
        Assert.isTrue(StringUtils.isNotBlank(loginName), "loginName should not be null.");
        String sourceKey = LNAME_TO_ACCESS + loginName;
        String sourceKeyApp = LNAME_TO_ACCESS_APP + loginName;
        //web
        removeAccessTokenBySourceKey(sourceKey);
        //app
        removeAccessTokenBySourceKey(sourceKeyApp);
    }

    public Collection<String> findTokenValuesByLoginName(String loginName) {
        if (StringUtils.isEmpty(loginName)) {
            return Collections.emptyList();
        }
        Set<String> tokenValues = new HashSet<>();
        String sourceKey = LNAME_TO_ACCESS + loginName;
        String sourceKeyApp = LNAME_TO_ACCESS_APP + loginName;

        RedisConnection conn = getConnection();
        try {
            List<byte[]> bytes = conn.lRange(serializeKey(sourceKey), 0, -1);
            if (!CollectionUtils.isEmpty(bytes)) {
                bytes.forEach(tokenBytes -> tokenValues.add(deserializeString(tokenBytes)));
            }
            bytes = conn.lRange(serializeKey(sourceKeyApp), 0, -1);
            if (!CollectionUtils.isEmpty(bytes)) {
                bytes.forEach(tokenBytes -> tokenValues.add(deserializeString(tokenBytes)));
            }
        } finally {
            conn.close();
        }
        return tokenValues;
    }


    private void removeAccessTokenBySourceKey(String sourceKey) {
        RedisConnection conn = getConnection();
        try {
            byte[] tokenBytes = null;
            while ((tokenBytes = conn.lPop(serializeKey(sourceKey))) != null) {
                String token = deserializeString(tokenBytes);
                removeAccessToken(token);
                String refreshToken = getRefreshTokenByToken(conn, token);
                removeRefreshToken(refreshToken);
            }
        } finally {
            conn.close();
        }
    }

    private String getRefreshTokenByToken(RedisConnection conn, String token) {
        if (conn != null && !conn.isClosed()) {
            byte[] key = serializeKey(ACCESS_TO_REFRESH + token);
            return deserializeString(conn.get(key));
        } else {
            throw new RuntimeException("RedisConnection is closed.");
        }

    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = serializeKey(ACCESS + tokenValue);
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(key);
        } finally {
            conn.close();
        }
        return deserializeAccessToken(bytes);
    }

    @Override
    public void removeAccessToken(String tokenValue) {
        byte[] accessKey = serializeKey(ACCESS + tokenValue);
        byte[] authKey = serializeKey(AUTH + tokenValue);
        byte[] accessToRefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        byte[] accessSessionKey = serializeKey(ACCESS_TO_SESSION + tokenValue);
        RedisConnection conn = getConnection();
        try {
            try {
                conn.openPipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
            byte[] access = conn.get(accessKey);
            byte[] auth = conn.get(authKey);
            byte[] serializedSessionId = conn.get(accessSessionKey);
            conn.del(accessKey);
            conn.del(accessToRefreshKey);
            conn.del(accessSessionKey);
            // Don't remove the refresh token - it's up to the caller to do that
            conn.del(authKey);
            List<Object> results = null;
            try {
                results = conn.closePipeline();
                if (!CollectionUtils.isEmpty(results)) {
                    access = (byte[]) results.get(0);
                    auth = (byte[]) results.get(1);
                    serializedSessionId = (byte[]) results.get(2);
                }
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }

            // 清除session
            if (serializedSessionId != null) {
                String sessionId = deserializeString(serializedSessionId);
                sessionRepository.deleteById(sessionId);
            }

            OAuth2Authentication authentication = deserializeAuthentication(auth);
            if (authentication != null) {
                String key = this.extractKey(authentication);
                byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
                // ---------- 区分移动端和web端 -----------
                String uKey = UNAME_TO_ACCESS;
                String lKey = LNAME_TO_ACCESS;
                if (loginUtil.isMobileDeviceLogin(authentication)) {
                    uKey = UNAME_TO_ACCESS_APP;
                    lKey = LNAME_TO_ACCESS_APP;
                }
                byte[] uKeyBytes = serializeKey(uKey + getApprovalKey(authentication));
                byte[] lKeyBytes = null;
                if (authentication.getUserAuthentication() != null) {
                    lKeyBytes = serializeKey(lKey + authentication.getUserAuthentication().getName());
                }
                //byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());
                try {
                    conn.openPipeline();
                } catch (UnsupportedOperationException e) {
                    LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
                }
                conn.del(authToAccessKey);
                conn.lRem(uKeyBytes, 1, tokenValue.getBytes(StandardCharsets.UTF_8));
                if (lKeyBytes != null) {
                    conn.lRem(lKeyBytes, 1, tokenValue.getBytes(StandardCharsets.UTF_8));
                }
                //conn.lRem(clientId, 1, access);
                conn.del(serialize(ACCESS + key));
                try {
                    conn.closePipeline();
                } catch (UnsupportedOperationException e) {
                    LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
                }
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());
        byte[] serializedRefreshToken = serialize(refreshToken);
        RedisConnection conn = getConnection();
        try {
            try {
                conn.openPipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
            conn.set(refreshKey, serializedRefreshToken);
            conn.set(refreshAuthKey, serialize(authentication));
            if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
                ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
                Date expiration = expiringRefreshToken.getExpiration();
                if (expiration != null) {
                    int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue();
                    conn.expire(refreshKey, seconds);
                    conn.expire(refreshAuthKey, seconds);
                }
            }
            try {
                conn.closePipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        byte[] key = serializeKey(REFRESH + tokenValue);
        byte[] bytes = null;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(key);
        } finally {
            conn.close();
        }
        return deserializeRefreshToken(bytes);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        removeRefreshToken(refreshToken.getValue());
    }

    @Override
    public void removeRefreshToken(String tokenValue) {
        byte[] refreshKey = serializeKey(REFRESH + tokenValue);
        byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
        byte[] refresh2AccessKey = serializeKey(REFRESH_TO_ACCESS + tokenValue);
        byte[] access2RefreshKey = serializeKey(ACCESS_TO_REFRESH + tokenValue);
        RedisConnection conn = getConnection();
        try {
            try {
                conn.openPipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
            conn.del(refreshKey);
            conn.del(refreshAuthKey);
            conn.del(refresh2AccessKey);
            conn.del(access2RefreshKey);
            try {
                conn.closePipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    private void removeAccessTokenUsingRefreshToken(String refreshToken) {
        byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshToken);
        List<Object> results = null;
        RedisConnection conn = getConnection();
        byte[] bytes = null;
        try {
            try {
                conn.openPipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
            bytes = conn.get(key);
            conn.del(key);
            try {
                results = conn.closePipeline();
            } catch (UnsupportedOperationException e) {
                LOGGER.debug("Currently RedisConnection[" + conn.getClass() + "] does not support the use of pipes, ignore it.");
            }
        } finally {
            conn.close();
        }
        if (bytes == null && results == null) {
            return;
        }
        Assert.notNull(results, "results is null");
        bytes = bytes == null ? (byte[]) results.get(0) : bytes;
        String accessToken = deserializeString(bytes);
        if (accessToken != null) {
            removeAccessToken(accessToken);
        }
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(clientId, userName));
        List<byte[]> byteList = null;
        RedisConnection conn = getConnection();
        try {
            byteList = conn.lRange(approvalKey, 0, -1);
        } finally {
            conn.close();
        }
        if (byteList == null || byteList.size() == 0) {
            return Collections.<OAuth2AccessToken>emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
            accessTokens.add(accessToken);
        }
        return Collections.<OAuth2AccessToken>unmodifiableCollection(accessTokens);
    }

    public Set<String> findTokensByClientIdAndUserName(String clientId, String userName, OAuth2Authentication authentication) {
        // --------- 移动端和web端分开存储 ---------
        String sourceKey = loginUtil.isMobileDeviceLogin(authentication) ? UNAME_TO_ACCESS_APP : UNAME_TO_ACCESS;
        byte[] approvalKey = serializeKey(sourceKey + getApprovalKey(clientId, userName));
        List<byte[]> byteList = null;
        RedisConnection conn = getConnection();
        try {
            byteList = conn.lRange(approvalKey, 0, -1);
        } finally {
            conn.close();
        }
        if (byteList == null || byteList.size() == 0) {
            return Collections.emptySet();
        }
        Set<String> accessTokens = new HashSet<>(byteList.size());
        for (byte[] bytes : byteList) {
            String accessToken = deserializeString(bytes);
            accessTokens.add(accessToken);
        }
        return accessTokens;
    }

    /**
     * @deprecated  CLIENT_ID_TO_ACCESS 缓存的认证信息会造成缓存数据过多且几乎不会过期，因此禁用 CLIENT_ID_TO_ACCESS，此方法返回数据始终为空
     */
    @Override
    @Deprecated
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        byte[] key = serializeKey(CLIENT_ID_TO_ACCESS + clientId);
        List<byte[]> byteList = null;
        RedisConnection conn = getConnection();
        try {
            byteList = conn.lRange(key, 0, -1);
        } finally {
            conn.close();
        }
        if (byteList == null || byteList.size() == 0) {
            return Collections.<OAuth2AccessToken>emptySet();
        }
        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>(byteList.size());
        for (byte[] bytes : byteList) {
            OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
            accessTokens.add(accessToken);
        }
        return Collections.<OAuth2AccessToken>unmodifiableCollection(accessTokens);
    }
}
