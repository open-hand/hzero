package io.choerodon.mybatis.helper;

import io.choerodon.mybatis.MybatisConfigurationProperties;
import io.choerodon.mybatis.helper.snowflake.NoneSnowflakeMetaProvider;
import io.choerodon.mybatis.helper.snowflake.RedisSnowflakeMetaProvider;
import io.choerodon.mybatis.helper.snowflake.SnowflakeHelper;
import io.choerodon.mybatis.helper.snowflake.SnowflakeMetaProvider;
import org.hzero.core.redis.RedisHelper;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.PriorityOrdered;

import java.util.Optional;


/**
 * @author qingsheng.chen@hand-china.com
 */
@Configuration
@EnableConfigurationProperties(MybatisConfigurationProperties.class)
public class MybatisHelperAutoConfiguration implements PriorityOrdered {

    @Bean
    @ConditionalOnProperty(name = "mybatis.configuration.key-generator", havingValue = "snowflake")
    @ConditionalOnMissingBean(SnowflakeHelper.class)
    public SnowflakeHelper snowflakeHelper(MybatisConfigurationProperties properties,
                                           ApplicationContext context,
                                           @Value("${spring.application.name}") String serviceName,
                                           RedisHelper redisHelper) {
        SnowflakeMetaProvider snowflakeMetaProvider;
        if (MybatisConfigurationProperties.SnowflakeProperties.MetaProvider.redis.equals(properties.getSnowflake().getMetaProvider())) {
            snowflakeMetaProvider = new RedisSnowflakeMetaProvider(serviceName,
                    properties.getSnowflake(),
                    properties.getSnowflake().getBitDataCenterId(),
                    properties.getSnowflake().getBitWorkerId(),
                    redisHelper);
        } else {
            snowflakeMetaProvider = new NoneSnowflakeMetaProvider(properties.getSnowflake().getBitDataCenterId(),
                    properties.getSnowflake().getBitWorkerId());
        }
        EncryptContext.setDefaultEncryptType(EncryptType.TO_STRING);
        return new SnowflakeHelper(properties.getSnowflake().getStartTimestamp(),
                Optional.ofNullable(properties.getSnowflake().getDataCenterId())
                        .orElseGet(() -> snowflakeMetaProvider.dataCenterId(context)),
                Optional.ofNullable(properties.getSnowflake().getWorkerId())
                        .orElseGet(() -> snowflakeMetaProvider.workerId(context)),
                properties.getSnowflake().getBitTimestamp(),
                properties.getSnowflake().getBitDataCenterId(),
                properties.getSnowflake().getBitWorkerId(),
                properties.getSnowflake().getBitSequence());
    }

    @Bean
    @ConditionalOnMissingBean(LanguageHelper.class)
    public LanguageHelper languageHelper(MybatisConfigurationProperties properties) {
        LanguageHelper.setDefaultLanguage(properties.getDefaultLanguage());
        return LanguageHelper.getInstance();
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1000;
    }
}
