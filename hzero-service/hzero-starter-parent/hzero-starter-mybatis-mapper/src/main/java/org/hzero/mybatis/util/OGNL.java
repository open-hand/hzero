package org.hzero.mybatis.util;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.util.StringUtil;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.domian.IDynamicTableName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * OGNL静态方法
 *
 * @author liuzh
 */
public abstract class OGNL {
    private static final String SAFE_DELETE_ERROR = "通用 Mapper 安全检查: 对查询条件参数进行检查时出错!";
    private static final String SAFE_DELETE_EXCEPTION = "通用 Mapper 安全检查: 当前操作的方法没有指定查询条件，不允许执行该操作!";


    /**
     * 校验通用 Condition 的 entityClass 和当前方法是否匹配
     *
     * @param parameter
     * @param entityFullName
     * @return
     */
    public static boolean checkConditionEntityClass(Object parameter, String entityFullName) {
        if (parameter != null && parameter instanceof Condition && StringUtil.isNotEmpty(entityFullName)) {
            Condition example = (Condition) parameter;
            Class<?> entityClass = example.getEntityClass();
            if(!entityClass.getCanonicalName().equals(entityFullName)){
                throw new MapperException("当前 Condition 方法对应实体为:" + entityFullName
                        + ", 但是参数 Condition 中的 entityClass 为:" + entityClass.getCanonicalName());
            }
        }
        return true;
    }

    /**
     * 是否包含自定义查询列
     *
     * @param parameter
     * @return
     */
    public static boolean hasSelectColumns(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            if (condition.getSelectColumns() != null && condition.getSelectColumns().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含自定义 Count 列
     *
     * @param parameter
     * @return
     */
    public static boolean hasCountColumn(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return StringUtil.isNotEmpty(condition.getCountColumn());
        }
        return false;
    }

    /**
     * 是否包含 forUpdate
     *
     * @param parameter
     * @return
     */
    public static boolean hasForUpdate(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return condition.isForUpdate();
        }
        return false;
    }

    /**
     * 不包含自定义查询列
     *
     * @param parameter
     * @return
     */
    public static boolean hasNoSelectColumns(Object parameter) {
        return !hasSelectColumns(parameter);
    }

    /**
     * 判断参数是否支持动态表名
     *
     * @param parameter
     * @return true支持，false不支持
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (parameter != null && parameter instanceof IDynamicTableName) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否b支持动态表名
     *
     * @param parameter
     * @return true不支持，false支持
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

    /**
     * 判断条件是 and 还是 or
     *
     * @param parameter
     * @return
     */
    public static String andOr(Object parameter){
        if(parameter instanceof Condition.Criteria){
            return ((Condition.Criteria)parameter).getAndOr();
        } else if(parameter instanceof Condition.Criterion){
            return ((Condition.Criterion)parameter).getAndOr();
        } else if(parameter.getClass().getCanonicalName().endsWith("Criteria")){
            return "or";
        } else {
            return "and";
        }
    }

    /**
     * 检查 paremeter 对象中指定的 fields 是否全是 null，如果是则抛出异常
     *
     * @param parameter
     * @param fields
     * @return
     */
    public static boolean notAllNullParameterCheck(Object parameter, String fields) {
        if (parameter != null) {
            try {
                Set<EntityColumn> columns = EntityHelper.getColumns(parameter.getClass());
                Set<String> fieldSet = new HashSet<String>(Arrays.asList(fields.split(",")));
                for (EntityColumn column : columns) {
                    if (fieldSet.contains(column.getProperty())) {
                        Object value = column.getField().get(parameter);
                        if (value != null) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                throw new MapperException(SAFE_DELETE_ERROR, e);
            }
        }
        throw new MapperException(SAFE_DELETE_EXCEPTION);
    }

    public static String language() {
        return "'" + LanguageHelper.language() + "'";
    }
}

