package org.hzero.boot.autoconfigure.platform;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.encrypt.EncryptRepository;
import org.hzero.boot.platform.encrypt.impl.DefaultEncryptClient;
import org.hzero.boot.platform.encrypt.impl.DefaultEncryptRepository;
import org.hzero.core.redis.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bojiangzhou 2019/07/23
 */
@Configuration
public class PlatformAutoConfiguration {

    @Bean
    public PlatformInitializeConfig platformInitializeConfig() {
        return new PlatformInitializeConfig();
    }

    @Bean
    @ConditionalOnMissingBean(EncryptRepository.class)
    public EncryptRepository encryptRepository(RedisHelper redisHelper) {
        return new DefaultEncryptRepository(redisHelper);
    }

    @Bean
    @ConditionalOnMissingBean(EncryptClient.class)
    public EncryptClient encryptClient(EncryptRepository encryptRepository) {
        return new DefaultEncryptClient(encryptRepository);
    }

}
