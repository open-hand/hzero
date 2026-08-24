package org.hzero.sso.core.common.config;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.lang.NonNull;

import org.hzero.core.util.DomainUtils;
import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.domain.Domain;

/**
 * Cas应用层配置对象抽象实现
 *
 * @author berg-turing 2022/03/18
 */
public abstract class AbstractSsoPropertiesConfig implements SsoPropertiesConfig {
    /**
     * Cas配置对象
     */
    private final Map<String, Map<String, String>> domainConfigs;

    protected AbstractSsoPropertiesConfig(@NonNull SsoProperties ssoCasProperties) {
        Map<String, List<SsoProperties.DomainConfig.Config>> domainConfigs = Optional
                .ofNullable(ssoCasProperties.getDomainConfigs()).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(SsoProperties.DomainConfig::getDomain,
                        SsoProperties.DomainConfig::getConfigs));

        this.domainConfigs = new HashMap<>(domainConfigs.size());
        domainConfigs.forEach((key, value) -> {
            // 域名下的配置
            Map<String, String> domainConfig = value.stream().collect(Collectors.toMap(SsoProperties.DomainConfig.Config::getKey,
                    SsoProperties.DomainConfig.Config::getValue));

            // 设置配置值
            this.domainConfigs.put(DomainUtils.getDomain(key), domainConfig);
        });
    }

    @Override
    public String getValue(@NonNull Domain domain) {
        // 获取对应域名下的配置
        Map<String, String> domainConfig = this.domainConfigs.get(DomainUtils.getDomain(domain.getDomainUrl()));
        if (MapUtils.isEmpty(domainConfig)) {
            return null;
        }

        // 获取对应配置的值
        return domainConfig.get(this.getKey());
    }
}
