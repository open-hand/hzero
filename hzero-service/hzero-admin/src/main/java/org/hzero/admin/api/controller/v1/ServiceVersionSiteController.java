
package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.ServiceVersionService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/11/29 1:53 下午
 */
@Api(tags = SwaggerApiConfig.SERVICE_VERSION_SITE)
@RestController("serviceVersionSiteController.v1")
@RequestMapping("/v1/service-versions")
public class ServiceVersionSiteController {

    @Autowired
    private ServiceVersionService serviceVersionService;

    @ApiOperation(value = "分页查询服务版本列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<ServiceVersion>> page(@Encrypt @RequestParam("serviceId") Long serviceId, @ApiIgnore PageRequest pageRequest) {
        return Results.success(serviceVersionService.page(pageRequest, serviceId));
    }

}
