package org.hzero.boot.platform.form.autoconfigure;

import org.hzero.boot.platform.form.FormClient;
import org.hzero.boot.platform.form.constraints.FormConstraintsCheck;
import org.hzero.boot.platform.form.domain.repository.BaseFormLineRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 表单配置自动装配
 *
 * @author liufanghan 2019/11/19 20:02
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.platform.form")
public class FormAutoConfiguration {

    @Bean
    public FormClient formClient(FormConstraintsCheck formConstraintsCheck, BaseFormLineRepository baseFormLineRepository) {
        return new FormClient(formConstraintsCheck, baseFormLineRepository);
    }

}
