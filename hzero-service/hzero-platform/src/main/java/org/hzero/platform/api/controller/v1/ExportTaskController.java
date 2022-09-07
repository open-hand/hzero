package org.hzero.platform.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.platform.config.PlatformSwaggerApiConfig;
import org.hzero.platform.domain.entity.ExportTask;
import org.hzero.platform.domain.repository.ExportTaskRepository;
import org.hzero.platform.app.service.ExportTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 导出任务API
 * @author XCXCXCXCX
 * @date 2019/8/5
 * @project hzero-platform
 */
@Api(tags = {PlatformSwaggerApiConfig.EXPORT_TASK})
@RestController("exportTaskController.v1")
public class ExportTaskController extends BaseController {

    @Autowired
    private ExportTaskService exportTaskService;

    @Autowired
    private ExportTaskRepository exportTaskRepository;

    @ApiOperation(value = "新建导出任务")
    @Permission(permissionWithin = true)
    @PostMapping("/v1/export-task")
    public ResponseEntity<?> create(@RequestBody ExportTask exportTask){
        return Results.success(exportTaskService.insert(exportTask));
    }

    @ApiOperation(value = "更新导出任务")
    @Permission(permissionWithin = true)
    @PutMapping("/v1/export-task")
    public ResponseEntity<ExportTask> update(@RequestBody ExportTask exportTask){
        return Results.success(exportTaskService.updateByTaskCode(exportTask.getTaskCode(), exportTask));
    }

    @ApiOperation(value = "查询导出任务（用户级）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskCode", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "serviceName", value = "所属服务", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "任务状态", paramType = "query")
    })
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/v1/{organizationId}/self/export-task")
    public ResponseEntity<Page<ExportTask>> selectByUser(@PathVariable(value = "organizationId") Long tenantId,
                                                   @RequestParam(value = "taskCode", required = false) String taskCode,
                                                   @RequestParam(value = "taskName", required = false) String taskName,
                                                   @RequestParam(value = "serviceName", required = false) String serviceName,
                                                   @RequestParam(value = "state", required = false) String state,
                                                   @ApiIgnore @SortDefault(value = ExportTask.FIELD_TASK_ID, direction = Sort.Direction.DESC) PageRequest pageRequest){

        return Results.success(exportTaskRepository.getExportTaskByUser(pageRequest, tenantId, taskCode, taskName, serviceName, state));
    }

    @ApiOperation(value = "查询导出任务（租户级）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskCode", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "serviceName", value = "所属服务", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "任务状态", paramType = "query")
    })
    @Permission(permissionLogin = true)
    @ProcessLovValue(targetField = "body")
    @GetMapping("/v1/{organizationId}/export-task")
    public ResponseEntity<Page<ExportTask>> selectByTenant(@PathVariable(value = "organizationId") Long tenantId,
                                                   @RequestParam(value = "taskCode", required = false) String taskCode,
                                                   @RequestParam(value = "taskName", required = false) String taskName,
                                                   @RequestParam(value = "serviceName", required = false) String serviceName,
                                                   @RequestParam(value = "state", required = false) String state,
                                                   @ApiIgnore @SortDefault(value = ExportTask.FIELD_TASK_ID, direction = Sort.Direction.DESC) PageRequest pageRequest){

        return Results.success(exportTaskRepository.getExportTaskByTenant(pageRequest, tenantId, taskCode, taskName, serviceName, state));
    }

    @ApiOperation(value = "取消导出任务")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PutMapping("/v1/{organizationId}/export-task/cancel")
    public ResponseEntity<?> cancel(@PathVariable(value = "organizationId") Long tenantId,
                                          @RequestParam("taskCode") String taskCode){
        exportTaskService.cancel(tenantId, taskCode);
        return Results.success();
    }

}
