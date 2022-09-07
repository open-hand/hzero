package org.hzero.boot.iam.field.handler;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.iam.field.dto.FieldPermission;
import org.hzero.mybatis.domian.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * SecurityToken 对象递归遍历
 *
 * @author qingsheng.chen@hand-china.com
 */
public abstract class SecurityTokenObjectRecursiveHandler implements FieldPermissionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SecurityTokenObjectRecursiveHandler.class);

    protected void recursive(List<FieldPermission> fieldPermissionList, Object obj, Handler handler) {
        recursive(null, fieldPermissionList, obj, handler);
    }

    protected void recursive(String parentFieldName, List<FieldPermission> fieldPermissionList, Object obj, Handler handler) {
        if (obj == null) {
            return;
        }
        if (obj instanceof ResponseEntity){
            recursive(parentFieldName, fieldPermissionList, ((ResponseEntity) obj).getBody(), handler);
            return;
        }
        if (obj instanceof Collection) {
            for (Object item : (Collection<?>)obj){
                recursive(parentFieldName, fieldPermissionList, item, handler);
            }
            return;
        }
        if (obj instanceof SecurityToken && !CollectionUtils.isEmpty(fieldPermissionList)) {
            for (Field field : obj.getClass().getDeclaredFields()) {
                FieldPermission fieldPermission = shouldProcess(fieldPermissionList, getFieldName(parentFieldName, field.getName()));
                if (fieldPermission != null) {
                    try {
                        handler.handler(obj, field.getName(), fieldPermission);
                    } catch (Exception e) {
                        logger.error("Error process field.", e);
                    }
                }
                if (SecurityToken.class.isAssignableFrom(field.getType()) && !SecurityToken.class.equals(field.getType())) {
                    try {
                        recursive(getFieldName(parentFieldName, field.getName()), fieldPermissionList, FieldUtils.readDeclaredField(obj, field.getName(), true), handler);
                    } catch (IllegalAccessException e) {
                        logger.error("Error read field.", e);
                    }
                }
            }
        }
    }

    protected FieldPermission shouldProcess(List<FieldPermission> fieldPermissionList, String fieldName) {
        for (FieldPermission fieldPermission : fieldPermissionList) {
            if (Objects.equals(fieldName, fieldPermission.getFieldName())) {
                return fieldPermission;
            }
        }
        return null;
    }

    protected String getFieldName(String parentFieldName, String fieldName) {
        return parentFieldName != null ? parentFieldName + "." + fieldName : fieldName;
    }

    @FunctionalInterface
    public interface Handler {
        void handler(Object obj, String fieldName, FieldPermission fieldPermission) throws Exception;
    }
}
