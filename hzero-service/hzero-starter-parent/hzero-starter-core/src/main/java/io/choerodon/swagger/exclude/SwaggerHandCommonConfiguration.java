package io.choerodon.swagger.exclude;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * 用于排除ApiResourceController
 * @author zhipeng.zuo
 * 2018/1/19
 */
@Configuration
@ComponentScan(basePackages = {
        "springfox.documentation.swagger.schema",
        "springfox.documentation.swagger.readers",
        "springfox.documentation.swagger.web"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "springfox.documentation.swagger.web.ApiResourceController")})
public class SwaggerHandCommonConfiguration {

}