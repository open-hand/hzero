package org.hzero.sso.core.common;

import org.springframework.lang.NonNull;

import org.hzero.sso.core.domain.Domain;

/**
 * 单点配置对象
 *
 * @author berg-turing 2022/03/18
 */
public interface SsoConfig {
    /**
     * 获取配置的key
     *
     * @return 配置的key
     */
    String getKey();

    /**
     * 获取配置的value
     *
     * @param domain 域对象
     * @return 配置的value
     */
    String getValue(@NonNull Domain domain);
}
