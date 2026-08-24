package org.hzero.sso.core.common.config;

import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.hzero.sso.core.common.SsoConfig;
import org.hzero.sso.core.domain.Domain;

/**
 * @author chihao.ran@hand-china.com
 * 2022/03/17 16:23
 */
public interface SsoConfigProvider extends Ordered {
    /**
     * 是否支持当处理
     *
     * @param domain 域对象
     * @return true 支持  false 不支持
     */
    boolean support(@NonNull Domain domain);

    /**
     * 获取配置
     *
     * @param configKey 配置名称
     * @return 配置值
     */
    @Nullable
    SsoConfig getConfig(@NonNull String configKey);

    /**
     * 提供器执行的优先级
     * 默认：0
     *
     * @return 优先级
     */
    @Override
    default int getOrder() {
        return 0;
    }
}
