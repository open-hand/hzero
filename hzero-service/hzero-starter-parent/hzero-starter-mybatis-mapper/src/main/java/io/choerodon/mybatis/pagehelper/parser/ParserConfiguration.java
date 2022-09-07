package io.choerodon.mybatis.pagehelper.parser;

import io.choerodon.mybatis.helper.MapperHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 解析器配置类
 *
 * @author XCXCXCXCX
 * @date 2020/2/26 1:57 下午
 */
@Configuration
public class ParserConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ICountSqlParser countSqlParser() {
        return new CountSqlParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public IOrderByParser orderByParser(MapperHelper mapperHelper) {
        return new OrderByParser(mapperHelper.getConfig().getStyle());
    }

    @Bean
    @ConditionalOnMissingBean
    public IPageSqlParser pageSqlParser() {
        return new SqlServerParser();
    }

}
