package org.hzero.mybatis.security;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.mybatis.config.DataSecurityProperty;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.service.DataSecurityKeyService;
import org.hzero.mybatis.util.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据安全拦截器，插入数据库时加密，读取时解密
 * </p>
 *
 * @author qingsheng.chen 2018/9/19 星期三 19:33
 */
@InterceptorOrder(400)
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class DataSecurityInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(DataSecurityInterceptor.class);
    public static final ThreadLocal<Boolean> SECURITY_ENABLE = new ThreadLocal<>();

    /**
     * 是否默认打开数据加解密，默认关闭，设置为 true 之后，不需要调用 {@link DataSecurityHelper#open()} 即可自动打开，<strong>但是相应的，如果需要禁用数据加解密，则需要显式调用 {@link DataSecurityHelper#close()} ()} </strong>
     */
    private boolean defaultOpen = false;
    /**
     * 隔离级别，表示设置的启用/禁用影响的范围，默认 once 表示一次查询或修改后失效(不包含 count 查询); thread 表示当前线程内
     */
    private DataSecurityProperty.IsolationLevel isolationLevel = DataSecurityProperty.IsolationLevel.ONCE;

    private static volatile DataSecurityKeyService dataSecurityKeyService;

    public DataSecurityInterceptor() {
    }

    public DataSecurityInterceptor(boolean defaultOpen,
                                   DataSecurityProperty.IsolationLevel isolationLevel) {
        this.defaultOpen = defaultOpen;
        this.isolationLevel = isolationLevel;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result;
        if (!SqlUtils.isCountSql(invocation) && isOpen()) {
            clearSetting();
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            // 查询时解密，新增/修改时加密
            if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
                result = invocation.proceed();
                process(result, false);
            } else {
                process(invocation.getArgs()[1], true);
                result = invocation.proceed();
            }
        } else {
            result = invocation.proceed();
        }
        return result;
    }

    private boolean isOpen() {
        return BooleanUtils.isTrue(SECURITY_ENABLE.get()) || (SECURITY_ENABLE.get() == null && defaultOpen);
    }

    private void clearSetting() {
        if (DataSecurityProperty.IsolationLevel.ONCE.equals(isolationLevel)) {
            SECURITY_ENABLE.remove();
        }
    }

    private void process(Object result, boolean encrypt) {
        Set<String> dataSecurityFieldSet;
        if (result instanceof Collection) {
            Collection collection = (Collection) result;
            if (!collection.iterator().hasNext()) {
                return;
            }
            Object entity = collection.iterator().next();
            if (entity == null) {
                return;
            }
            if (EntityHelper.contain(entity.getClass())) {
                dataSecurityFieldSet = EntityHelper.getTableByEntity(entity.getClass()).getDataSecurityColumns().stream().map(EntityColumn::getProperty).collect(Collectors.toSet());
            } else {
                dataSecurityFieldSet = DataSecurityHelper.getDataSecurityColumns(entity.getClass()).stream().map(EntityColumn::getProperty).collect(Collectors.toSet());
            }
            if (!CollectionUtils.isEmpty(dataSecurityFieldSet)) {
                for (Object object : collection) {
                    process(object, dataSecurityFieldSet, encrypt);
                }
            }
        } else {
            if (result == null) {
                return;
            }
            Class<?> entityClass = result.getClass();
            if (EntityHelper.contain(entityClass)) {
                dataSecurityFieldSet = EntityHelper.getTableByEntity(entityClass).getDataSecurityColumns().stream().map(EntityColumn::getProperty).collect(Collectors.toSet());
            } else {
                dataSecurityFieldSet = DataSecurityHelper.getDataSecurityColumns(entityClass).stream().map(EntityColumn::getProperty).collect(Collectors.toSet());
            }
            if (!CollectionUtils.isEmpty(dataSecurityFieldSet)) {
                process(result, dataSecurityFieldSet, encrypt);
            }
        }
    }

    private void process(Object result, Set<String> fieldSet, boolean encrypt) {
        if (!CollectionUtils.isEmpty(fieldSet)) {
            for (String fieldName : fieldSet) {
                try {
                    String value = encrypt ? encrypt(valueOfString(FieldUtils.readField(result, fieldName, true)))
                            : decrypt(valueOfString(FieldUtils.readField(result, fieldName, true)));
                    FieldUtils.writeField(result, fieldName, value, true);
                } catch (Exception e) {
                    logger.error("Unable to process recode [encrypt({}), fieldName({}), result({})]", encrypt, fieldName, result);
                    e.printStackTrace();
                }
            }
        }
    }

    public static String valueOfString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    public static String encrypt(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return EncryptionUtils.AES.encrypt(value, getSecurityKey());
    }

    public static String decrypt(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return EncryptionUtils.AES.decrypt(value, getSecurityKey());
    }

    private static String getSecurityKey() {
        return getDataSecurityKeyService().readSecurityKey();
    }

    public static void setDataSecurityKeyService(DataSecurityKeyService dataSecurityKeyService) {
        DataSecurityInterceptor.dataSecurityKeyService = dataSecurityKeyService;
    }

    public static DataSecurityKeyService getDataSecurityKeyService() {
        if (dataSecurityKeyService == null) {
            synchronized (DataSecurityInterceptor.class) {
                if (dataSecurityKeyService == null) {
                    dataSecurityKeyService = ApplicationContextHelper.getContext().getBean(DataSecurityKeyService.class);
                }
            }
        }
        return dataSecurityKeyService;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // no need properties
    }
}
