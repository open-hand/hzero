package org.hzero.core.redis.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 动态 RedisTemplate 工厂类
 *
 * @author bojiangzhou 2020/05/06
 */
public class DynamicRedisTemplateFactory<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRedisTemplateFactory.class);

    private RedisProperties properties;
    private ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration;
    private ObjectProvider<RedisClusterConfiguration> clusterConfiguration;
    private ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> jedisBuilderCustomizers;
    private ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> lettuceBuilderCustomizers;

    private static final String REDIS_CLIENT_LETTUCE = "lettuce";
    private static final String REDIS_CLIENT_JEDIS = "jedis";

    public DynamicRedisTemplateFactory(RedisProperties properties,
                                       ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                                       ObjectProvider<RedisClusterConfiguration> clusterConfiguration,
                                       ObjectProvider<List<JedisClientConfigurationBuilderCustomizer>> jedisBuilderCustomizers,
                                       ObjectProvider<List<LettuceClientConfigurationBuilderCustomizer>> lettuceBuilderCustomizers) {
        this.properties = properties;
        this.sentinelConfiguration = sentinelConfiguration;
        this.clusterConfiguration = clusterConfiguration;
        this.jedisBuilderCustomizers = jedisBuilderCustomizers;
        this.lettuceBuilderCustomizers = lettuceBuilderCustomizers;
    }

    public RedisConnectionFactory createRedisConnectionFactory(int database) {
        RedisConnectionFactory redisConnectionFactory = null;
        switch (getRedisClientType()) {
            case REDIS_CLIENT_LETTUCE:
                LettuceConnectionConfigure lettuceConnectionConfigure = new LettuceConnectionConfigure(properties,
                        sentinelConfiguration, clusterConfiguration, lettuceBuilderCustomizers, database);
                redisConnectionFactory = lettuceConnectionConfigure.redisConnectionFactory();
                break;
            case REDIS_CLIENT_JEDIS:
                JedisConnectionConfigure jedisConnectionConfigure = new JedisConnectionConfigure(properties,
                        sentinelConfiguration, clusterConfiguration, jedisBuilderCustomizers, database);
                redisConnectionFactory = jedisConnectionConfigure.redisConnectionFactory();
                break;
            default:
                LOGGER.warn("Unsupported dynamic redis client.");
        }
        return redisConnectionFactory;
    }

    public RedisTemplate<K, V> createRedisTemplate(int database) {
        RedisConnectionFactory redisConnectionFactory = createRedisConnectionFactory(database);

        Assert.notNull(redisConnectionFactory, "redisConnectionFactory is null.");
        return createRedisTemplate(redisConnectionFactory);
    }

    private RedisTemplate<K, V> createRedisTemplate(RedisConnectionFactory factory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setStringSerializer(stringRedisSerializer);
        redisTemplate.setDefaultSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private String getRedisClientType() {
        try {
            Class.forName("io.lettuce.core.RedisClient");
            return REDIS_CLIENT_LETTUCE;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Lettuce redis client");
        }

        try {
            Class.forName("redis.clients.jedis.Jedis");
            return REDIS_CLIENT_JEDIS;
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Not Jedis redis client");
        }

        throw new RuntimeException("redis client not found.");
    }


}
