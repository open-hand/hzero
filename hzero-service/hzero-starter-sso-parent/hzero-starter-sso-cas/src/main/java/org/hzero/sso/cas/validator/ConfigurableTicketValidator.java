package org.hzero.sso.cas.validator;

import org.springframework.lang.Nullable;

/**
 * 可配置的ticket校验器
 *
 * @author berg-turing 2022/03/18
 */
public interface ConfigurableTicketValidator {
    /**
     * 设置url后缀
     *
     * @param urlSuffix url后缀
     */
    void setUrlSuffix(@Nullable String urlSuffix);
}
