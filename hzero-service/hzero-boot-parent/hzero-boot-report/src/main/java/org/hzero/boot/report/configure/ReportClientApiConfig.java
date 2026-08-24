package org.hzero.boot.report.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger api
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 10:18
 */
@Configuration
public class ReportClientApiConfig {

    public static final String REPORT_EXECUTE = "Report Execute(Client)";

    @Autowired
    public ReportClientApiConfig(Docket docket) {
        docket.tags(
                new Tag(REPORT_EXECUTE, "报表执行(客户端)")
        );
    }
}
