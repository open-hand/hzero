package io.choerodon.swagger.exclude;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 用于排除ApiResourceController
 * @author zhipeng.zuo
 * 2018/1/19
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({Swagger2HandDocumentationConfiguration.class})
public @interface EnableHandSwagger2 {
}