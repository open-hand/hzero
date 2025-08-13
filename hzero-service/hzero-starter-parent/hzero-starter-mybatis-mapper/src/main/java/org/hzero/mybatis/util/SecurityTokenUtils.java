package org.hzero.mybatis.util;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityField;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.mybatis.config.SecurityTokenProperty;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.mybatis.domian.SecurityTokenEntity;
import org.hzero.mybatis.exception.SecurityTokenException;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * 数据防篡改工具类
 * 提供获取，生成和校验 _token 字段的方法
 * </p>
 *
 * @author qingsheng.chen 2018/9/7 星期五 15:38
 */
@SuppressWarnings("all")
public class SecurityTokenUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityTokenUtils.class);
    private static final String SPLIT = ":";
    private volatile static String applicationName;
    private volatile static String contextPath;
    private volatile static String securityKey;

    private SecurityTokenUtils() {
    }

    private static boolean noContext() {
        return ApplicationContextHelper.getContext() == null;
    }

    public static String getApplicationName() {
        if (!StringUtils.hasText(applicationName)) {
            applicationName = ApplicationContextHelper.getContext().getEnvironment().getProperty("spring.application.name");
        }
        return applicationName;
    }

    public static String getContextPath() {
        if (contextPath == null) {
            contextPath = ApplicationContextHelper.getContext().getEnvironment().getProperty("server.servlet.context-path", "");
        }
        return contextPath;
    }

    public static String getSecurityKey() {
        if (!StringUtils.hasText(securityKey)) {
            SecurityTokenProperty securityTokenProperties = ApplicationContextHelper.getContext().getBean(SecurityTokenProperty.class);
            if (securityTokenProperties == null) {
                throw new SecurityTokenException("Invalid or empty security key config.");
            }
            securityKey = securityTokenProperties.getSecurityKey();
        }
        return securityKey;
    }

    public static String getSerializeString(SecurityTokenEntity obj) {
        StringBuilder sb = new StringBuilder(obj.getApplicationName()).append("&")
                .append(StringUtils.hasText(obj.getContextPath()) ? obj.getContextPath() + "&" : "")
                .append(obj.getClassName()).append("&");
        if (!CollectionUtils.isEmpty(obj.getPkValue())) {
            obj.getPkValue().forEach((field, value) -> sb.append(field).append(":").append(value).append("#"));
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public static SecurityTokenEntity getDserializeSecurityTokenEntity(String str) {
        SecurityTokenEntity securityTokenEntity = null;
        if (StringUtils.hasText(str)) {
            String[] entityStr = str.split("&");
            securityTokenEntity = new SecurityTokenEntity().setApplicationName(entityStr[0]);
            if (entityStr.length > 1 && entityStr.length == 4) {
                securityTokenEntity.setContextPath(entityStr[1]);
            }
            if (entityStr.length > 1 && entityStr.length == 3) {
                securityTokenEntity.setClassName(entityStr[1]);
            } else if (entityStr.length > 2 && entityStr.length == 4) {
                securityTokenEntity.setClassName(entityStr[2]);
            }
            if (entityStr.length > 2 && entityStr.length == 3) {
                String[] pkValueStr = entityStr[2].split("#");
                for (String pkValue : pkValueStr) {
                    if (StringUtils.hasText(pkValue)) {
                        String[] pk = pkValue.split(":");
                        securityTokenEntity.addPkValue(pk[0], pk.length > 1 ? pk[1] : null);
                    }
                }
            } else if (entityStr.length > 3 && entityStr.length == 4) {
                String[] pkValueStr = entityStr[3].split("#");
                for (String pkValue : pkValueStr) {
                    if (StringUtils.hasText(pkValue)) {
                        String[] pk = pkValue.split(":");
                        securityTokenEntity.addPkValue(pk[0], pk.length > 1 ? pk[1] : null);
                    }
                }
            }

        }
        return securityTokenEntity;
    }

    /**
     * 批量生成安全令牌，设置 Token
     *
     * @param objs 对象列表
     * @param <T>  实现安全令牌接口的泛型
     * @return 设置 Token
     */
    public static <T extends SecurityToken> Collection<T> setToken(Collection<T> objs) {
        for (T obj : objs) {
            setToken(obj);
        }
        return objs;
    }

    /**
     * 设置设置 Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     * @return 设置 Token
     */
    public static <T extends SecurityToken> T setToken(T obj) {
        if (obj == null) {
            return obj;
        }
        obj.set_token(generateToken(obj));
        Set<EntityField> securityTokenSet = SecurityTokenHelper.getSecurityTokenFieldSet(obj.getClass());
        if (!CollectionUtils.isEmpty(securityTokenSet)) {
            for (EntityField field : securityTokenSet) {
                try {
                    if (SecurityToken.class.isAssignableFrom(field.getJavaType())) {
                        Object object = FieldUtils.readField(obj, field.getName(), true);
                        setToken((SecurityToken) object);
                    } else if (Collection.class.isAssignableFrom(field.getJavaType())) {
                        Collection<?> list = (Collection<?>) FieldUtils.readField(obj, field.getName(), true);
                        if (!CollectionUtils.isEmpty(list)) {
                            for (Object item : list) {
                                if (!(item instanceof SecurityToken)) {
                                    break;
                                } else {
                                    setToken((SecurityToken) item);
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    logger.error("Unable to read field {} : {}", field.getName(), ExceptionUtils.getStackFrames(e));
                }
            }
        }
        return obj;
    }

    /**
     * 生成Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     * @return Token
     */
    public static <T extends SecurityToken> String generateToken(T obj) {
        if (noContext()) {
            logger.debug("ApplicationContext not found. Application may be starting.");
            return null;
        }
        SecurityTokenEntity securityTokenEntity = new SecurityTokenEntity()
                .setApplicationName(getApplicationName())
                .setContextPath(getContextPath())
                .setClassName(getEntityClass(obj).getName());
        for (EntityColumn pkColumn : SecurityTokenHelper.getPkColumns(getEntityClass(obj))) {
            try {
                securityTokenEntity.addPkValue(pkColumn.getField().getName(), String.valueOf(FieldUtils.readField(obj, pkColumn.getField().getName(), true)));
            } catch (IllegalAccessException e) {
                logger.error("Get field value error : {}", ExceptionUtils.getStackTrace(e));
            }
        }
        try {
            return EncryptionUtils.AES.encrypt(getSerializeString(securityTokenEntity), getSecurityKey());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public static SecurityTokenEntity getEntity(String token) {
        if (noContext()) {
            logger.debug("ApplicationContext not found. Application may be starting.");
            return null;
        }
        try {
            return getDserializeSecurityTokenEntity(EncryptionUtils.AES.decrypt(token, getSecurityKey()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 校验 Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj) {
        validToken(obj, true);
    }

    /**
     * 校验 Token
     *
     * @param obj 对象
     * @param <T> 实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validTokenIgnoreInsert(T obj) {
        validToken(obj, true, true);
    }

    /**
     * 校验 Token
     *
     * @param obj        对象
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj, boolean validChild) {
        validToken(obj, validChild, false);
    }

    /**
     * 校验 Token
     *
     * @param obj        对象
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(T obj, boolean validChild, boolean ignoreInsert) {
        if (obj == null) {
            return;
        }
        String token = obj.get_token();
        try {
            if (ignoreInsert && token == null) {
                Set<EntityColumn> pkColumn = SecurityTokenHelper.getPkColumns(obj.getClass());
                if (!CollectionUtils.isEmpty(pkColumn)) {
                    for (EntityColumn pk : pkColumn) {
                        if (FieldUtils.readField(obj, pk.getField().getName(), true) != null) {
                            throw new SecurityTokenException("Security Token not found!");
                        }
                    }
                    return;
                }
            }
            if (token == null) {
                throw new SecurityTokenException("Security Token not found!");
            }
            String currentToken = generateToken(obj);
            if (!token.equalsIgnoreCase(currentToken)) {
                throw new SecurityTokenException(String.format("Security Token not match for [%s] , now : [%s]", token, currentToken));
            }
            if (validChild) {
                for (EntityField field : SecurityTokenHelper.getEntityFields(getEntityClass(obj))) {
                    if (SecurityToken.class.isAssignableFrom(field.getJavaType())) {
                        validToken((SecurityToken) FieldUtils.readField(obj, field.getName(), true), validChild, ignoreInsert);
                    } else if (Collection.class.isAssignableFrom(field.getJavaType())) {
                        Collection<?> list = (Collection) FieldUtils.readField(obj, field.getName(), true);
                        if (!CollectionUtils.isEmpty(list)) {
                            for (Object item : list) {
                                if (item instanceof SecurityToken) {
                                    validToken((SecurityToken) item, validChild, ignoreInsert);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            logger.error("Get field value error : {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 校验 Token
     *
     * @param objs 对象列表
     * @param <T>  实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs) {
        validToken(objs, true);
    }

    /**
     * 校验 Token
     *
     * @param objs 对象列表
     * @param <T>  实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validTokenIgnoreInsert(Collection<T> objs) {
        validToken(objs, true, true);
    }

    /**
     * 校验 Token
     *
     * @param objs       对象列表
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs, boolean validChild) {
        validToken(objs, validChild, false);
    }

    /**
     * 校验 Token
     *
     * @param objs       对象列表
     * @param validChild 是否校验子对象
     * @param <T>        实现安全令牌接口的泛型
     */
    public static <T extends SecurityToken> void validToken(Collection<T> objs, boolean validChild, boolean ignoreInsert) {
        if (CollectionUtils.isEmpty(objs)) {
            return;
        }
        for (T obj : objs) {
            validToken(obj, validChild, ignoreInsert);
        }
    }

    public static <T extends SecurityToken> Class<? extends SecurityToken> getEntityClass(T objs) {
        return objs.associateEntityClass() == null ? objs.getClass() : objs.associateEntityClass();
    }
}
