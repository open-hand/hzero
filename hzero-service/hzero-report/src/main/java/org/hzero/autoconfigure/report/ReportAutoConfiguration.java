package org.hzero.autoconfigure.report;

import com.bstek.ureport.console.UReportServlet;
import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

@ComponentScan(value = {
        "org.hzero.report.api",
        "org.hzero.report.app",
        "org.hzero.report.config",
        "org.hzero.report.domain",
        "org.hzero.report.infra",
})
@EnableFeignClients("org.hzero.report")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@EnableChoerodonResourceServer
@EnableObjectMapper
@Configuration
public class ReportAutoConfiguration {

    /**
     * 通用线程池
     *
     * @return
     */
    @Bean
    @Qualifier("commonAsyncTaskExecutor")
    public AsyncTaskExecutor commonAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("common-executor");
        executor.setMaxPoolSize(30);
        executor.setCorePoolSize(10);
        executor.setQueueCapacity(99999);
        return executor;
    }

    @Bean
    public ServletRegistrationBean<UReportServlet> ureportServlet() {
        ServletRegistrationBean<UReportServlet> bean = new ServletRegistrationBean<>(new UReportServlet());
        bean.addUrlMappings("/ureport/*");
        return bean;
    }
}
