package org.hzero.imported.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/26 9:50
 */
@Configuration
public class ImportSwaggerApiConfig {

    public static final String TEMPLATE_MANAGER = "Template Manager";
    public static final String TEMPLATE_MANAGER_SITE = "Template Manager(Site Level)";
    public static final String TEMPLATE_HEADER = "Template Header";
    public static final String TEMPLATE_HEADER_SITE = "Template Header(Site Level)";
    public static final String TEMPLATE_LINE = "Template Line";
    public static final String TEMPLATE_LINE_SITE = "Template Line(Site Level)";

    @Autowired
    public ImportSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(TEMPLATE_MANAGER, "模板功能相关"),
                new Tag(TEMPLATE_MANAGER_SITE, "模板功能相关(平台级)"),
                new Tag(TEMPLATE_HEADER, "模板头管理接口"),
                new Tag(TEMPLATE_HEADER_SITE, "模板头管理接口(平台级)"),
                new Tag(TEMPLATE_LINE, "模板行管理接口"),
                new Tag(TEMPLATE_LINE_SITE, "模板行管理接口(平台级)")
        );
    }
}
