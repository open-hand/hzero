package org.hzero.mybatis.parser.autoconfigure;

import org.hzero.mybatis.parser.SqlInterceptor;
import org.hzero.mybatis.parser.SqlParserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-04-28 13:31
 */
@Configuration
public class SqlParserInterceptorConfiguration {
    @Value("${spring.application.name:application}")
    private String serviceName;
    private List<SqlInterceptor> sqlInterceptors;

    @Bean
    @ConditionalOnMissingBean
    public SqlParserInterceptor sqlParserInterceptor() {
        return new SqlParserInterceptor(serviceName, sqlInterceptors);
    }

    @Autowired(required = false)
    public SqlParserInterceptorConfiguration setSqlInterceptors(List<SqlInterceptor> sqlInterceptors) {
        this.sqlInterceptors = sqlInterceptors;
        return this;
    }

}
