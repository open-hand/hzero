package io.choerodon.mybatis.helper.snowflake;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;

import java.sql.Statement;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class SnowflakeKeyGenerator implements KeyGenerator {
    private static volatile SnowflakeHelper snowflakeHelper;

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (parameter != null && ms != null && ms.getConfiguration() != null) {
            final MetaObject parameterMeta = ms.getConfiguration().newMetaObject(parameter);
            String[] keyProperties = ms.getKeyProperties();
            if (keyProperties.length != 1) {
                throw new CommonException("Snowflake ID does not support composite primary keys.");
            }
            if (existsKeyValues(parameterMeta, keyProperties, parameter)) {
                return;
            }
            parameterMeta.setValue(keyProperties[0], generateKey());
        }
    }

    private Long generateKey() {
        return getSnowflakeHelper().next();
    }

    private boolean existsKeyValues(MetaObject metaParam, String[] keyProperties, Object parameter) {
        if (keyProperties == null || keyProperties.length == 0) {
            return true;
        }
        for (String property : keyProperties) {
            if (metaParam.hasGetter(property)) {
                Object defaultValue = metaParam.getValue(property);
                if (defaultValue == null) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing.
    }

    private SnowflakeHelper getSnowflakeHelper() {
        if (snowflakeHelper == null) {
            synchronized (SnowflakeKeyGenerator.class) {
                if (snowflakeHelper == null) {
                    snowflakeHelper = ApplicationContextHelper.getContext().getBean(SnowflakeHelper.class);
                }
            }
        }
        return snowflakeHelper;
    }
}
