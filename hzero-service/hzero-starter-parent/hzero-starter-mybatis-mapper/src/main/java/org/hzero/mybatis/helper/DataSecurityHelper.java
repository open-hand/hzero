package org.hzero.mybatis.helper;

import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityField;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.FieldHelper;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.mybatis.security.DataSecurityInterceptor;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 数据安全工具
 * </p>
 *
 * @author qingsheng.chen 2018/9/19 星期三 19:37
 */
public class DataSecurityHelper {
    private DataSecurityHelper() {
    }

    private static final Map<Class<?>, EntityTable> ENTITY_CLASS_TABLE_MAP = new HashMap<>();

    /**
     * 关闭安全校验
     */
    public static void close() {
        DataSecurityInterceptor.SECURITY_ENABLE.set(false);
    }

    /**
     * 开启安全校验
     */
    public static void open() {
        DataSecurityInterceptor.SECURITY_ENABLE.set(true);
    }

    public static void clear() {
        DataSecurityInterceptor.SECURITY_ENABLE.remove();
    }

    public static Set<EntityColumn> getDataSecurityColumns(Class<?> entityClass) {
        if (EntityHelper.contain(entityClass)) {
            return EntityHelper.getTableByEntity(entityClass).getDataSecurityColumns();
        }
        return getEntityTable(entityClass).getDataSecurityColumns();
    }

    public static EntityTable getEntityTable(Class<?> entityClass) {
        if (!ENTITY_CLASS_TABLE_MAP.containsKey(entityClass)) {
            initEntityNameMap(entityClass);
        }
        return ENTITY_CLASS_TABLE_MAP.get(entityClass);
    }

    public static Set<EntityField> getEntityFields(Class<?> entityClass) {
        if (EntityHelper.contain(entityClass)) {
            return EntityHelper.getTableByEntity(entityClass).getFieldSet();
        }
        return getEntityTable(entityClass).getFieldSet();
    }

    public static synchronized void initEntityNameMap(Class<?> entityClass) {
        if (ENTITY_CLASS_TABLE_MAP.containsKey(entityClass)) {
            return;
        }
        EntityTable entityTable = new EntityTable(entityClass);
        entityTable.setEntityClassColumns(new LinkedHashSet<>());
        entityTable.setDataSecurityColumns(new LinkedHashSet<>());
        for (EntityField field : FieldHelper.getAll(entityClass)) {
            entityTable.appendField(field);
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            // 数据加密
            EntityColumn entityColumn = new EntityColumn(entityTable);
            if (field.isAnnotationPresent(DataSecurity.class)) {
                entityColumn.setProperty(field.getName());
                entityTable.setDataSecurity(true);
                entityTable.addDataSecurityColumns(entityColumn);
            }
            entityTable.getEntityClassColumns().add(entityColumn);
        }
        ENTITY_CLASS_TABLE_MAP.put(entityClass, entityTable);
    }

    public static String encrypt(String value) {
        return DataSecurityInterceptor.encrypt(value);
    }

    public static String decrypt(String value) {
        return DataSecurityInterceptor.decrypt(value);
    }
}
