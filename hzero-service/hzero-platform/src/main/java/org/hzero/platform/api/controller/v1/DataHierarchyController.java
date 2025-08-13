package org.hzero.platform.api.controller.v1;

import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.helper.UniqueHelper;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.app.service.DataHierarchyService;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
@Api(tags = PlatformSwaggerApiConfig.DATA_HIERARCHY)
@RestController("dataHierarchyController.v1")
@RequestMapping("/v1/{organizationId}/data-hierarchies")
public class DataHierarchyController extends BaseController {
    private DataHierarchyService dataHierarchyService;

    @Autowired
    public DataHierarchyController(DataHierarchyService dataHierarchyService) {
        this.dataHierarchyService = dataHierarchyService;
    }


    @ApiOperation("查询数据层级配置树形列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<List<DataHierarchyDTO>> tree(
                    @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                    @ApiParam("数据层级编码") @RequestParam(required = false) String dataHierarchyCode,
                    @ApiParam("数据层级名称") @RequestParam(required = false) String dataHierarchyName) {
        return Results.success(dataHierarchyService.treeDataHierarchy(organizationId, dataHierarchyCode,
                        dataHierarchyName, null));
    }

    @ApiOperation("查询数据层级配置明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{dataHierarchyId}")
    public ResponseEntity<DataHierarchyDTO> detail(
                    @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                    @ApiParam(value = "租户ID", required = true) @PathVariable @Encrypt Long dataHierarchyId) {
        return Results.success(dataHierarchyService.getDataHierarchy(organizationId, dataHierarchyId));
    }

    @ApiOperation(value = "创建数据层级配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<DataHierarchy> create(
                    @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                    @RequestBody @Encrypt DataHierarchy dataHierarchy) {
        validObject(dataHierarchy.setTenantId(organizationId));
        Assert.isTrue(UniqueHelper.valid(dataHierarchy), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        return Results.success(dataHierarchyService.createDataHierarchy(dataHierarchy));
    }

    @ApiOperation(value = "修改数据层级配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<DataHierarchy> update(
                    @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                    @RequestBody @Encrypt DataHierarchy dataHierarchy) {
        SecurityTokenHelper.validToken(dataHierarchy.setTenantId(organizationId));
        validObject(dataHierarchy);
        return Results.success(dataHierarchyService.updateDataHierarchy(dataHierarchy));
    }

    @ApiOperation(value = "删除数据层级配置")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> delete(@ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
                    @RequestBody @Encrypt DataHierarchy dataHierarchy) {
        SecurityTokenHelper.validToken(dataHierarchy.setTenantId(organizationId));
        dataHierarchyService.deleteDataHierarchy(dataHierarchy);
        return Results.success();
    }

}
