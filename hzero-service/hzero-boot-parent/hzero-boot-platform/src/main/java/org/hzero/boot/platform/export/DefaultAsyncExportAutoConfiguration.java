package org.hzero.boot.platform.export;

import org.hzero.boot.platform.export.feign.ExportTaskService;
import org.hzero.core.export.ExportAsyncTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author XCXCXCXCX
 * @date 2019/8/6
 * @project hzero-boot-parent
 */
@Configuration
@ConditionalOnProperty(name = "hzero.export.enable-async", havingValue = "true")
@EnableFeignClients(clients = ExportTaskService.class)
public class DefaultAsyncExportAutoConfiguration {

    @Bean
    @ConditionalOnClass(name = "org.hzero.boot.file.FileClient")
    @ConditionalOnMissingBean
    public ExportAsyncTemplate defaultExportAsyncTemplate() {
        return new DefaultExportAsyncTemplate();
    }

}
