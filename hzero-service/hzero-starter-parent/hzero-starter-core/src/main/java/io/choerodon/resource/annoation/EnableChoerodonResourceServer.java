package io.choerodon.resource.annoation;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

import io.choerodon.resource.config.ChoerodonResourceServerConfiguration;

/**
 * @author dongfan117@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ChoerodonResourceServerConfiguration.class})
public @interface EnableChoerodonResourceServer {
}
