package org.hzero.boot.platform.language.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 语言自动刷新配置
 *
 * @author qingsheng.chen@hand-china.com 2021/6/9 13:54
 */
@ConfigurationProperties(prefix = LanguageAutoRefreshProperties.LANGUAGE_AUTO_REFRESH_PROPERTIES_PREFIX)
public class LanguageAutoRefreshProperties {
    public static final String LANGUAGE_AUTO_REFRESH_PROPERTIES_PREFIX = "hzero.language.auto-refresh";
    /**
     * 是否启用自动刷新语言，默认关闭
     */
    private boolean enable = false;
    /**
     * 自动刷新语言时间间隔，默认 3 分钟
     */
    private Duration interval = Duration.ofMinutes(3);

    public boolean isEnable() {
        return enable;
    }

    public LanguageAutoRefreshProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public Duration getInterval() {
        return interval;
    }

    public LanguageAutoRefreshProperties setInterval(Duration interval) {
        this.interval = interval;
        return this;
    }
}
