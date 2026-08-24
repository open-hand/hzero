package org.hzero.sso.core.common.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.hzero.sso.core.common.SsoConfig;
import org.hzero.sso.core.domain.Domain;

/**
 * 应用层配置提供器
 *
 * @author berg-turing 2022/03/18
 */
public class SsoPropertiesConfigProvider implements SsoConfigProvider {
    /**
     * 配置对象
     */
    private final Map<String, SsoPropertiesConfig> configMap;

    @Autowired
    public SsoPropertiesConfigProvider(@Nullable @Autowired(required = false) List<SsoPropertiesConfig> configs) {
        this.configMap = Optional.ofNullable(configs).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(SsoConfig::getKey, Function.identity()));
    }

    @Override
    public boolean support(@NonNull Domain domain) {
        return true;
    }

    @Override
    @Nullable
    public SsoConfig getConfig(@NonNull String configKey) {
        return this.configMap.get(configKey);
    }
}
