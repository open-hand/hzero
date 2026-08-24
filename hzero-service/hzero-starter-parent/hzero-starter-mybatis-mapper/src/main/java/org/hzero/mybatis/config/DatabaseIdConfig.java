package org.hzero.mybatis.config;

import java.util.Properties;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支持mapper上写databaseId
 *
 * @author bojiangzhou 2018/09/11
 */
@Configuration
public class DatabaseIdConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("SQL Server", "sqlserver");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

}
