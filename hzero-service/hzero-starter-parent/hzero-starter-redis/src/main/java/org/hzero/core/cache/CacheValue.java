package org.hzero.core.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;

/**
 * 根据实体某个字段的值从缓存中获取对应的值，比如根据用户ID查找用户名、根据KEY查找LOV的值... <p></p>
 * 该注解的实体类需要实现 {@link Cacheable} 接口，标识可从缓存中取值. <p></p>
 * 该注解功能默认不启用，要启用需配置 <code>"hzero.use-cache-value=true"</code> <p></p>
 * 要求存储的数据结构是 {@link DataStructure}
 *
 * @author bojiangzhou 2018/08/15
 * @see Cacheable
 * @see CacheValueAspect
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheValue {

    /**
     * 缓存的key，根据该key查找对象，key可以使用占位符，如 <code>key="hpfm:lov:values:{code}:{lang}" </code>
     * 如果有占位符存在，需保证当前对象中有相应的参数的K-V，K即为占位符的名字；
     * 如果参数中包含 <font color=red>lang</font>(语言) 或 <font color=red>tenantId</font>(租户ID)，
     * 且没有传参数，则 lang 默认取 {@link CustomUserDetails#getLanguage()} 或者 {@link LanguageHelper#language()}，
     * tenantId 取 {@link CustomUserDetails#getOrganizationId()} 或 {@link BaseConstants#DEFAULT_TENANT_ID} 。
     */
    String key();

    /**
     * 指定为实体的某个字段名，根据该字段的值作为主键查找
     */
    String primaryKey() default "";

    /**
     * primaryKey 的别名，一般实体的字段和缓存的字段不一致时，可以指定缓存中对应的别名。
     */
    String primaryKeyAlias() default "";

    /**
     * Object 查找属性的KEY
     */
    String searchKey() default "";

    /**
     * 缓存的数据结构
     */
    DataStructure structure() default DataStructure.VALUE;

    /**
     * 指定缓存的DB，-1即为默认
     */
    int db() default BaseConstants.NEGATIVE_ONE;

    /**
     * 指定 redis db 别名，可以使用配置的形式传入，如：${hzero.service.platform.redis-db}
     */
    String dbAlias() default "";

    /**
     * 数据结构
     */
    enum DataStructure {
        /**
         * <code>K - V</code>
         */
        VALUE,
        /**
         * <code>K - {searchKey: value}</code>
         */
        OBJECT,
        /**
         * <code>K - Map&lt;primaryValue, value&gt;</code>
         */
        MAP_VALUE,
        /**
         * <code>K - Map&lt;primaryValue, {searchKey, value}&gt;</code>
         */
        MAP_OBJECT,
        /**
         * <code>K - List&lt;{searchKey: value}&gt;</code>
         */
        LIST_OBJECT
    }

}
