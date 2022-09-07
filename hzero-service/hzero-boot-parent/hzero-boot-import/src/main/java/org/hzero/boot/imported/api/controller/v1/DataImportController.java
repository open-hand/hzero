package org.hzero.boot.imported.api.controller.v1;

import com.fasterxml.jackson.databind.JsonNode;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.ObjectUtils;
import org.hzero.boot.imported.api.dto.ImportDTO;
import org.hzero.boot.imported.app.service.ImportDataService;
import org.hzero.boot.imported.config.ImportClientApiConfig;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
 * 通用导入Controller
 *
 * @author shuangfei.zhu@hand-china.com
 */
@RestController("importController.v1")
@RequestMapping(value = "/v1/{organizationId}/import/data")
@Api(tags = ImportClientApiConfig.IMPORT_DATA)
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);

    @Autowired
    private ImportDataService importDataService;
    @Autowired
    private ImportRepository importRepository;

    @ApiOperation(value = "从文件导入临时表(异步)")
    @PostMapping("/data-upload")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<String> uploadData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                             @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                             @RequestParam(required = false) @ApiParam(value = "自定义参数") String param,
                                             @ApiParam(value = "excel") MultipartFile excel) throws IOException {
        return Results.success(importDataService.uploadData(organizationId, templateCode, param, excel));
    }

    @ApiOperation(value = "验证临时表数据(异步)")
    @PostMapping("/data-validate")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Import> validateData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                               @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                               @RequestParam @ApiParam(value = "批次号", required = true) String batch,
                                               @RequestBody(required = false) @Encrypt Map<String, Object> args) {
        return Results.success(importDataService.validateData(organizationId, templateCode, batch, ObjectUtils.defaultIfNull(args, new HashMap<>(4))));
    }

    @ApiOperation(value = "从临时表导入正式表(异步)")
    @PostMapping("/data-import")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<ImportDTO> importData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                @RequestParam @ApiParam(value = "批次号", required = true) String batch,
                                                @RequestBody(required = false) @Encrypt Map<String, Object> args) {
        return Results.success(importDataService.importData(organizationId, templateCode, batch, ObjectUtils.defaultIfNull(args, new HashMap<>(4))));
    }

    @ApiOperation(value = "查询数据")
    @GetMapping
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Page<ImportData>> pageData(@RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                     @RequestParam @ApiParam(value = "批次号", required = true) String batch,
                                                     @RequestParam(required = false) Integer sheetIndex,
                                                     @RequestParam(required = false) DataStatus status,
                                                     @ApiIgnore @SortDefault(value = ImportData.FIELD_ID) PageRequest pageRequest) {
        return Results.success(importDataService.pageData(templateCode, batch, sheetIndex, status, pageRequest));
    }

    @ApiOperation(value = "更新单条数据")
    @PutMapping("/{id}")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<String> updateImportData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                   @PathVariable(value = "id") @ApiParam(value = "临时数据Id", required = true) @Encrypt Long id,
                                                   @RequestBody String data) {
        return Results.success(importDataService.updateImportData(id, data));
    }

    @ApiOperation(value = "删除单条数据")
    @DeleteMapping("/{id}")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<String> deleteImportData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                   @PathVariable(value = "id") @ApiParam(value = "临时数据Id", required = true) @Encrypt Long id) {
        importDataService.deleteById(id);
        return Results.success();
    }

    @ApiOperation(value = "状态查询")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/status")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Import> getStatus(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                            @RequestParam @ApiParam(value = "批次", required = true) String batch) {
        return Results.success(importRepository.getStatus(batch));
    }

    @ApiOperation(value = "自动导入到正式表，一次执行三个流程(同步)")
    @PostMapping("/sync/auto-import")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<String> autoImport(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                             @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                             @RequestParam(required = false) @ApiParam(value = "自定义参数") String param,
                                             @ApiParam(value = "excel") MultipartFile excel) {
        String batch = importDataService.syncUploadData(organizationId, templateCode, param, excel);
        try {
            importDataService.syncValidateData(organizationId, templateCode, batch, new HashMap<>(4));
            importDataService.syncImportData(organizationId, templateCode, batch, new HashMap<>(4));
        } catch (Exception e) {
            logger.error("导入数据异常！", e);
        }
        return Results.success(batch);
    }

    @ApiOperation(value = "从文件导入临时表(同步)")
    @PostMapping("/sync/data-upload")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<String> syncUploadData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                 @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                 @RequestParam(required = false) @ApiParam(value = "自定义参数") String param,
                                                 @ApiParam(value = "excel") MultipartFile excel) {
        return Results.success(importDataService.syncUploadData(organizationId, templateCode, param, excel));
    }

    @ApiOperation(value = "验证临时表数据(同步)")
    @PostMapping("/sync/data-validate")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<Import> syncValidateData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                   @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                   @RequestParam @ApiParam(value = "批次号", required = true) String batch,
                                                   @RequestBody(required = false) @Encrypt Map<String, Object> args) {
        return Results.success(importDataService.syncValidateData(organizationId, templateCode, batch, ObjectUtils.defaultIfNull(args, new HashMap<>(4))));
    }

    @ApiOperation(value = "从临时表导入正式表(同步)")
    @PostMapping("/sync/data-import")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<ImportDTO> syncImportData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                    @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                    @RequestParam @ApiParam(value = "批次号", required = true) String batch,
                                                    @RequestBody(required = false) @Encrypt Map<String, Object> args) {
        return Results.success(importDataService.syncImportData(organizationId, templateCode, batch, ObjectUtils.defaultIfNull(args, new HashMap<>(4))));
    }

    @ApiOperation(value = "导入JSON到临时表/正式表")
    @PostMapping("/data-upload/json")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    public ResponseEntity<String> uploadJsonData(@PathVariable @ApiParam(value = "租户Id", required = true) Long organizationId,
                                                 @RequestParam @ApiParam(value = "模板编码", required = true) String templateCode,
                                                 @RequestParam(required = false) @ApiParam(value = "自定义参数") String param,
                                                 @RequestParam(defaultValue = "0") @ApiParam(value = "sheet页下标") Integer sheetIndex,
                                                 @RequestParam(defaultValue = "0") @ApiParam(value = "自动导入到正式表标识") Integer autoFlag,
                                                 @RequestBody JsonNode data) {
        String batch = importDataService.syncUploadData(organizationId, templateCode, param, sheetIndex, data);
        if (BaseConstants.Flag.YES.equals(autoFlag)) {
            importDataService.syncValidateData(organizationId, templateCode, batch, new HashMap<>(4));
            importDataService.syncImportData(organizationId, templateCode, batch, new HashMap<>(4));
        }
        return Results.success(batch);
    }

}