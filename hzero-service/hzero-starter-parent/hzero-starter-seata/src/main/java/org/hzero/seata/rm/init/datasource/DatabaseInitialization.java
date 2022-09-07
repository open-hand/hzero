package org.hzero.seata.rm.init.datasource;

import org.hzero.seata.rm.init.Initialization;
import org.hzero.seata.rm.init.datasource.locator.SqlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 4:41 下午
 */
public class DatabaseInitialization implements Initialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitialization.class);

    private DataSource dataSource;

    private SqlTypeRecognizer sqlTypeRecognizer;

    /**
     * 串行执行，无需同步
     */
    private Map<String, SqlLoader> sqlLocatorMap = new HashMap<>(2);

    private SqlLoader emptySqlLocator = new SqlLoader() {

        @Override
        public String load() {
            return null;
        }

        @Override
        public String getSqlType() {
            return null;
        }
    };

    public DatabaseInitialization(DataSource dataSource,
                                  SqlTypeRecognizer sqlTypeRecognizer,
                                  List<SqlLoader> sqlLoaders) {
        this.dataSource = dataSource;
        this.sqlTypeRecognizer = sqlTypeRecognizer;
        sqlLocatorMap = sqlLoaders.stream().collect(Collectors.toMap(SqlLoader::getSqlType, sqlLoader -> sqlLoader));
    }

    @Override
    public void doExec() {

        /**
         * 多数据源的情况
         */
        if (dataSource instanceof AbstractRoutingDataSource) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> targetDataSources = (Map<Object, Object>) ReflectionUtils.getField(ReflectionUtils.findField(dataSource.getClass(), "targetDataSources") ,dataSource);
            if (targetDataSources != null) {
                for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
                    if (!(entry.getValue() instanceof DataSource)) {
                        LOGGER.warn("find non-datasource object, ignore it.");
                        continue;
                    }
                    init((DataSource) entry.getValue());
                }
            }
            Object defaultTargetDataSource = ReflectionUtils.getField(ReflectionUtils.findField(dataSource.getClass(), "defaultTargetDataSource") ,dataSource);
            if (!(defaultTargetDataSource instanceof DataSource)) {
                LOGGER.warn("find non-datasource object, ignore it.");
                return;
            }
            init((DataSource) defaultTargetDataSource);
        } else { //单数据源的情况
            init(dataSource);
        }

    }

    private void init(DataSource dataSource) {
        try {
            String sqlType = sqlTypeRecognizer.recognizeSqlType(dataSource);
            String sql = sqlLocatorMap.getOrDefault(sqlType, emptySqlLocator).load();
            if (StringUtils.isEmpty(sql)) {
                //can not recognize sql type or unrecognized sql
                LOGGER.info("unrecognized sql type[{}] or unrecognized sql[{}]", sqlType, sql);
                return;
            }
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
            if (!connection.getAutoCommit()){
                connection.commit();
            }
        } catch (IOException | SQLException e) {
            LOGGER.error("database init failed");
            throw new RuntimeException("database init failed", e);
        }
    }

    @Override
    public boolean allowFailure() {
        return false;
    }
}
