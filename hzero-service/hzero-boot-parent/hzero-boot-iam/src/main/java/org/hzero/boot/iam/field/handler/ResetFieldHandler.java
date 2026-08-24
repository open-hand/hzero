package org.hzero.boot.iam.field.handler;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.iam.field.config.FieldPermissionProperty;
import org.hzero.boot.iam.field.dto.FieldPermission;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.domian.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 将出参中屏蔽的字段进行缓存，然后在入参中恢复
 *
 * @author qingsheng.chen@hand-china.com
 */
public class ResetFieldHandler implements FieldPermissionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResetFieldHandler.class);
    private static final String TOKEN_CACHE_PREFIX = HZeroService.Iam.CODE + ":field-permission:token:";
    private RedisHelper redisHelper;
    private FieldPermissionProperty fieldPermissionProperty;

    public ResetFieldHandler(RedisHelper redisHelper,
                             FieldPermissionProperty fieldPermissionProperty) {
        this.redisHelper = redisHelper;
        this.fieldPermissionProperty = fieldPermissionProperty;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void beforeProcess(List<FieldPermission> fieldPermissionList, Object[] args) {
        if (args != null) {
            for (Object arg : args) {
                if (arg == null) {
                    return;
                }
                if (arg instanceof Collection) {
                    beforeProcess(fieldPermissionList, ((Collection<?>) arg).toArray());
                    return;
                }
                if (arg instanceof SecurityToken) {
                    String token = ((SecurityToken) arg).get_token();
                    if (StringUtils.hasText(token)) {
                        String cacheValue = redisHelper.strGet(getTokenCacheKey(token));
                        if (StringUtils.isEmpty(cacheValue)) {
                            continue;
                        }
                        try {
                            resetValue(arg.getClass(), arg, cacheValue);
                        } catch (IOException | IllegalAccessException e) {
                            logger.error("Error read cache value.", e);
                        }
                    }
                }
            }
        }
    }

    private <T> void resetValue(Class<?> clazz, T obj, String json) throws IOException, IllegalAccessException {
        Iterator<String> fieldNames = RedisHelper.getObjectMapper().readTree(json).fieldNames();
        Object value = RedisHelper.getObjectMapper().readValue(json, clazz);
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            FieldUtils.writeDeclaredField(obj, fieldName, FieldUtils.readDeclaredField(value, fieldName, true), true);
        }
    }


    @Override
    public void afterProcess(List<FieldPermission> fieldPermissionList, Object arg) {
        if (arg == null) {
            return;
        }
        if (arg instanceof ResponseEntity) {
            afterProcess(fieldPermissionList, ((ResponseEntity) arg).getBody());
            return;
        }
        if (arg instanceof Collection) {
            for (Object item : (Collection<?>) arg) {
                afterProcess(fieldPermissionList, item);
            }
            return;
        }
        if (CollectionUtils.isEmpty(fieldPermissionList) || !(arg instanceof SecurityToken)) {
            return;
        }
        String token = ((SecurityToken) arg).get_token();
        if (StringUtils.hasText(token)) {
            handler(null, (SecurityToken) arg, getTokenCacheKey(token), fieldPermissionList);
        }
    }

    private void handler(String parentFieldName, SecurityToken obj, String tokenKey, List<FieldPermission> fieldPermissionList) {
        Map<String, Object> cacheMap = new HashMap<>(fieldPermissionList.size());
        Map<String, Field> fieldMap = getAllFieldWithoutStaticAndFinal(obj.getClass()).stream()
                .collect(Collectors.toMap(item -> getFieldName(parentFieldName, item.getName()), Function.identity(), (o, n) -> o));
        for (FieldPermission fieldPermission : fieldPermissionList) {
            if (fieldMap.containsKey(fieldPermission.getFieldName())) {
                try {
                    cacheMap.put(fieldPermission.getFieldName(), FieldUtils.readField(fieldMap.get(fieldPermission.getFieldName()), obj, true));
                } catch (IllegalAccessException e) {
                    logger.error("Error read field value.", e);
                }
            }
        }
        for (Field field : fieldMap.values()) {
            if (SecurityToken.class.isAssignableFrom(field.getType())) {
                try {
                    SecurityToken securityToken = (SecurityToken) FieldUtils.readField(field, obj, true);
                    if (securityToken != null && StringUtils.hasText(securityToken.get_token())) {
                        handler(field.getName(), securityToken, getTokenCacheKey(securityToken.get_token()), fieldPermissionList);
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Error read field value.", e);
                }
            }
        }
        if (!CollectionUtils.isEmpty(cacheMap)) {
            redisHelper.objectSet(tokenKey, cacheMap);
            redisHelper.setExpire(tokenKey, fieldPermissionProperty.getExpire());
        }
    }

    private String getTokenCacheKey(String token) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Assert.notNull(userDetails, "UserDetails is null.");
        return TOKEN_CACHE_PREFIX + token + ":" + userDetails.getUserId() + "." + userDetails.getRoleId();
    }

    private String getFieldName(String parentFieldName, String fieldName) {
        return parentFieldName != null ? parentFieldName + "." + fieldName : fieldName;
    }

    private List<Field> getAllFieldWithoutStaticAndFinal(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                fieldList.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }
}

