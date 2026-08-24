package org.hzero.seata.rm.init;

import org.hzero.seata.rm.init.datasource.DatabaseInitialization;
import org.hzero.seata.rm.init.datasource.MetadataSqlTypeRecognizer;
import org.hzero.seata.rm.init.datasource.SqlTypeRecognizer;
import org.hzero.seata.rm.init.datasource.locator.MysqlAtClientSqlLoader;
import org.hzero.seata.rm.init.datasource.locator.OracleAtClientSqlLoader;
import org.hzero.seata.rm.init.datasource.locator.PostageSqlAtClientSqlLoader;
import org.hzero.seata.rm.init.datasource.locator.SqlLoader;
import org.hzero.seata.rm.init.datasource.locator.SqlserverAtClientSqlLoader;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/4/20 4:54 下午
 */
@Configuration
public class DataInitAutoConfiguration {

    @Bean
    public ApplicationRunner compositeInitialization(List<Initialization> initializations) {
        return args -> initializations.forEach(Initialization::exec);
    }

    @Bean
    public Initialization databaseInitialization(DataSource dataSource, SqlTypeRecognizer sqlTypeRecognizer, List<SqlLoader> sqlLoaders) {
        return new DatabaseInitialization(dataSource, sqlTypeRecognizer, sqlLoaders);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlTypeRecognizer defaultSqlTypeRecognizer() {
        return new MetadataSqlTypeRecognizer();
    }

    @Bean
    public SqlLoader mysqlAtClientSqlLoader() {
        return new MysqlAtClientSqlLoader();
    }

    @Bean
    public SqlLoader oracleAtClientSqlLoader() {
        return new OracleAtClientSqlLoader();
    }

    @Bean
    public SqlLoader postageSqlAtClientSqlLoader() {
        return new PostageSqlAtClientSqlLoader();
    }

    @Bean
    public SqlLoader sqlserverAtClientSqlLoader() {
        return new SqlserverAtClientSqlLoader();
    }

}
