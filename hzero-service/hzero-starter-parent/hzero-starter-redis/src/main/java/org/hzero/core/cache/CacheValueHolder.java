package org.hzero.core.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;

/**
 * 从缓存中获取值，一般可用在getXxx方法里进行调用.
 * <p>
 * 每个方法都有一个参数key，根据该key查找对象，key中可使用 {tenantId} 和 {lang} 表示租户ID和语言，
 * 如 <code>key="hzero:hpfm:{tenantId}:{lang}" </code>。
 * 如果参数中包含 <font color=red>lang</font>(语言) 或 <font color=red>tenantId</font>(租户ID)，
 * 则 lang 默认取 {@link CustomUserDetails#getLanguage()} 或者 {@link LanguageHelper#language()}，
 * tenantId 取 {@link CustomUserDetails#getOrganizationId()} 或 {@link BaseConstants#DEFAULT_TENANT_ID} 。
 *
 * @author bojiangzhou 2018/08/16
 */
public class CacheValueHolder {

    public static final String PLACEHOLDER_LANG = "{lang}";
    public static final String PLACEHOLDER_TENANT_ID = "{tenantId}";

    private static final Logger logger = LoggerFactory.getLogger(CacheValueHolder.class);
    
    private CacheValueHolder() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * <code>K - V</code>
     * @param key key
     */
    public static String getValue(String key) {
        key = replacePlaceholder(key);
        return getRedisHelper().strGet(key);
    }

    /***
     * <code>K - {searchKey: value}</code>
     * @param key key
     * @param searchKey return key-value of this object
     */
    public static String getValueFromObject(String key, String searchKey) {
        key = replacePlaceholder(key);
        String json = getRedisHelper().strGet(key);
        if (StringUtils.isNotBlank(json)) {
            try {
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = getObjectMapper().readValue(json, HashMap.class);
                return map.get(searchKey);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * <code>K - Map&lt;primaryValue, value&gt;</code>
     * @param key key
     * @param primaryValue map key-value
     */
    public static String getValueFromMapValue(String key, Object primaryValue) {
        key = replacePlaceholder(key);
        return getRedisHelper().hshGet(key, String.valueOf(primaryValue));
    }

    /**
     * <code>K - Map&lt;primaryValue, {searchKey, value}&gt;</code>
     * @param key key
     * @param primaryValue map key
     * @param searchKey return this key of value in the object
     */
    public static String getValueFromMapObject(String key, Object primaryValue, String searchKey) {
        key = replacePlaceholder(key);
        String json = getRedisHelper().hshGet(key, String.valueOf(primaryValue));
        try {
            @SuppressWarnings("unchecked")
            HashMap<String, String> map = getObjectMapper().readValue(json, HashMap.class);
            return map.get(searchKey);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * <code>K - List&lt;{searchKey: value}&gt;</code>
     *
     * @param key key
     * @param primaryKey object key
     * @param primaryValue object value
     * @param searchKey return this key of value in the object
     */
    public static String getValueFromListObject(String key, String primaryKey, Object primaryValue, String searchKey) {
        key = replacePlaceholder(key);
        List<String> list = getRedisHelper().lstAll(key);
        for (String obj : list) {
            try {
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = getObjectMapper().readValue(obj, HashMap.class);
                if (map.get(primaryKey).equals(primaryValue)) {
                    return map.get(searchKey);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    private static RedisHelper getRedisHelper() {
        return ApplicationContextHelper.getContext().getBean(RedisHelper.class);
    }

    private static ObjectMapper getObjectMapper() {
        return ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
    }

    private static String replacePlaceholder(String key) {
        if (key.contains(PLACEHOLDER_LANG)) {
            String lang;
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null) {
                lang = details.getLanguage();
            } else {
                lang = LanguageHelper.language();
            }
            key = StringUtils.replace(key, PLACEHOLDER_LANG, lang);
        }
        if (key.contains(PLACEHOLDER_TENANT_ID)) {
            String tenantId = "";
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null) {
                tenantId = details.getOrganizationId().toString();
            } else {
                tenantId = BaseConstants.DEFAULT_TENANT_ID.toString();
            }
            key = StringUtils.replace(key, PLACEHOLDER_TENANT_ID, tenantId);
        }
        return key;
    }

}
