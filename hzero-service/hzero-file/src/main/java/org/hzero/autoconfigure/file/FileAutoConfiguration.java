package org.hzero.autoconfigure.file;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

import org.hzero.core.jackson.annotation.EnableObjectMapper;

/**
 * <p>
 * 自动配置
 * </p>
 *
 * @author qingsheng.chen 2018/11/10 星期六 11:33
 */
@ComponentScan(value = {
        "org.hzero.file.api",
        "org.hzero.file.app",
        "org.hzero.file.config",
        "org.hzero.file.domain",
        "org.hzero.file.infra",
})
@EnableChoerodonResourceServer
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@EnableObjectMapper
@EnableFeignClients("org.hzero.file")
@Configuration
public class FileAutoConfiguration {

}
