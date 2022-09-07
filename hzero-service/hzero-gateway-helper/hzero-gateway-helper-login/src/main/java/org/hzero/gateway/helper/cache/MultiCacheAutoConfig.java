package org.hzero.gateway.helper.cache;

import org.hzero.gateway.helper.cache.l1.L1CacheManager;
import org.hzero.gateway.helper.cache.l1.caffeine.CaffeineL1CacheManager;
import org.hzero.gateway.helper.cache.l2.L2CacheManager;
import org.hzero.gateway.helper.cache.l2.redis.RedisL2CacheManager;
import org.hzero.gateway.helper.cache.multi.MultiCacheManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Optional;

/**
 * redis config copy from RedisAutoConfiguration and RedisCacheConfiguration
 */
@Configuration
@EnableConfigurationProperties({MultiCacheProperties.class, RedisProperties.class})
public class MultiCacheAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(
            ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
        return new CacheManagerCustomizers(customizers.getIfAvailable());
    }

    @Bean
    @Primary
//    @ConditionalOnExpression("#{'${spring.cache.multi.l1.enabled}'=='true' || " +
//            "'${spring.cache.multi.l1.enabled}'=='true'}")
    public MultiCacheManager multiCacheManager(Optional<L2CacheManager> l2CacheManagerOptional,
                                               MultiCacheProperties multiCacheProperties) {
        L1CacheManager l1CacheManager = null;
        if (multiCacheProperties.getL1().isEnabled()) {
            if (CaffeineL1CacheManager.type().equals(multiCacheProperties.getL1().getType())) {
                l1CacheManager = new CaffeineL1CacheManager();
                ((CaffeineL1CacheManager) l1CacheManager).setAllowNullValues(multiCacheProperties.isAllowNullValues());
            }
        }
        L2CacheManager l2CacheManager = null;
        if (l2CacheManagerOptional.isPresent()) {
            l2CacheManager = l2CacheManagerOptional.get();
        }
        return new MultiCacheManager(l1CacheManager, l2CacheManager, multiCacheProperties);

    }

    @Configuration
    @ConditionalOnExpression("#{'${spring.cache.multi.l2.enabled}'=='true' &&" +
            " '${spring.cache.multi.l2.type}'=='redis'}")
    @ConditionalOnClass({JedisConnection.class, RedisOperations.class})
    public static class RedisConfig {

        private final RedisProperties properties;

        private final RedisSentinelConfiguration sentinelConfiguration;

        private final RedisClusterConfiguration clusterConfiguration;

        public RedisConfig(RedisProperties properties,
                           ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                           ObjectProvider<RedisClusterConfiguration> clusterConfiguration) {
            this.properties = properties;
            this.sentinelConfiguration = sentinelConfiguration.getIfAvailable();
            this.clusterConfiguration = clusterConfiguration.getIfAvailable();
        }

        @Bean
        @ConditionalOnMissingBean(name = "redisTemplate")
        public RedisTemplate<Object, Object> redisTemplate(
                RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(StringRedisTemplate.class)
        public StringRedisTemplate stringRedisTemplate(
                RedisConnectionFactory redisConnectionFactory) {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }

        @Bean
        @ConditionalOnMissingBean(L2CacheManager.class)
        public L2CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                           CacheManagerCustomizers customizerInvoker) {
            return customizerInvoker.customize(new RedisL2CacheManager(connectionFactory));
        }
    }

}
