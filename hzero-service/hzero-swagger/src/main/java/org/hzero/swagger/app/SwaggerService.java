package org.hzero.swagger.app;

import java.util.List;

import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;

public interface SwaggerService {

    List<SwaggerResource> getSwaggerResource();

    UiConfiguration getUiConfiguration();

    SecurityConfiguration getSecurityConfiguration();

    /**
     * 更新或者插入swagger json
     *
     * @param serviceName 服务名
     * @param version     版本
     * @param json        swagger json
     */
    void updateOrInsertSwagger(String serviceName, String version, String json);
}
