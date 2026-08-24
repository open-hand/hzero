package org.hzero.swagger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerApiConfig {

    public static final String SWAGGER_DOCUMENT = "Swagger Document";

    @Autowired
    public SwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(SWAGGER_DOCUMENT, "Swagger 文档刷新")
        );
    }
}
