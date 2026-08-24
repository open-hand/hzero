package org.hzero.oauth.domain.utils;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;

/**
 *
 * @author bojiangzhou 2019/10/22
 */
@Component
public class ConfigGetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGetter.class);

    @Autowired
    private Environment environment;
    @Autowired
    private RedisHelper redisHelper;

    public String getValue(ProfileCode profile) {
        return getValue(BaseConstants.DEFAULT_TENANT_ID, profile);
    }

    public String getValue(long tenantId, ProfileCode profile) {
        String profileKey = profile.profileKey();
        String value = null;
        if (StringUtils.isNotBlank(profileKey)) {
            try {
                value = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, () -> {
                    String key = HZeroService.Platform.CODE + ":config:" + profileKey + "." + tenantId;
                    return redisHelper.strGet(key);
                });
            } catch (Exception e) {
                LOGGER.error("get profile error, profile code is {}", profile.profileKey(), e);
            }
        }
        if (StringUtils.isBlank(value)) {
            value = environment.getProperty(profile.configKey(), profile.defaultValue());
        }
        return value;
    }

    public boolean isTrue(ProfileCode profile) {
        return isTrue(BaseConstants.DEFAULT_TENANT_ID, profile);
    }

    public boolean isTrue(long tenantId, ProfileCode profile) {
        String value = getValue(tenantId, profile);
        if (StringUtils.isNumeric(value)) {
            return Integer.parseInt(value) > 0;
        }
        return Boolean.parseBoolean(value);
    }

    public Set<String> getSet(ProfileCode profile) {
        return getSet(BaseConstants.DEFAULT_TENANT_ID, profile);
    }

    public Set<String> getSet(long tenantId, ProfileCode profile) {
        String value = getValue(tenantId, profile);
        if (StringUtils.isNotBlank(value)) {
            return Stream.of(value.split(",")).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

}
