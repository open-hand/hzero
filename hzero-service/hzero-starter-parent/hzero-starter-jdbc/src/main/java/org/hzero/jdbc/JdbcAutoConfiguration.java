package org.hzero.jdbc;

import org.hzero.jdbc.config.JdbcInitializeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto Configuration
 *
 * @author bojiangzhou 2018/07/26
 */
@Configuration
public class JdbcAutoConfiguration {

    @Bean
    public JdbcInitializeConfig exportInitializeConfig() {
        return new JdbcInitializeConfig();
    }

}
