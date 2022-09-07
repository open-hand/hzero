package org.hzero.boot.imported.api.controller.v1;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.hzero.boot.imported.app.service.ImportDataService;
import org.hzero.boot.imported.config.ImportClientApiConfig;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.cache.ProcessCacheValue;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 导入管理
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/16 11:20
 */
@RestController("importManagerController.v1")
@RequestMapping(value = "/v1/{organizationId}/import/manager")
@Api(tags = ImportClientApiConfig.IMPORT_MANAGER)
public class ImportManagerController {

    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private ImportDataService importDataService;

    @ApiOperation(value = "查询导入历史")
    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ProcessCacheValue
    public ResponseEntity<Page<Import>> pageData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                 @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                 @RequestParam(required = false) @ApiParam(value = "创建时间从") Date creationDateFrom,
                                                 @RequestParam(required = false) @ApiParam(value = "创建时间至") Date creationDateTo,
                                                 @ApiIgnore @SortDefault(value = Import.FIELD_IMPORT_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(importRepository.pageImportHistory(organizationId, templateCode, creationDateFrom, creationDateTo, pageRequest));
    }

    @ApiOperation(value = "查询单条导入历史数据")
    @GetMapping("/{importId}")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessCacheValue
    public ResponseEntity<Import> queryImport(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                              @PathVariable @ApiParam(value = "导入历史Id", required = true) @Encrypt Long importId) {
        return Results.success(importRepository.selectByPrimaryKey(importId));
    }

    @ApiOperation(value = "导出临时数据Excel")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/export/excel")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public void exportExcel(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                            @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                            @RequestParam @ApiParam(value = "批次", required = true) String batch,
                            @RequestParam(required = false) Integer sheetIndex,
                            @RequestParam(required = false) DataStatus status,
                            HttpServletResponse response) {
        importDataService.exportExcelData(organizationId, templateCode, batch, sheetIndex, status, response);
    }

    @ApiOperation(value = "导出临时数据Csv")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/export/csv")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public void exportCsv(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                          @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                          @RequestParam @ApiParam(value = "批次", required = true) String batch,
                          @RequestParam(required = false) Integer sheetIndex,
                          @RequestParam(required = false) DataStatus status,
                          HttpServletResponse response) {
        importDataService.exportCsvData(organizationId, templateCode, batch, sheetIndex, status, response);
    }

    @ApiOperation(value = "清理导入历史")
    @DeleteMapping
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @CustomPageRequest
    public ResponseEntity clearHistory(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                       @RequestBody List<Import> importList) {
        importDataService.clearData(importList);
        return Results.success();
    }
}
