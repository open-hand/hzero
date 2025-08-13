package org.hzero.boot.imported.api.controller.v1;

import javax.servlet.http.HttpServletResponse;

import org.hzero.boot.imported.app.service.TemplateClientService;
import org.hzero.boot.imported.config.ImportClientApiConfig;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 通用导入模板客户端
 * </p>
 *
 * @author qingsheng.chen 2019/1/25 星期五 17:34
 */
@Api(tags = ImportClientApiConfig.TEMPLATE_CLIENT)
@RestController("templateClientController.v1")
@RequestMapping(value = "/v1/{organizationId}/import/template")
public class TemplateClientController {

    private final TemplateClientService templateService;

    @Autowired
    public TemplateClientController(TemplateClientService templateService) {
        this.templateService = templateService;
    }

    @ApiOperation(value = "导出Excel模板")
    @GetMapping(value = "/{templateCode}/excel", produces = "application/octet-stream;charset=UTF-8")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public void export(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                       @PathVariable @ApiParam(value = "模板编码", required = true) String templateCode,
                       HttpServletResponse response) {
        templateService.exportExcel(() -> templateService.getTemplate(organizationId, templateCode), response);
    }

    @ApiOperation(value = "导出Csv模板")
    @GetMapping(value = "/{templateCode}/csv", produces = "application/octet-stream;charset=UTF-8")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public void exportCsv(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                          @PathVariable @ApiParam(value = "模板编码", required = true) String templateCode,
                          HttpServletResponse response) {
        templateService.exportCsv(() -> templateService.getTemplate(organizationId, templateCode), response);
    }

    @ApiOperation(value = "获取模板头行详细信息")
    @GetMapping(value = "/{templateCode}/info")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<Template> getTemplateInfo(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                    @PathVariable @ApiParam(value = "模板编码", required = true) String templateCode) {
        return Results.success(templateService.getTemplateWithMulti(organizationId, templateCode));
    }
}
