package org.hzero.boot.platform.language;

import io.choerodon.mybatis.helper.LanguageHelper;
import org.hzero.boot.platform.language.autoconfigure.LanguageAutoRefreshProperties;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.domian.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author qingsheng.chen@hand-china.com 2021/6/9 14:04
 */
public class LanguageAutoRefreshTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LanguageAutoRefreshTask.class);

    private static final String VERSION = LanguageAutoRefreshConstant.LANGUAGE_AUTO_REFRESH_VERSION_KEY;
    private static final String LANGUAGE = LanguageAutoRefreshConstant.LANGUAGE_AUTO_REFRESH_LANGUAGE_KEY;

    private final RedisHelper redisHelper;
    private final LanguageAutoRefreshProperties properties;

    private volatile String version;

    public LanguageAutoRefreshTask(RedisHelper redisHelper, LanguageAutoRefreshProperties properties) {
        this.redisHelper = redisHelper;
        this.properties = properties;
    }

    public LanguageAutoRefreshProperties getProperties() {
        return properties;
    }

    @Override
    public void run() {
        String latestVersion;
        if (redisHelper == null
                || properties == null
                || !properties.isEnable()
                || (latestVersion = isLatestVersion()) == null) {
            return;
        }
        loadLatestVersionLanguages(latestVersion);
    }

    private String isLatestVersion() {
        String latestVersion = getLatestVersion();
        if (Objects.equals(latestVersion, version)) {
            logger.debug("[Language] The current language is already the latest version, skip refresh");
            return null;
        }
        if (version == null) {
            logger.debug("[Language] Loading language for the first time, current latest language version: {}", latestVersion);
        } else {
            logger.debug("[Language] Found language version update, current version: {}, latest version: {}", version, latestVersion);
        }
        return latestVersion;
    }

    public String getLatestVersion() {
        return SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> redisHelper.strGet(VERSION));
    }

    private void loadLatestVersionLanguages(String version) {
        List<Language> languages = Optional
                .ofNullable(SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> redisHelper.hshGetAll(LANGUAGE)))
                .orElseGet(Collections::emptyMap)
                .values()
                .stream()
                .map(value -> redisHelper.fromJson(value, Language.class))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(languages)) {
            logger.error("[Language] No language data was loaded when the language was updated to version: {}", version);
            return;
        }
        LanguageHelper.resetLanguages(languages);
        this.version = version;
        logger.info("[Language] Language version updated to: {}{}", this.version, languagesToString(languages));
    }

    private String languagesToString(List<Language> languages) {
        StringBuffer sb = new StringBuffer(String.format("%n    code / name / description%n"));
        languages.forEach(language ->
                sb.append(String.format("    %s /", language.getCode()))
                        .append(String.format(" %s /", language.getName()))
                        .append(String.format(" %s %n", language.getDescription()))
        );
        return sb.toString();
    }
}
