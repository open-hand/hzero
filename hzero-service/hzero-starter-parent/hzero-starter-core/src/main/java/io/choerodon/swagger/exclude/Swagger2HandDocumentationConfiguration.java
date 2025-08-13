package io.choerodon.swagger.exclude;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.swagger2.configuration.Swagger2JacksonModule;

/**
 * 用于排除ApiResourceController
 * @author zhipeng.zuo
 * 2018/1/19
 */
@Configuration
@Import({SpringfoxWebMvcConfiguration.class, SwaggerHandCommonConfiguration.class})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger2.readers.parameter",
        "springfox.documentation.swagger2.web",
        "springfox.documentation.swagger2.mappers"}
)
public class Swagger2HandDocumentationConfiguration {
    @Bean
    public JacksonModuleRegistrar swagger2Module() {
        return new Swagger2JacksonModule();
    }
}
