package org.hzero.boot.platform.lov.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.mybatis.MybatisMapperAutoConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.adapter.impl.DefaultLovAdapter;
import org.hzero.boot.platform.lov.aspect.LovValueAspect;
import org.hzero.boot.platform.lov.controller.v1.LovPubSqlController;
import org.hzero.boot.platform.lov.controller.v1.LovSqlController;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.boot.platform.lov.handler.*;
import org.hzero.boot.platform.lov.handler.impl.*;
import org.hzero.core.redis.RedisHelper;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

/**
 * lov客户端自动化配置类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午8:09:21
 */
@Configuration
@ComponentScan(basePackageClasses = LovFeignClient.class)
@EnableFeignClients(basePackageClasses = LovFeignClient.class)
@EnableConfigurationProperties(LovProperties.class)
public class LovAutoConfiguration {

    /**
     * @param redisHelper
     * @param lovFeignClient
     * @return {@link LovAdapter}
     */
    @Bean
    @ConditionalOnMissingBean
    public LovAdapter lovAdapter(RedisHelper redisHelper, LovFeignClient lovFeignClient, ObjectMapper objectMapper, RestTemplate restTemplate, IEncryptionService encryptionService, LovProperties lovProperties) {
        return new DefaultLovAdapter(redisHelper, lovFeignClient, objectMapper, restTemplate, encryptionService, lovProperties);
    }

    @ConditionalOnClass(MybatisMapperAutoConfiguration.class)
    @ConditionalOnProperty(prefix = LovProperties.LOV_PROPERTIES_PREFIX + '.' + LovProperties.Sql.LOV_SQL_PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    static class LovSql {
        /**
         * @return {@link SqlChecker}
         */
        @Bean
        @ConditionalOnMissingBean
        public SqlChecker sqlFilter() {
            return new DefaultSqlChecker();
        }

        /**
         * @param lovFeignClient
         * @return {@link LovSqlGetter}
         */
        @Bean
        @ConditionalOnMissingBean
        public LovSqlGetter lovSqlGetter(RedisHelper redisHelper, LovFeignClient lovFeignClient) {
            return new CacheAndFeignLovSqlGetter(redisHelper, lovFeignClient);
        }

        @Bean
        @ConditionalOnMissingBean
        public LovMeaningSqlTransformer lovMeaningSqlTransformer() {
            return new DefaultLovMeaningSqlTransformer();
        }

        /**
         * @param sqlSessionFactory
         * @return {@link LovSqlHandler}
         */
        @Bean
        @ConditionalOnMissingBean
        public LovSqlHandler lovSqlHandler(SqlSessionFactory sqlSessionFactory, LovSqlGetter lovSqlGetter,
                                           SqlChecker sqlFilter, LovMeaningSqlTransformer lovMeaningSqlTransformer, LovAdapter lovAdapter,
                                           BeanFactory beanFactory, ObjectMapper objectMapper, IEncryptionService encryptionService) {
            return new DefaultLovSqlHandle(sqlSessionFactory, lovSqlGetter, sqlFilter, lovMeaningSqlTransformer, lovAdapter, beanFactory, objectMapper, encryptionService);
        }

        /**
         * @param lovSqlHandler
         * @return {@link LovSqlController}
         */
        @Bean("lovSqlController.v1")
        @ConditionalOnMissingBean
        public LovSqlController lovSqlController(LovSqlHandler lovSqlHandler) {
            return new LovSqlController(lovSqlHandler);
        }

    }

    @ConditionalOnProperty(prefix = LovProperties.LOV_PROPERTIES_PREFIX + '.' + LovProperties.Value.LOV_VALUE_PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    @EnableAspectJAutoProxy
    static class LovValue {

        /**
         * Hzero平台HTTP协议,默认http
         */
        @Value("${hzero.platform.httpProtocol:http}")
        private String hzeroPlatformHttpProtocol;

        /**
         * @param lovAdapter
         * @return {@link LovValueHandle}
         */
        @Bean
        @ConditionalOnMissingBean
        public LovValueHandle lovValueHandle(LovAdapter lovAdapter, RestTemplate restTemplate) {
            return new DefaultLovValueHandle(lovAdapter, restTemplate, this.hzeroPlatformHttpProtocol);
        }

        /**
         * @param lovValueHandle
         * @return {@link LovValueAspect}
         */
        @Bean
        public LovValueAspect lovValueAspect(LovValueHandle lovValueHandle) {
            return new LovValueAspect(lovValueHandle);
        }

    }

    @ConditionalOnProperty(prefix = LovProperties.LOV_PROPERTIES_PREFIX + '.' + LovProperties.Sql.LOV_SQL_PROPERTIES_PREFIX, name = "public-api-enabled", havingValue = "true")
    static class LovPubSql {

        @Bean
        @ConditionalOnMissingBean
        public LovPubSqlHandler lovPubSqlHandler(SqlSessionFactory sqlSessionFactory, LovSqlGetter lovSqlGetter,
                SqlChecker sqlFilter, LovMeaningSqlTransformer lovMeaningSqlTransformer, LovAdapter lovAdapter,
                BeanFactory beanFactory, ObjectMapper objectMapper, IEncryptionService encryptionService) {
            return new LovPubSqlHandlerImpl(sqlSessionFactory, lovSqlGetter, sqlFilter, lovMeaningSqlTransformer, lovAdapter, beanFactory, objectMapper, encryptionService);
        }


        /**
         * @param lovPubSqlHandler
         * @return {@link LovPubSqlController}
         */
        @Bean("lovPubSqlController.v1")
        @ConditionalOnMissingBean
        public LovPubSqlController lovPubSqlController(LovPubSqlHandler lovPubSqlHandler) {
            return new LovPubSqlController(lovPubSqlHandler);
        }
    }

}
