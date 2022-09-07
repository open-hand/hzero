package org.hzero.boot.imported.config;

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
public class ImportClientApiConfig {
    public static final String TEMPLATE_CLIENT = "Template(Client)";
    public static final String LOCAL_TEMPLATE = "Local template(Client)";
    public static final String IMPORT_DATA = "Import Data(Client)";
    public static final String IMPORT_MANAGER = "Import Manager(Client)";

    @Autowired
    public ImportClientApiConfig(Docket docket) {
        docket.tags(
                new Tag(TEMPLATE_CLIENT, "通用导入模板(客户端)"),
                new Tag(LOCAL_TEMPLATE, "本地模板"),
                new Tag(IMPORT_DATA, "通用导入"),
                new Tag(IMPORT_MANAGER, "导入管理")
        );
    }
}
