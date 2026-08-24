package org.hzero.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api 描述配置
 */
@Configuration
public class SwaggerApiConfig {


    public static final String REFRESH = "Refresh Controller";
    public static final String SERVICE_SITE = "Service Controller(Site Level)";
    public static final String SERVICE_ROUTE = "Service Route";
    public static final String SERVICE_ROUTE_SITE = "Service Route(Site Level)";
    public static final String SERVICE_CONFIG_SITE = "Service Config(Site Level)";
    /**
     * 限流
     */
    public static final String GATEWAY_RATE_LIMIT_SITE = "Gateway Rate Limit(Site Level)";
    public static final String GATEWAY_RATE_LIMIT_LINE_SITE = "Gateway Rate Limit Line(Site Level)";
    /**
     * 保护配置
     */
    public static final String HYSTRIX_SITE = "Hystrix(Site Level)";
    public static final String HYSTRIX_LINE_SITE = "HystrixLine(Site Level)";

    /**
     * 维度配置
     */
    public static final String GATEWAY_RATE_LIMIT_DIMENSION_SITE = "Gateway Rate Limit Dimension(Site Level)";

    /**
     * 服务版本
     */
    public static final String SERVICE_VERSION_SITE = "Service Version Site";

    /**
     * api监控配置
     */
    public static final String API_MONITOR_RULE_SITE = "Api Monitor Rule Site";

    /**
     * api监控分析
     */
    public static final String API_MONITOR_SITE = "Api Monitor Site";

    /**
     * api限制（黑白名单）
     */
    public static final String API_LIMIT_SITE = "Api Limit Site";

    /**
     * 日志追溯分析
     */
    public static final String TRACE = "Trace";

    /**
     * 访问统计(平台级)
     */
    public static final String ACCESS_STATISTICS_SITE = "Access Statistics (Site)";

    /**
     * swagger平台级
     */
    public static final String SWAGGER_SITE = "Swagger (Site)";

    /**
     * 服务实例(平台级)
     */
    public static final String INSTANCE_SITE = "Service Instance (Site)";

    /**
     * 在线运维(平台级)
     */
    public static final String MAINTAIN_SITE = "Maintain Online (Site)";
    public static final String MAINTAIN_TABLE_SITE = "Maintain Table Online (Site)";

    @Autowired
    public SwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(REFRESH, "动态刷新"),
                new Tag(SERVICE_SITE, "服务管理(平台级)"),
                new Tag(SERVICE_CONFIG_SITE, "服务配置(平台级)"),
                new Tag(SERVICE_ROUTE, "服务路由(租户级)"),
                new Tag(SERVICE_ROUTE_SITE, "服务路由(平台级)"),
                new Tag(GATEWAY_RATE_LIMIT_SITE, "网关限流规则(平台级)"),
                new Tag(GATEWAY_RATE_LIMIT_LINE_SITE, "网关限流规则明细(平台级)"),
                new Tag(HYSTRIX_SITE, "保护配置(平台级)"),
                new Tag(HYSTRIX_LINE_SITE, "保护配置明细(平台级)"),
                new Tag(GATEWAY_RATE_LIMIT_DIMENSION_SITE, "网关限流维度配置(平台级)"),
                new Tag(SERVICE_VERSION_SITE, "服务版本(平台级)"),
                new Tag(API_MONITOR_RULE_SITE, "api监控配置(平台级)"),
                new Tag(API_MONITOR_SITE, "api监控分析(平台级)"),
                new Tag(API_LIMIT_SITE, "api限制(平台级)"),
                new Tag(TRACE, "日志追溯分析"),
                new Tag(ACCESS_STATISTICS_SITE, "访问统计(平台级)"),
                new Tag(SWAGGER_SITE, "Swagger(平台级)"),
                new Tag(INSTANCE_SITE, "服务实例(平台级)"),
                new Tag(MAINTAIN_SITE, "在线运维(平台级)"),
                new Tag(MAINTAIN_TABLE_SITE, "在线运维表(平台级)")
        );
    }
}
