package org.hzero.core.cache;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Reflections;

/**
 * ProcessCacheValue 切面
 *
 * @author bojiangzhou 2018/08/16
 * @see CacheValue
 * @see ProcessCacheValue
 */
@Aspect
@Order(20)
public class CacheValueAspect {

    private final RedisHelper redisHelper;
    private final Environment environment;

    public CacheValueAspect(RedisHelper redisHelper, Environment environment) {
        Assert.notNull(redisHelper, "redisHelper not be null.");
        Assert.notNull(environment, "environment not be null.");
        this.redisHelper = redisHelper;
        this.environment = environment;
    }

    private ObjectMapper mapper = BaseConstants.MAPPER;

    private static final Logger logger = LoggerFactory.getLogger(CacheValueAspect.class);

    private static final String FIELD_LANG = "lang";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_TENANT_ID = "tenantId";
    private static final String PLACEHOLDER_LANG = "{lang}";
    private static final String PLACEHOLDER_USER_ID = "{userId}";
    private static final String PLACEHOLDER_TENANT_ID = "{tenantId}";

    @AfterReturning(value = "@annotation(processCacheValue)", returning = "result")
    public Object afterReturning(JoinPoint joinPoint, ProcessCacheValue processCacheValue, Object result) throws Exception {
        if (result == null) {
            return null;
        }
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();
            if (body == null) {
                return result;
            }
            if (body instanceof Collection) {
                for (Object obj : (Collection<?>) body) {
                    processObject(obj);
                }
            } else {
                processObject(body);
            }
        } else if (result instanceof Collection) {
            for (Object obj : (Collection<?>) result) {
                processObject(obj);
            }
        } else {
            processObject(result);
        }

        return result;
    }

    private void processObject(Object obj) throws IllegalAccessException, IOException {
        if (!(obj instanceof Cacheable)) { return; }
        Field[] fields = Reflections.getAllField(obj.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(CacheValue.class)) {
                processObjectCacheValue(obj, fields, field, field.getAnnotation(CacheValue.class));
            }
            Reflections.makeAccessible(field);
            Object fieldValue = field.get(obj);
            if (fieldValue == null) {
                continue;
            }
            if (fieldValue instanceof Collection) {
                for (Object fv : (Collection<?>) fieldValue) {
                    processObject(fv);
                }
            } else {
                processObject(fieldValue);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processObjectCacheValue(Object target, Field[] targetFields, Field cacheValueField, CacheValue cacheValue) throws IllegalAccessException, IOException {
        int db = cacheValue.db();
        if (StringUtils.isNotBlank(cacheValue.dbAlias())) {
            try {
                String value = environment.resolveRequiredPlaceholders(cacheValue.dbAlias());
                db = Integer.parseInt(value);
            } catch (Exception e) {
                logger.warn("parse redis db alias error, use default db, dbAlias is {}", cacheValue.dbAlias(), e);
            }
        }

        String key = replacePlaceholder(cacheValue.key(), target, targetFields);

        Field primaryField = null;
        for (Field targetField : targetFields) {
            if (StringUtils.equals(cacheValue.primaryKey(), targetField.getName())) {
                primaryField = targetField;
                break;
            }
        }
        String primaryValue = null;
        if (primaryField != null) {
            Reflections.makeAccessible(primaryField);
            primaryValue = String.valueOf(primaryField.get(target));
        }
        primaryValue = StringUtils.defaultIfBlank(primaryValue, "null");

        // 对象中查找的属性
        String searchKey = cacheValue.searchKey();

        logger.debug("process cache value, key is [{}], primaryValue is [{}], db is [{}]", key, primaryValue, db);

        String searchValue = null;
        String json = null;
        Map<String, String> map = null;
        if (db > BaseConstants.NEGATIVE_ONE) {
            redisHelper.setCurrentDatabase(db);
        }
        switch (cacheValue.structure()) {
            case VALUE:
                searchValue = redisHelper.strGet(key);
                break;
            case OBJECT:
                json = redisHelper.strGet(key);
                if (StringUtils.isNotBlank(json)) {
                    map = mapper.readValue(json, HashMap.class);
                    searchValue = map.get(searchKey);
                }
                break;
            case MAP_VALUE:
                searchValue = redisHelper.hshGet(key, primaryValue);
                break;
            case MAP_OBJECT:
                json = redisHelper.hshGet(key, primaryValue);
                if (StringUtils.isNotBlank(json)) {
                    map = mapper.readValue(json, HashMap.class);
                    searchValue = map.get(searchKey);
                }
                break;
            case LIST_OBJECT:
                List<String> list = redisHelper.lstAll(key);
                for (String obj : list) {
                    map = mapper.readValue(obj, HashMap.class);
                    String mapKey = StringUtils.defaultIfBlank(cacheValue.primaryKeyAlias(), cacheValue.primaryKey());
                    if (StringUtils.equals(map.get(mapKey), primaryValue)) {
                        searchValue = map.get(searchKey);
                        break;
                    }
                }
                break;
            default:
        }
        if (db > BaseConstants.NEGATIVE_ONE) {
            redisHelper.clearCurrentDatabase();
        }
        Reflections.makeAccessible(cacheValueField);
        cacheValueField.set(target, searchValue);
    }

    private String replacePlaceholder(String key, Object target, Field[] targetFields) throws IllegalAccessException {
        String[] placeholders = StringUtils.substringsBetween(key, "{", "}");
        if (ArrayUtils.isEmpty(placeholders)) {
            return key;
        }

        Map<String, String> values = new HashMap<>(placeholders.length);
        for (Field targetField : targetFields) {
            for (String placeholder : placeholders) {
                if (StringUtils.equals(placeholder, targetField.getName())) {
                    Reflections.makeAccessible(targetField);
                    values.put(placeholder, String.valueOf(targetField.get(target)));
                    break;
                }
            }
        }

        for (String placeholder : placeholders) {
            if (values.containsKey(placeholder)) {
                key = StringUtils.replace(key, "{" + placeholder + "}", StringUtils.defaultIfBlank(values.get(placeholder), "null"));
            }
        }

        if (key.contains(PLACEHOLDER_LANG)) {
            String lang;
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null) {
                lang = details.getLanguage();
            } else {
                lang = LanguageHelper.language();
            }
            key = StringUtils.replace(key, PLACEHOLDER_LANG, String.valueOf(lang));
        }
        if (key.contains(PLACEHOLDER_USER_ID)) {
            Object userId;
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null) {
                userId = details.getUserId();
            } else {
                userId = "null";
            }
            key = StringUtils.replace(key, PLACEHOLDER_USER_ID, String.valueOf(userId));
        }
        if (key.contains(PLACEHOLDER_TENANT_ID)) {
            Object tenantId;
            CustomUserDetails details = DetailsHelper.getUserDetails();
            if (details != null) {
                tenantId = details.getOrganizationId().toString();
            } else {
                tenantId = BaseConstants.DEFAULT_TENANT_ID.toString();
            }
            key = StringUtils.replace(key, PLACEHOLDER_TENANT_ID, String.valueOf(tenantId));
        }

        return key;
    }


}
