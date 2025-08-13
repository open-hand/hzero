package org.hzero.mybatis;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.persistence.Table;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.mybatis.impl.DefaultDynamicSqlMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.domain.Config;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.SqlHelper;

/**
 * @author mingke.yan@hand-china.com
 * @version 1.0
 * @date 2018/8/21
 */
public class BatchInsertHelper<T> {
    private static final Long DEFAULT_VERSION_NUMBER = 1L;
    private static final Long DEFAULT_USER_ID = 10001L;

    //@Value("${hzero.supporter.batch-insert.slice-size:500}")
    private final int sliceSize;

    private final SqlSessionFactory sqlSessionFactory;


    public BatchInsertHelper(SqlSessionFactory sqlSessionFactory, int sliceSize) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.sliceSize = sliceSize;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<T> batchInsert(List<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return entities;
        }
        // 校验对象是否基础AuditDomain
        //this.validEntity(entities);
        return doProcess(entities);
    }

    private List<T> doProcess(List<T> entities) {
        // 初始化对象
        this.initEntities(entities);
        // 获取动态SQL
        String insertSql = this.getInsertSql(entities.get(0));
        // 执行SQL
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            DefaultDynamicSqlMapper mapper = new DefaultDynamicSqlMapper(sqlSession);
            int limit = (int) Math.ceil((double) entities.size() / sliceSize);
            for (int i = 0; i < limit; i++) {
                if (i == limit - 1) {
                    mapper.insert(insertSql, entities.subList(i * sliceSize, entities.size()));
                } else {
                    mapper.insert(insertSql, entities.subList(i * sliceSize, (i + 1) * sliceSize));
                }
            }
        }
        return entities;
    }

    private void initEntities(List<T> entities) {
        Long userId = DEFAULT_USER_ID;
        if (DetailsHelper.getUserDetails() != null) {
            userId = DetailsHelper.getUserDetails().getUserId();
        }
        Date date = new Date();
        for (T entity : entities) {
            //AuditDomain子类设置who等字段
            if (entity instanceof AuditDomain) {
                AuditDomain domain = (AuditDomain) entity;
                domain.setCreatedBy(userId);
                domain.setCreationDate(date);
                domain.setLastUpdatedBy(userId);
                domain.setLastUpdateDate(date);
                domain.setObjectVersionNumber(DEFAULT_VERSION_NUMBER);
            }
        }
    }

    /**
     * 校验该对象是否是AuditDomain的子类
     *
     * @param entities
     */
    private void validEntity(List<T> entities) {
        T template = entities.get(0);
        if (template.getClass().isAssignableFrom(AuditDomain.class)) {
            throw new CommonException("error.entity.class");
        }
    }

    /**
     * 初始化EntityHelper
     *
     * @param entity
     */
    private void initEntityHelper(T entity) {
        Config config = new Config();
        EntityHelper.initEntityNameMap(entity.getClass(), "INSERT", config);
    }

    /**
     * 获取批量插入SQL
     *
     * @param entity
     * @return
     */
    private String getInsertSql(T entity) {
        // 初始化EntityHelper工具
        this.initEntityHelper(entity);
        StringBuilder sb = new StringBuilder(1024);
        sb.append("<script>")
                .append("INSERT INTO ")
                .append(this.getTableName(entity.getClass()))
                .append(this.insertColumns(entity.getClass(), true, false, false))
                .append("VALUES ")
                .append("<foreach collection=\"list\" index=\"index\" item=\"item\" separator=\",\">")
                .append(this.insertValuesColumns(entity.getClass(), true, false, false))
                .append("</foreach> ")
                .append("</script>");
        return sb.toString();
    }

    /**
     * 获取表名
     *
     * @param clazz
     * @return
     */
    private String getTableName(Class<?> clazz) {
        String tableName = null;
        Table annotation = getTableAnnotation(clazz);
        if (annotation != null) {
            tableName = annotation.name();
        }
        return tableName;
    }

    private Table getTableAnnotation(Class<?> clazz) {
        Table annotation = null;
        annotation = clazz.getAnnotation(Table.class);
        if (annotation == null && clazz.getSuperclass() != null) {
            annotation = getTableAnnotation(clazz.getSuperclass());
        }
        return annotation;
    }

    /**
     * <p>insert table()列</p>
     * <p><i>--fork from SqlHelper.insertColumns</i></p>
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    private String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        // 获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        // 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull("item", column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * <p>insert-values()列</p>
     * <p><i>--fork from SqlHelper.insertValuesColumns</i></p>
     *
     * @param entityClass
     * @param skipId      是否从列中忽略id类型
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    private String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        // 获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        // 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull("item", column, column.getColumnHolderWithComma("item", null), notEmpty));
            } else {
                sql.append(column.getColumnHolderWithComma("item", null));
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }
}