package org.hzero.boot.platform.data.permission.util;

import io.choerodon.core.oauth.CustomUserDetails;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 处理数据屏蔽中的动态值
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/01 15:26
 */
public class DynamicValueUtils {

    private static final Logger logger = LoggerFactory.getLogger(DynamicValueUtils.class);
    private static final String ROLE_MERGE_IDS = "roleMergeIds";
    private static final String TABLE_ALIAS = "tableAlias";
    private static final String ADDITION_INFO_PREFIX = "additionInfo.";
    private static final Set<String> USER_DETAILS_FIELDS = FieldUtils.getAllFieldsList(CustomUserDetails.class)
            .stream().map(Field::getName).collect(Collectors.toSet());

    private DynamicValueUtils() {

    }

    /**
     * 根据字段名从UserInfo中得到值
     *
     * @param field field
     * @return value
     */
    public static Object getValue(String field, Map args, CustomUserDetails userDetails, String tableAlias) {
        try {
            if (TABLE_ALIAS.equals(field)) {
                return tableAlias;
            }
            if (USER_DETAILS_FIELDS.contains(field)) {
                return FieldUtils.readDeclaredField(userDetails, field, true);
            }
            if (args.containsKey(field)) {
                return args.get(field);
            }
            if (ROLE_MERGE_IDS.equals(field)) {
                return userDetails.roleMergeIds();
            }
            if (StringUtils.isNotEmpty(field) && field.startsWith(ADDITION_INFO_PREFIX)) {
                return userDetails.getAdditionInfo() == null ? null : userDetails.getAdditionInfo().get(field.substring(ADDITION_INFO_PREFIX.length()));
            }
            return FieldUtils.readDeclaredField(userDetails, field, true);
        } catch (Exception e) {
            logger.error("CustomUserDetails don't have " + field + " field!");
            return field;
        }
    }
}
