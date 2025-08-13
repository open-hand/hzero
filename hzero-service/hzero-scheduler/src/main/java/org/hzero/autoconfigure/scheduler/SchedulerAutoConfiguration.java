package org.hzero.autoconfigure.scheduler;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;

import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;


/**
 * @author shuangfei.zhu@hand-china.com
 */
@ComponentScan(value = {
        "org.hzero.scheduler.api",
        "org.hzero.scheduler.app",
        "org.hzero.scheduler.config",
        "org.hzero.scheduler.domain",
        "org.hzero.scheduler.infra",
})
@EnableFeignClients({"org.hzero.scheduler"})
@EnableObjectMapper
@EnableChoerodonResourceServer
@Configuration
@EnableAsync
public class SchedulerAutoConfiguration {

    private final DataSource dataSource;

    public SchedulerAutoConfiguration(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean("commonAsyncTaskExecutor")
    public AsyncTaskExecutor commonAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("common-executor");
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        // 在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        // 创建SchedulerFactoryBean
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        Properties pro = propertiesFactoryBean.getObject();
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(true);
        factory.setQuartzProperties(Objects.requireNonNull(pro));
        factory.setDataSource(dataSource);
        return factory;
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean(name = "Scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}
