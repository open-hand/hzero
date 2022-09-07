package org.hzero.scheduler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api
 *
 * @author bojiangzhou 2018/08/20
 */
@Configuration
public class SchedulerSwaggerApiConfig {

    public static final String EXECUTOR_SITE = "Executor(Site Level)";
    public static final String EXECUTOR = "Executor";
    public static final String EXECUTOR_CONFIG_SITE = "Executor Config(Site Level)";
    public static final String EXECUTOR_CONFIG = "Executor Config";
    public static final String JOB_INFO_SITE = "Job Info(Site Level)";
    public static final String JOB_INFO = "job Info";
    public static final String JOB_LOG_SITE = "Job Log(Site Level)";
    public static final String JOB_LOG = "Job Log";
    public static final String EXECUTABLE_SITE = "Executable(Site Level)";
    public static final String EXECUTABLE = "Executable";
    public static final String CONCURRENT_SITE = "Concurrent(Site Level)";
    public static final String CONCURRENT = "Concurrent";
    public static final String CONCURRENT_PARAM_SITE = "Concurrent Param(Site Level)";
    public static final String CONCURRENT_PARAM = "Concurrent Param";
    public static final String CONCURRENT_REQUEST_SITE = "Concurrent Request(Site Level)";
    public static final String CONCURRENT_REQUEST = "Concurrent Request";
    public static final String PERMISSION_SITE = "Concurrent Permission(Site Level)";
    public static final String PERMISSION = "Concurrent Permission";
    public static final String MANAGER = "Manager";

    @Autowired
    public SchedulerSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(EXECUTOR_SITE, "任务执行器(平台级)"),
                new Tag(EXECUTOR, "任务执行器"),
                new Tag(EXECUTOR_CONFIG_SITE, "执行器配置(平台级)"),
                new Tag(EXECUTOR_CONFIG, "执行器配置"),
                new Tag(JOB_INFO_SITE, "调度任务信息(平台级)"),
                new Tag(JOB_INFO, "调度任务信息"),
                new Tag(JOB_LOG_SITE, "任务日志(平台级)"),
                new Tag(JOB_LOG, "任务日志"),
                new Tag(EXECUTABLE_SITE, "可执行定义(平台级)"),
                new Tag(EXECUTABLE, "可执行定义"),
                new Tag(CONCURRENT_SITE, "请求定义(平台级)"),
                new Tag(CONCURRENT, "请求定义"),
                new Tag(CONCURRENT_PARAM_SITE, "请求定义参数(平台级)"),
                new Tag(CONCURRENT_PARAM, "请求定义参数"),
                new Tag(CONCURRENT_REQUEST_SITE, "并发请求(平台级)"),
                new Tag(CONCURRENT_REQUEST, "并发请求"),
                new Tag(PERMISSION_SITE, "并发请求权限定义(平台级)"),
                new Tag(PERMISSION, "并发请求权限定义"),
                new Tag(MANAGER, "管理工具")
        );
    }
}
