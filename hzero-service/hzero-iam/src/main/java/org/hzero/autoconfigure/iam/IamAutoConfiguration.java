package org.hzero.autoconfigure.iam;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.hzero.core.util.CommonExecutor;

/**
 * IAM 自动化配置
 *
 * @author bojiangzhou 2018/10/25
 */
@ComponentScan(value = {
    "org.hzero.iam.api",
    "org.hzero.iam.app",
    "org.hzero.iam.config",
    "org.hzero.iam.domain",
    "org.hzero.iam.infra",
})
@EnableFeignClients({"org.hzero.iam", "org.hzero.boot.imported"})
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Configuration
@EnableChoerodonResourceServer
@EnableObjectMapper
public class IamAutoConfiguration {

    @Bean
    @Qualifier("LdapExecutor")
    public ThreadPoolExecutor ldapExecutor() {
        int coreSize = CommonExecutor.getCpuProcessors();
        return CommonExecutor.buildThreadFirstExecutor(2, coreSize * 4, 10, TimeUnit.MINUTES, 1 << 15, "LdapExecutor");
    }

    /**
     * 通用线程池
     */
    @Bean(name = "IamCommonAsyncTaskExecutor")
    @Qualifier("IamCommonAsyncTaskExecutor")
    public ThreadPoolExecutor iamCommonAsyncTaskExecutor() {
        return CommonExecutor.buildThreadFirstExecutor("IamExecutor");
    }

}
