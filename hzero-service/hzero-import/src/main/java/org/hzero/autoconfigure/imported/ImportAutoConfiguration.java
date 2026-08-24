package org.hzero.autoconfigure.imported;

import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

@ComponentScan(value = {
        "org.hzero.imported.api",
        "org.hzero.imported.app",
        "org.hzero.imported.config",
        "org.hzero.imported.domain",
        "org.hzero.imported.infra",
})
@EnableAsync
@Configuration
@EnableObjectMapper
@EnableChoerodonResourceServer
@EnableFeignClients({"org.hzero.imported"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ImportAutoConfiguration {

}
