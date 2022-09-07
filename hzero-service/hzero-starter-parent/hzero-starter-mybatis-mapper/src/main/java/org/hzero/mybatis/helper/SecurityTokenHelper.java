package org.hzero.mybatis.helper;

import java.util.*;
import javax.persistence.Id;
import javax.persistence.Transient;

import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityField;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.FieldHelper;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.mybatis.security.SecurityTokenInterceptor;
import org.hzero.mybatis.util.SecurityTokenUtils;

/**
 * <p>
 * 安全令牌 Helper
 * </p>
 *
 * @author qingsheng.chen 2018/9/10 星期一 14:08
 */
public class SecurityTokenHelper {
    private SecurityTokenHelper() {
    }

    public static void open() {
        SecurityTokenInterceptor.SECURITY_ENABLE.set(true);
    }

    public static void close() {
        SecurityTokenInterceptor.SECURITY_ENABLE.set(false);
    }

    public static void clear() {
        SecurityTokenInterceptor.SECURITY_ENABLE.remove();
    }

    private static final Map<Class<?>, EntityTable> ENTITY_CLASS_TABLE_MAP = new HashMap<>();

    /**
     * 校验 Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj) {
        SecurityTokenUtils.validToken(obj);
    }

    /**
     * 校验 Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validTokenIgnoreInsert(T obj) {
        SecurityTokenUtils.validTokenIgnoreInsert(obj);
    }

    /**
     * 校验 Token
     *
     * @param obj        对象
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj, boolean validChild) {
        SecurityTokenUtils.validToken(obj, validChild);
    }

    /**
     * 校验 Token
     *
     * @param obj        对象
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj, boolean validChild, boolean ignoreInsert) {
        SecurityTokenUtils.validToken(obj, validChild, ignoreInsert);
    }

    /**
     * 校验 Token
     *
     * @param objs 对象列表
     * @param <T>  实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs) {
        SecurityTokenUtils.validToken(objs);
    }

    /**
     * 校验 Token
     *
     * @param objs 对象列表
     * @param <T>  实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validTokenIgnoreInsert(Collection<T> objs) {
        SecurityTokenUtils.validTokenIgnoreInsert(objs);
    }

    /**
     * 校验 Token
     *
     * @param objs       对象列表
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs, boolean validChild) {
        SecurityTokenUtils.validToken(objs, validChild);
    }

    /**
     * 校验 Token
     *
     * @param objs       对象列表
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs, boolean validChild, boolean ignoreInsert) {
        SecurityTokenUtils.validToken(objs, validChild, ignoreInsert);
    }

    public static Set<EntityColumn> getPkColumns(Class<? extends SecurityToken> entityClass) {
        if (EntityHelper.contain(entityClass)) {
            return EntityHelper.getPkColumns(entityClass);
        }
        return getEntityTable(entityClass).getEntityClassPkColumns();
    }

    public static EntityTable getEntityTable(Class<? extends SecurityToken> entityClass) {
        if (!ENTITY_CLASS_TABLE_MAP.containsKey(entityClass)) {
            initEntityNameMap(entityClass);
        }
        return ENTITY_CLASS_TABLE_MAP.get(entityClass);
    }

    public static Set<EntityField> getEntityFields(Class<? extends SecurityToken> entityClass) {
        if (EntityHelper.contain(entityClass)) {
            return EntityHelper.getTableByEntity(entityClass).getFieldSet();
        }
        return getEntityTable(entityClass).getFieldSet();
    }

    public static Set<EntityField> getSecurityTokenFieldSet(Class<? extends SecurityToken> entityClass) {
        if (EntityHelper.contain(entityClass)) {
            return EntityHelper.getTableByEntity(entityClass).getSecurityTokenFieldSet();
        }
        return getEntityTable(entityClass).getSecurityTokenFieldSet();
    }

    public static synchronized void initEntityNameMap(Class<? extends SecurityToken> entityClass) {
        if (ENTITY_CLASS_TABLE_MAP.containsKey(entityClass)) {
            return;
        }
        EntityTable entityTable = new EntityTable(entityClass);
        entityTable.setEntityClassColumns(new LinkedHashSet<>());
        entityTable.setEntityClassPkColumns(new LinkedHashSet<>());
        for (EntityField field : FieldHelper.getAll(entityClass)) {
            entityTable.appendField(field);
            if (field.isAnnotationPresent(Transient.class)) {
                // 字段继承自 SecurityToken 或 对象继承自 Collection
                if (SecurityToken.class.isAssignableFrom(field.getJavaType()) || Collection.class.isAssignableFrom(field.getJavaType())) {
                    entityTable.addSecurityTokenField(field);
                }
                continue;
            }
            //Id
            EntityColumn entityColumn = new EntityColumn(entityTable);
            entityColumn.setField(field);
            if (field.isAnnotationPresent(Id.class)) {
                entityColumn.setId(true);
            }
            if (entityColumn.isId()) {
                entityTable.getEntityClassPkColumns().add(entityColumn);
            }
            entityTable.getEntityClassColumns().add(entityColumn);
        }
        ENTITY_CLASS_TABLE_MAP.put(entityClass, entityTable);
    }
}
