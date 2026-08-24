package org.hzero.platform.infra.task;

import org.hzero.boot.platform.language.LanguageAutoRefreshConstant;
import org.hzero.boot.platform.language.autoconfigure.LanguageAutoRefreshProperties;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.core.util.SystemClock;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.domain.entity.Language;
import org.hzero.platform.infra.mapper.HpfmLanguageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qingsheng.chen@hand-china.com 2021/6/9 14:04
 */
public class LanguageAutoUpdateTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(LanguageAutoUpdateTask.class);

    private static final String VERSION = LanguageAutoRefreshConstant.LANGUAGE_AUTO_REFRESH_VERSION_KEY;
    private static final String LANGUAGE = LanguageAutoRefreshConstant.LANGUAGE_AUTO_REFRESH_LANGUAGE_KEY;
    private static final String LOCK = LanguageAutoRefreshConstant.LANGUAGE_AUTO_REFRESH_LOCK_KEY;

    private final RedisHelper redisHelper;
    private final LanguageAutoRefreshProperties properties;
    private final HpfmLanguageMapper languageMapper;

    private volatile String version;


    public LanguageAutoUpdateTask(RedisHelper redisHelper,
                                  LanguageAutoRefreshProperties properties,
                                  HpfmLanguageMapper languageMapper) {
        this.redisHelper = redisHelper;
        this.properties = properties;
        this.languageMapper = languageMapper;
    }

    public LanguageAutoRefreshProperties getProperties() {
        return properties;
    }

    @Override
    public void run() {
        if (redisHelper == null
                || properties == null
                || !properties.isEnable()
                || !getLock()) {
            return;
        }
        loadLatestVersionLanguages();
        releaseLock();
    }

    private boolean getLock() {
        boolean locked = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.strSetIfAbsent(LOCK, String.valueOf(SystemClock.now())));
        Long expire = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.getExpire(LOCK));
        if (locked) {
            logger.debug("[Language] Start loading the latest language data from the database.");
        }
        if (expire == -1L) {
            long expireSeconds = properties.getInterval().getSeconds() << 1;
            logger.debug("[Language] To prevent deadlock, add a timeout period to the lock: {}", expireSeconds);
            SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                    () -> redisHelper.setExpire(LOCK, expireSeconds));
        }
        return locked;
    }

    private void releaseLock() {
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.delKey(LOCK));
        logger.debug("[Language] Release lock.");
    }

    private void loadLatestVersionLanguages() {
        SecurityTokenHelper.close();
        List<Language> languageList = languageMapper.selectAll();
        Map<String, String> languages = languageList.stream()
                .collect(Collectors.toMap(Language::getCode, redisHelper::toJson));
        SecurityTokenHelper.clear();
        if (CollectionUtils.isEmpty(languages)) {
            logger.error("[Language] No language loaded from the database.");
            return;
        }
        String languagesValue = StringUtils.collectionToCommaDelimitedString(languages.values());
        this.version = EncryptionUtils.MD5.encrypt(languagesValue);
        if (isLatestVersion() != null) {
            logger.info("[Language] The current version language is different from the last update, update the language cache: {}", this.version);
            updateLatestVersion();
            updateLanguages(languages);
            logger.info("[Language] Language version updated to: {}{}", this.version, languagesToString(languageList));
        } else {
            logger.info("[Language] The current language version is the same as the last loaded language version: {}, skip the update.", this.version);
        }
    }

    private String isLatestVersion() {
        String latestVersion = getLatestVersion();
        if (Objects.equals(latestVersion, version)) {
            logger.debug("[Language] The current language is already the latest version, skip refresh");
            return null;
        }
        logger.debug("[Language] The current version does not match the latest version, the updated version: {} to {}", latestVersion, this.version);
        return this.version;
    }

    public String getLatestVersion() {
        return SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> redisHelper.strGet(VERSION));
    }

    public void updateLatestVersion() {
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> redisHelper.strSet(VERSION, this.version));
    }

    private void updateLanguages(Map<String, String> languages) {
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> redisHelper.hshPutAll(LANGUAGE, languages));
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
