package org.hzero.boot.iam.field.handler;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.iam.field.dto.FieldPermission;
import org.hzero.core.util.SensitiveUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段权限 - 脱敏
 *
 * @author qingsheng.chen@hand-china.com
 */
public class DesensitizationFieldHandler extends SecurityTokenObjectRecursiveHandler {
    private static final String PERMISSION_TYPE = "DESENSITIZE";

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void afterProcess(List<FieldPermission> fieldPermissionList, Object arg) {
        if (CollectionUtils.isEmpty(fieldPermissionList) || arg == null) {
            return;
        }
        recursive(fieldPermissionList
                        .stream()
                        .filter(item -> PERMISSION_TYPE.equals(item.getPermissionType()))
                        .collect(Collectors.toList()),
                arg,
                (obj, fieldName, fieldPermission) -> FieldUtils.writeDeclaredField(obj, fieldName, desensitization(obj, fieldName, fieldPermission.getPermissionRule()), true));
    }

    private String desensitization(Object obj, String fieldName, String rule) throws IllegalAccessException {
        if (obj == null) {
            return null;
        }
        Object fieldValue = FieldUtils.readDeclaredField(obj, fieldName, true);
        if (!(fieldValue instanceof String)) {
            throw new IllegalArgumentException("Field " + fieldName + " in " + obj.getClass() + " is not a String.class");
        }
        return SensitiveUtils.generateCipherTextByCipher((String) fieldValue, new String[]{rule}, '*', 0, 0);
    }
}
