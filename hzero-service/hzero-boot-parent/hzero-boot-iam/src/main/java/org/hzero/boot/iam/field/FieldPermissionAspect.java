package org.hzero.boot.iam.field;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.iam.field.dto.FieldPermission;
import org.hzero.boot.iam.field.handler.FieldPermissionHandler;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.request.RequestMappingHelper;
import org.hzero.mybatis.domian.SecurityToken;

/**
 * 字段权限拦截器
 */
@Aspect
public class FieldPermissionAspect {
    private static final Logger logger = LoggerFactory.getLogger(FieldPermissionAspect.class);
    private static final String FIELD_PERMISSION_PREFIX = "hiam:field-permission";
    private static final String DIMENSION_USER = "user";
    private static final String DIMENSION_ROLE = "role";
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private String serviceName;
    private RedisHelper redisHelper;
    private List<FieldPermissionHandler> handlerList;

    public FieldPermissionAspect(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                 RedisHelper redisHelper,
                                 String serviceName) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.redisHelper = redisHelper;
        this.serviceName = serviceName;
    }

    public void addHandler(FieldPermissionHandler handler) {
        if (handlerList == null) {
            handlerList = new ArrayList<>();
        }
        handlerList.add(handler);
        handlerList = handlerList.stream()
                .sorted(Comparator.comparingInt(FieldPermissionHandler::getOrder))
                .collect(Collectors.toList());
    }

    @Pointcut("@annotation(io.choerodon.swagger.annotation.Permission)")
    public void apiPointcut() {

    }

    @Around("apiPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RequestMappingInfo requestMappingInfo = null;
        // before
        try {
            redisHelper.setCurrentDatabase(HZeroService.Iam.REDIS_DB);
            requestMappingInfo = getRequestMappingInfo();
            if (requestMappingInfo != null) {
                // 入参拦截
                List<FieldPermission> fieldPermissionList = getFieldPermission(requestMappingInfo, () -> getFieldNames(point.getArgs()));
                if (!CollectionUtils.isEmpty(handlerList)) {
                    handlerList.forEach(handler -> handler.beforeProcess(fieldPermissionList, point.getArgs()));
                }
            }
        } catch (Exception e) {
            logger.error("Field permission preprocessing error.", e);
        } finally {
            redisHelper.clearCurrentDatabase();
        }

        Object result = point.proceed();
        // after
        try {
            redisHelper.setCurrentDatabase(HZeroService.Iam.REDIS_DB);
            if (requestMappingInfo != null) {
                // 返回拦截
                List<FieldPermission> fieldPermissionList = getFieldPermission(requestMappingInfo, () -> getFieldNames(result));
                if (!CollectionUtils.isEmpty(handlerList) && !CollectionUtils.isEmpty(fieldPermissionList)) {
                    handlerList.forEach(handler -> handler.afterProcess(fieldPermissionList, result));
                }
            }
        } catch (Exception e) {
            logger.error("Field permission post processing error.", e);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return result;
    }

    private List<FieldPermission> getFieldPermission(RequestMappingInfo requestMappingInfo, ObtainFieldName obtainFieldName) {
        List<String> fieldNames = obtainFieldName.obtainFieldName();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return Collections.emptyList();
        }
        if (requestMappingInfo.userId != null) {
            List<String> fieldPermissions = redisHelper
                    .hshMultiGet(getFieldPermissionKey(requestMappingInfo.method, requestMappingInfo.path, DIMENSION_USER, requestMappingInfo.userId), fieldNames)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fieldPermissions)) {
                return fieldPermissions.stream().map(FieldPermission::createFromCache).collect(Collectors.toList());
            }
        }
        if (requestMappingInfo.roleId != null) {
            List<String> fieldPermissions = redisHelper
                    .hshMultiGet(getFieldPermissionKey(requestMappingInfo.method, requestMappingInfo.path, DIMENSION_ROLE, requestMappingInfo.roleId), fieldNames)
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fieldPermissions)) {
                return fieldPermissions.stream().map(FieldPermission::createFromCache).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private RequestMappingInfo getRequestMappingInfo() {
        RequestMappingInfo requestMappingInfo = null;
        try {
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            RequestMappingHelper.BestMatchRequestMapping bestMatchRequestMapping = RequestMappingHelper.getBestMatchRequestMapping(requestMappingHandlerMapping);
            if (userDetails != null && userDetails.getUserId() != null && userDetails.getRoleId() != null && bestMatchRequestMapping != null) {
                requestMappingInfo = new RequestMappingInfo(bestMatchRequestMapping.getMethod().toLowerCase(),
                        bestMatchRequestMapping.getPath(), userDetails.getUserId(), userDetails.getRoleId());
            }
        } catch (Exception e) {
            logger.error("Error get field permission.", e);
        }
        return requestMappingInfo;
    }

    private List<String> getFieldNames(Object[] args) {
        if (args == null || args.length == 0) {
            return Collections.emptyList();
        }
        List<String> fieldNames = new ArrayList<>();
        for (Object arg : args) {
            fieldNames.addAll(getFieldNames(arg));
        }
        return fieldNames;
    }

    private List<String> getFieldNames(Object arg) {
        List<String> fieldNames = new ArrayList<>();
        getFieldNames(null, arg, fieldNames);
        return fieldNames;
    }

    private void getFieldNames(String fieldPrefix, Object arg, List<String> fieldNames) {
        if (arg == null) {
            return;
        }
        if (arg instanceof ResponseEntity) {
            getFieldNames(fieldPrefix, ((ResponseEntity) arg).getBody(), fieldNames);
            return;
        }
        if (arg instanceof Collection) {
            Collection<?> collection = (Collection<?>) arg;
            getFieldNames(fieldPrefix, collection.stream().findFirst().orElse(null), fieldNames);
            return;
        }
        Class<?> argClass = arg.getClass();
        while (SecurityToken.class.isAssignableFrom(argClass) && !SecurityToken.class.equals(argClass)) {
            for (Field field : argClass.getDeclaredFields()) {
                // 忽略静态或者final字段
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                if (SecurityToken.class.isAssignableFrom(field.getType()) && !SecurityToken.class.equals(field.getType())) {
                    try {
                        getFieldNames(getFieldPrefix(fieldPrefix, field.getName()), FieldUtils.readField(field, arg, true), fieldNames);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    fieldNames.add(getFieldName(fieldPrefix, field.getName()));
                }
            }
            argClass = argClass.getSuperclass();
        }
    }

    private String getFieldPrefix(String parentFieldPrefix, String fieldPrefix) {
        return parentFieldPrefix != null ? parentFieldPrefix + "." + fieldPrefix : fieldPrefix;
    }

    private String getFieldName(String fieldPrefix, String fieldName) {
        return fieldPrefix != null ? fieldPrefix + "." + fieldName : fieldName;
    }


    private String getFieldPermissionKey(String method, String path, String permissionDimension, long dimensionValue) {
        return String.format("%s:%s:%s:%s:%s:%d", FIELD_PERMISSION_PREFIX,
                serviceName,
                method,
                path,
                permissionDimension,
                dimensionValue);
    }

    private static class RequestMappingInfo {
        private String method;
        private String path;
        private Long userId;
        private Long roleId;

        RequestMappingInfo(String method, String path, long userId, long roleId) {
            this.method = method;
            this.path = path;
            this.userId = userId;
            this.roleId = roleId;
        }
    }

    @FunctionalInterface
    private interface ObtainFieldName {
        List<String> obtainFieldName();
    }
}
