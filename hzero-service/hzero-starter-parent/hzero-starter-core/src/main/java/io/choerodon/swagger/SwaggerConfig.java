package io.choerodon.swagger;

import static com.google.common.base.Predicates.not;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import io.choerodon.swagger.exclude.EnableHandSwagger2;
import io.choerodon.swagger.swagger.*;

/**
 * swagger的config类，配置Docket和自定以CustomSwaggerOperationPlugin插件
 *
 * @author xausky
 */
@ComponentScan("io.choerodon.swagger.controller")
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableHandSwagger2
public class SwaggerConfig {

    /**
     * 配置swagger-ui
     * 因为包依赖没有BasicErrorController，所以没有使用instanceOf
     *
     * @return swagger-ui Docket
     */
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(not((RequestHandler requestHandler) ->
                "org.springframework.boot.autoconfigure.web.BasicErrorController".equals(requestHandler.declaringClass().getName())
        )).build();
    }


    @Bean
    public CustomSwaggerOperationPlugin customSwaggerOperationPlugin() {
        return new CustomSwaggerOperationPlugin();
    }

    @Bean
    public OperationCustomPageRequestReader operationCustomPageRequestReader() {
        return new OperationCustomPageRequestReader();
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionRegistry permissionRegistry(){
        return new PermissionRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public LabelRegistry labelRegistry(){
        return new LabelRegistry();
    }

    @Bean
    public CustomPermissionOperationPlugin customPermissionOperationPlugin(PermissionRegistry permissionRegistry,
                                                                           LabelRegistry labelRegistry){
        return new CustomPermissionOperationPlugin(permissionRegistry, labelRegistry);
    }

}
