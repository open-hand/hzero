package org.hzero.boot.iam.field.handler;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.iam.field.dto.FieldPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段权限 - 隐藏
 *
 * @author qingsheng.chen@hand-china.com
 */
public class HiddenFieldHandler extends SecurityTokenObjectRecursiveHandler {
    private static final Logger logger = LoggerFactory.getLogger(HiddenFieldHandler.class);
    private static final String PERMISSION_TYPE = "HIDE";


    @Override
    public int getOrder() {
        return 0;
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
                (obj, fieldName, fieldPermission) -> FieldUtils.writeDeclaredField(obj, fieldName, null, true));
    }

}
