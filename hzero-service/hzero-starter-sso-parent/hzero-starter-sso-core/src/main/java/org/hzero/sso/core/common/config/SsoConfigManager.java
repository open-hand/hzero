package org.hzero.sso.core.common.config;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.hzero.sso.core.common.SsoConfig;
import org.hzero.sso.core.domain.Domain;

/**
 * 单点配置管理器
 *
 * @author berg-turing 2022/03/18
 */
public class SsoConfigManager {
    /**
     * 配置提供器
     */
    private final List<SsoConfigProvider> configProviders;

    public SsoConfigManager(@Nullable List<SsoConfigProvider> configProviders) {
        configProviders = Optional.ofNullable(configProviders).orElse(Collections.emptyList());

        // 排序
        configProviders.sort(Comparator.comparing(Ordered::getOrder));
        this.configProviders = configProviders;
    }

    @Nullable
    public String getConfigValue(@NonNull Domain domain, @NonNull String configKey) {
        for (SsoConfigProvider ssoConfigProvider : this.configProviders) {
            if (!ssoConfigProvider.support(domain)) {
                // 不支持处理，就跳过
                continue;
            }

            SsoConfig ssoConfig = ssoConfigProvider.getConfig(configKey);
            if (Objects.isNull(ssoConfig)) {
                // 没有获取到配置对象就跳过
                continue;
            }

            String value = ssoConfig.getValue(domain);
            if (StringUtils.isNotBlank(value)) {
                // 获取到配置值就返回
                return value;
            }
        }
        return null;
    }
}
