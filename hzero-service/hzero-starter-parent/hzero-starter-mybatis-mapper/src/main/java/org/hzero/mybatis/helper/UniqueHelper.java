package org.hzero.mybatis.helper;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.LanguageHelper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Reflections;
import org.hzero.mybatis.annotation.Unique;
import org.hzero.mybatis.domian.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 * 唯一性校验
 * </p>
 *
 * @author qingsheng.chen 2019/3/18 星期一 17:27
 */
public class UniqueHelper {
    private static final Logger logger = LoggerFactory.getLogger(UniqueHelper.class);
    private static SqlSessionFactory sqlSessionFactory;

    private UniqueHelper() {
    }

    private static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory != null) {
            return sqlSessionFactory;
        }
        synchronized (UniqueHelper.class) {
            sqlSessionFactory = ApplicationContextHelper.getContext().getBean(SqlSessionFactory.class);
        }
        return sqlSessionFactory;
    }

    public static <T extends AuditDomain> void isUnique(T entity) {
        Assert.isTrue(valid(entity), BaseConstants.ErrorCode.DATA_EXISTS);
    }

    public static <T extends AuditDomain> boolean valid(T entity) {
        return valid(entity, Unique.DEFAULT_CONSTRAINT_NAME);
    }

    public static <T extends AuditDomain> void isUnique(T entity, String constraintName) {
        Assert.isTrue(valid(entity, constraintName), BaseConstants.ErrorCode.DATA_EXISTS);
    }

    public static <T extends AuditDomain> boolean valid(T entity, String constraintName) {
        if (!EntityHelper.contain(entity.getClass())) {
            logger.debug("Not found mapper for {}", entity.getClass());
            return true;
        }
        EntityTable entityTable = EntityHelper.getEntityTable(entity.getClass());
        Set<EntityColumn> uniqueColumns = entityTable.getUniqueColumns(constraintName);
        if (uniqueColumns.isEmpty()) {
            logger.debug("Not found unique constraint for {}", entity.getClass());
            return true;
        }
        try {
            return valid(entityTable, entity, uniqueColumns);
        } catch (SQLException | IllegalAccessException e) {
            logger.error("Error execute unique valid : {}", e);
            return true;
        }
    }

    public static <T extends AuditDomain> List<T> valid(Collection<T> entityList) {
        return valid(entityList, null, Unique.DEFAULT_CONSTRAINT_NAME);
    }

    public static <T extends AuditDomain> List<T> valid(Collection<T> entityList, Class<T> entityClass) {
        return valid(entityList, entityClass, Unique.DEFAULT_CONSTRAINT_NAME);
    }

    public static <T extends AuditDomain> List<T> valid(Collection<T> entityList, String constraintName) {
        return valid(entityList, null, constraintName);
    }

    public static <T extends AuditDomain> List<T> valid(Collection<T> entityList, Class<T> entityClass, String constraintName) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        List<T> repeatList = new ArrayList<>();
        // 从泛型获取
        Class<?> realType = entityClass == null ? entityClass = Reflections.getClassGenericType(entityList.getClass()) : entityClass;
        // 获取泛型失败
        if (!AuditDomain.class.isAssignableFrom(realType)) {
            realType = entityList.stream().findFirst().orElseThrow(IllegalStateException::new).getClass();
        }
        if (!AuditDomain.class.isAssignableFrom(realType)) {
            throw new IllegalArgumentException("Unable to find entity class.");
        }
        EntityTable entityTable = EntityHelper.getEntityTable(realType);
        Set<EntityColumn> uniqueColumns = entityTable.getUniqueColumns(constraintName);
        if (uniqueColumns.isEmpty()) {
            logger.debug("Not found unique constraint for {}", entityClass);
            return Collections.emptyList();
        }
        // 校验列表内是否有重复
        Set<Map<String, Object>> uniqueSet = new HashSet<>(entityList.size());
        for (T entity : entityList) {
            try {
                Map<String, Object> uniqueMap = new HashMap<>(uniqueColumns.size());
                for (EntityColumn uniqueColumn : uniqueColumns) {
                    uniqueMap.put(uniqueColumn.getField().getName(), FieldUtils.readField(entity, uniqueColumn.getField().getName(), true));
                }
                if (uniqueSet.contains(uniqueMap)) {
                    // 内存重复
                    repeatList.add(entity);
                    continue;
                }
                // 内存不重复，校验数据库
                uniqueSet.add(uniqueMap);
                if (!valid(entityTable, entity, uniqueColumns)) {
                    // 数据库重复
                    repeatList.add(entity);
                }
            } catch (Exception e) {
                logger.error("Error get value from entity.", e);
            }
        }
        return repeatList;
    }

    private static <T extends AuditDomain> boolean valid(EntityTable entityTable, T entity, Set<EntityColumn> uniqueColumns) throws SQLException, IllegalAccessException {
        return entityTable.isMultiLanguageUnique() ? validUniqueWithTl(entityTable, entity, uniqueColumns) : validUnique(entityTable, entity, uniqueColumns);
    }

    /**
     * 校验唯一索引
     * <pre>
     *     SELECT COUNT(*)
     *     FROM   TABLE T
     *     JOIN   TABLE_TL TL ON TL.PK = T.PK AND ((TL.LANG = LANG1 AND TL.UNIQUE_COLUMN1 = VALUE1 ...) OR ...)
     *     WHERE  T.UNIQUE_COLUMN2 = VALUE2 ...
     *     [AND PK &lt;&gt; PK_VALUES]
     * </pre>
     *
     * @param entityTable 实体类对应的表信息
     * @param entity      实体类信息
     * @param <T>         实体类类型
     * @return 是否唯一
     * @throws IllegalAccessException 获取参数异常
     */
    private static <T extends AuditDomain> boolean validUniqueWithTl(EntityTable entityTable, T entity, Set<EntityColumn> uniqueColumnSet) throws IllegalAccessException {
        List<Object> paramList = new LinkedList<>();
        // Append : SELECT ... FROM ... JOIN ... ON
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(entityTable.getName()).append(" T JOIN ").append(entityTable.getName()).append("_TL TL ON ");
        // Append : PK AND ...
        for (EntityColumn pkColumn : entityTable.getEntityClassPkColumns()) {
            sql.append("T.").append(pkColumn.getColumn()).append(" = TL.").append(pkColumn.getColumn()).append(" AND ");
        }
        // Append : LANG AND UNIQUE_COLUMN
        sql.append("(");
        Iterator<Language> iterator = LanguageHelper.languages().iterator();
        while (iterator.hasNext()) {
            Language language = iterator.next();
            paramList.add(language.getCode());
            sql.append("(TL.LANG = ? ");
            for (EntityColumn uniqueColumn : uniqueColumnSet) {
                if (uniqueColumn.isMultiLanguage()) {
                    String value = getValue(entity, uniqueColumn.getField().getName(), language.getCode());
                    sql.append("AND TL.").append(uniqueColumn.getColumn());
                    if (value == null) {
                        sql.append(" IS NULL ");
                    } else {
                        paramList.add(value);
                        sql.append(" = ? ");
                    }
                }
            }
            if (iterator.hasNext()) {
                sql.append(") OR ");
            } else {
                sql.append(")");
            }
        }
        sql.append(") ");
        // Append : WHERE UNIQUE_COLUMN
        boolean hasWhere = false;
        for (EntityColumn uniqueColumn : uniqueColumnSet) {
            if (!uniqueColumn.isMultiLanguage()) {
                if (!hasWhere) {
                    hasWhere = true;
                    sql.append("WHERE ");
                } else {
                    sql.append("AND ");
                }
                sql.append("T.").append(uniqueColumn.getColumn());
                Object value = FieldUtils.readField(entity, uniqueColumn.getField().getName(), true);
                if (value == null) {
                    sql.append(" IS NULL ");
                } else {
                    paramList.add(value);
                    sql.append(" = ? ");
                }
            }
        }
        // Append : PK
        appendPk(entityTable, entity, paramList, sql, "T.", hasWhere ? "AND" : "WHERE");
        return getUniqueQueryResult(sql.toString(), paramList);
    }

    private static <T extends AuditDomain> void appendPk(EntityTable entityTable, T entity, List<Object> paramList, StringBuilder sql, String tableAlias, String keyword) throws IllegalAccessException {
        boolean hasPkValue = false;
        for (EntityColumn pkColumn : entityTable.getEntityClassPkColumns()) {
            Object pk = FieldUtils.readField(entity, pkColumn.getField().getName(), true);
            if (pk != null) {
                if (!hasPkValue) {
                    hasPkValue = true;
                    sql.append(keyword).append(" (");
                } else {
                    sql.append(" AND ");
                }
                paramList.add(pk);
                sql.append(tableAlias).append(pkColumn.getColumn()).append(" <> ?");
            }
        }
        if (hasPkValue) {
            sql.append(")");
        }
    }

    private static <T extends AuditDomain> String getValue(T entity, String fieldName, String lang) throws IllegalAccessException {
        Map<String, Map<String, String>> tls = entity.get_tls();
        if (tls != null && tls.containsKey(fieldName)) {
            Map<String, String> columnLangMap = tls.get(fieldName);
            if (columnLangMap.containsKey(lang)) {
                return columnLangMap.get(lang);
            }
        }
        Object value = FieldUtils.readField(entity, fieldName, true);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 校验唯一索引
     * <pre>
     *     SELECT COUNT(*)
     *     FROM   TABLE
     *     WHERE  UNIQUE_COLUMN1 = VALUE1 ...
     *     [AND PK &lt;&gt; PK_VALUES]
     * </pre>
     *
     * @param entityTable 实体类对应的表信息
     * @param entity      实体类信息
     * @param <T>         实体类类型
     * @return 是否唯一
     * @throws IllegalAccessException 获取参数异常
     */
    private static <T extends AuditDomain> boolean validUnique(EntityTable entityTable, T entity, Set<EntityColumn> uniqueColumnSet) throws IllegalAccessException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ");
        sql.append(entityTable.getName()).append(" WHERE ");
        int cnt = 0;
        final int maxCnt = uniqueColumnSet.size();
        List<Object> paramList = new LinkedList<>();
        for (EntityColumn entityColumn : uniqueColumnSet) {
            Object value = FieldUtils.readField(entity, entityColumn.getField().getName(), true);
            if (value == null) {
                sql.append(entityColumn.getColumn()).append(" IS NULL");
            } else {
                paramList.add(value);
                sql.append(entityColumn.getColumn()).append(" = ?");
            }
            if (++cnt < maxCnt) {
                sql.append(" AND ");
            }
        }
        appendPk(entityTable, entity, paramList, sql, "", " AND");
        return getUniqueQueryResult(sql.toString(), paramList);
    }

    private static boolean getUniqueQueryResult(String sql, List<Object> paramList) {
        logger.debug("Unique valid query sql : {}", sql);
        logger.debug("Unique valid query sql arg : {}", paramList);
        try (SqlSession sqlSession = getSqlSessionFactory().openSession();
             PreparedStatement preparedStatement = sqlSession.getConnection().prepareStatement(sql)) {
            for (int paramIndex = 0; paramIndex < paramList.size(); ++paramIndex) {
                preparedStatement.setObject(paramIndex + 1, paramList.get(paramIndex));
            }
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Sql Exception : {}", e);
        }
        return true;
    }
}
