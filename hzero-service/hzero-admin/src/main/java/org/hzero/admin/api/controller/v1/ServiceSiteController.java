package org.hzero.admin.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.admin.app.service.HServiceService;
import org.hzero.admin.config.SwaggerApiConfig;
import org.hzero.admin.domain.entity.HService;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用服务 管理 API
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-14 10:35:51
 */
@Api(tags = SwaggerApiConfig.SERVICE_SITE)
@RestController("serviceSiteController.v1")
@RequestMapping("/v1/services")
public class ServiceSiteController extends BaseController {

    @Autowired
    private HServiceService serviceService;

    @ApiOperation(value = "分页查询应用服务列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<Page<HService>> pageServices(@Encrypt HService service, @ApiIgnore PageRequest pageRequest) {
        return Results.success(serviceService.pageService(service, pageRequest));
    }

    @ApiOperation(value = "创建应用服务信息")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<HService> createService(@Encrypt @RequestBody HService service) {
        return Results.success(serviceService.createService(service));
    }

    @ApiOperation(value = "修改应用服务信息")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<HService> updateService(@Encrypt @RequestBody HService service) {
        return Results.success(serviceService.updateService(service));
    }

    @ApiOperation(value = "获取应用服务明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{serviceId}")
    public ResponseEntity<HService> serviceDetails(@Encrypt @PathVariable("serviceId") Long serviceId) {
        return Results.success(serviceService.selectServiceDetails(serviceId));
    }

    @ApiOperation(value = "删除应用服务")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity removeService(@Encrypt @RequestBody HService service) {
        serviceService.removeService(service);
        return Results.success();
    }

    @ApiOperation(value = "下载服务版本信息")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/download/yml")
    public void downloadYml(@Encrypt HService service, HttpServletRequest request, HttpServletResponse response) {
        serviceService.downloadYml(service, request, response);
    }
}
