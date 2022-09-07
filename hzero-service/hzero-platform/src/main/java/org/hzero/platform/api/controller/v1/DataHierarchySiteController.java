package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.app.service.DataHierarchyService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 数据层级配置 管理 API
 *
 * @author qingsheng.chen@hand-china.com
 */
@Api(tags = PlatformSwaggerApiConfig.DATA_HIERARCHY_SITE)
@RestController("dataHierarchySiteController.v1")
@RequestMapping("/v1/data-hierarchies")
public class DataHierarchySiteController extends BaseController {
    private DataHierarchyService dataHierarchyService;

    @Autowired
    public DataHierarchySiteController(DataHierarchyService dataHierarchyService) {
        this.dataHierarchyService = dataHierarchyService;
    }


    @ApiOperation("查询数据层级配置树形列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    public ResponseEntity<List<DataHierarchyDTO>> tree(@ApiParam(value = "租户ID", required = true) @RequestParam long tenantId,
                                                       @ApiParam("数据层级编码") @RequestParam(required = false) String dataHierarchyCode,
                                                       @ApiParam("数据层级名称") @RequestParam(required = false) String dataHierarchyName) {
        return Results.success(dataHierarchyService.treeDataHierarchy(tenantId, dataHierarchyCode, dataHierarchyName, null));
    }

    @ApiOperation("查询数据层级配置明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{dataHierarchyId}")
    public ResponseEntity<DataHierarchyDTO> detail(@ApiParam(value = "租户ID", required = true) @RequestParam long tenantId,
                                                   @ApiParam(value = "租户ID", required = true) @PathVariable @Encrypt Long dataHierarchyId) {
        return Results.success(dataHierarchyService.getDataHierarchy(tenantId, dataHierarchyId));
    }

    @ApiOperation(value = "创建数据层级配置")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<DataHierarchy> create(@RequestBody @Encrypt DataHierarchy dataHierarchy) {
        validObject(dataHierarchy);
        return Results.success(dataHierarchyService.createDataHierarchy(dataHierarchy));
    }

    @ApiOperation(value = "修改数据层级配置")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<DataHierarchy> update(@RequestBody @Encrypt DataHierarchy dataHierarchy) {
        SecurityTokenHelper.validToken(dataHierarchy);
        validObject(dataHierarchy);
        return Results.success(dataHierarchyService.updateDataHierarchy(dataHierarchy));
    }

    @ApiOperation(value = "删除数据层级配置")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody @Encrypt DataHierarchy dataHierarchy) {
        SecurityTokenHelper.validToken(dataHierarchy);
        dataHierarchyService.deleteDataHierarchy(dataHierarchy);
        return Results.success();
    }

}
