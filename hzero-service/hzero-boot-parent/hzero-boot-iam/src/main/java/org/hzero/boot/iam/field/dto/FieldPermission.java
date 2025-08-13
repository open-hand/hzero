package org.hzero.boot.iam.field.dto;

import org.springframework.util.StringUtils;

/**
 * 字段权限缓存对象
 */
public class FieldPermission {
    private String fieldName;
    private String permissionType;
    private String permissionRule;

    public FieldPermission(String fieldName, String permissionType) {
        this.fieldName = fieldName;
        this.permissionType = permissionType;
    }

    public FieldPermission(String fieldName, String permissionType, String permissionRule) {
        this.fieldName = fieldName;
        this.permissionType = permissionType;
        this.permissionRule = permissionRule;
    }

    public static FieldPermission createFromCache(String cacheValue) {
        if (!StringUtils.hasText(cacheValue)) {
            return null;
        }
        String[] cacheValues = cacheValue.split(":");
        if (cacheValues.length == 2) {
            return new FieldPermission(cacheValues[0], cacheValues[1]);
        }
        if (cacheValues.length == 3) {
            return new FieldPermission(cacheValues[0], cacheValues[1], cacheValues[2]);
        }
        return null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldPermission setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public FieldPermission setPermissionType(String permissionType) {
        this.permissionType = permissionType;
        return this;
    }

    public String getPermissionRule() {
        return permissionRule;
    }

    public FieldPermission setPermissionRule(String permissionRule) {
        this.permissionRule = permissionRule;
        return this;
    }
}
