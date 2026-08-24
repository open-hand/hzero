package org.hzero.starter.social.core.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import org.hzero.starter.social.core.common.configurer.SocialConnectionFactoryBuilder;
import org.hzero.starter.social.core.provider.Provider;
import org.hzero.starter.social.core.provider.SocialProviderRepository;

/**
 * social 配置
 *
 * @author bojiangzhou 2018/10/17
 */
@EnableSocial
@Configuration
public class CustomSocialConfiguration extends SocialConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSocialConfiguration.class);

    @Autowired
    private SocialProviderRepository socialProviderRepository;

    private List<SocialConnectionFactoryBuilder> socialConnectionFactories;

    @Autowired
    public void setSocialConnectionFactories(Optional<List<SocialConnectionFactoryBuilder>> connectionFactoryBuilders) {
        this.socialConnectionFactories = connectionFactoryBuilders.orElse(Collections.emptyList());
        if (CollectionUtils.isEmpty(socialConnectionFactories)) {
            LOGGER.info("Not found SocialConnectionFactoryBuilder");
        }
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        if (CollectionUtils.isNotEmpty(socialConnectionFactories)) {
            for (SocialConnectionFactoryBuilder configurer : socialConnectionFactories) {
                List<Provider> providers = socialProviderRepository.getProvider(configurer.getProviderId());
                if (CollectionUtils.isNotEmpty(providers)) {
                    for (Provider provider : providers) {
                        // 使用三方编码+渠道组成唯一
                        String uniqueProviderId = Provider.uniqueProviderId(provider.getProviderId(), provider.getChannel());
                        Provider newProvider = new Provider(uniqueProviderId, provider.getChannel(),
                                provider.getAppId(), provider.getAppKey(), provider.getSubAppId());
                        connectionFactoryConfigurer.addConnectionFactory(configurer.buildConnectionFactory(newProvider));
                    }
                }
            }
        }
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Bean
    public SocialInitializeConfig socialInitializeConfig() {
        return new SocialInitializeConfig();
    }

}
