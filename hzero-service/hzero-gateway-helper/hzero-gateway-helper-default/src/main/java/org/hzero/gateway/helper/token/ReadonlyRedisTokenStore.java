package org.hzero.gateway.helper.token;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

import java.util.Collection;

/**
 * @author bojiangzhou
 * @author efenderbosch
 */
public class ReadonlyRedisTokenStore extends RedisTokenStore {
    private static final String ACCESS = "access:";
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    private static final String AUTH = "auth:";
    private static final String REFRESH_AUTH = "refresh_auth:";
    private String prefix = "access_token:";

    private final RedisConnectionFactory connectionFactory;
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();


    public ReadonlyRedisTokenStore(RedisConnectionFactory connectionFactory) {
        super(connectionFactory);
        this.connectionFactory = connectionFactory;
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

    private RedisConnection getConnection() {
        return connectionFactory.getConnection();
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

    private byte[] serialize(String string) {
        return serializationStrategy.serialize(string);
    }


    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        String key = authenticationKeyGenerator.extractKey(authentication);
        byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializedKey);
        } finally {
            conn.close();
        }
        OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
        if (accessToken != null && !key
                .equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
            // Keep the stores consistent (maybe the same user is
            // represented by this authentication but the details have
            // changed)
            storeAccessToken(accessToken, authentication);
        }

        return accessToken;
    }


    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        byte[] bytes;
        RedisConnection conn = getConnection();
        try {
            bytes = conn.get(serializeKey(AUTH + token));
        } finally {
            conn.close();
        }
        return deserializeAuthentication(bytes);
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
            return deserializeAuthentication(bytes);
        } finally {
            conn.close();
        }
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        throw new UnsupportedOperationException("Storage of access tokens is not allowed here.");
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken accessToken) {
        throw new UnsupportedOperationException("It is not allowed to delete the access token here.");
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        byte[] key = serializeKey(ACCESS + tokenValue);
        byte[] bytes;
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
        throw new UnsupportedOperationException("It is not allowed to delete the access token here.");
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        throw new UnsupportedOperationException("It is not allowed to store refresh tokens here.");
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        throw new UnsupportedOperationException("It is not allowed to find refresh tokens here.");
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
        throw new UnsupportedOperationException("It is not allowed to delete the refresh token here.");
    }

    @Override
    public void removeRefreshToken(String tokenValue) {
        throw new UnsupportedOperationException("It is not allowed to delete the refresh token here.");
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        throw new UnsupportedOperationException("It is not allowed to delete the access token here.");
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        throw new UnsupportedOperationException("It is not allowed to find the access token in this way.");
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        throw new UnsupportedOperationException("It is not allowed to find the access token in this way.");
    }
}
