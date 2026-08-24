package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.app.service.ExecutorService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.Executor;
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

/**
 * 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
@Api(tags = SchedulerSwaggerApiConfig.EXECUTOR)
@RestController("executorController.v1")
@RequestMapping("/v1/{organizationId}/executors")
public class ExecutorController extends BaseController {

    private final ExecutorService executorService;

    @Autowired
    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @ApiOperation(value = "查询执行器列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @CustomPageRequest
    @GetMapping
    @ApiImplicitParams({
            @ApiImplicitParam(name = "executorCode", value = "执行器编码", paramType = "query"),
            @ApiImplicitParam(name = "executorName", value = "执行器名称", paramType = "query"),
            @ApiImplicitParam(name = "executorType", value = "执行器类型", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "执行器状态", paramType = "query")
    })
    public ResponseEntity<Page<Executor>> pageExecutor(@ApiIgnore @SortDefault(value = Executor.FIELD_EXECUTOR_ID, direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                       @PathVariable Long organizationId, String executorCode, String executorName, Integer executorType, String status) {
        return Results.success(executorService.pageExecutor(pageRequest, executorCode, executorName, executorType, status, organizationId));
    }

    @ApiOperation(value = "创建执行器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Executor> createExecutor(@PathVariable Long organizationId, @RequestBody @Encrypt Executor executor) {
        executor.setTenantId(organizationId);
        validObject(executor);
        return Results.success(executorService.createExecutor(executor));
    }

    @ApiOperation(value = "更新执行器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Executor> updateExecutor(@PathVariable Long organizationId, @RequestBody @Encrypt Executor executor) {
        executor.setTenantId(organizationId);
        validObject(executor);
        SecurityTokenHelper.validToken(executor);
        return Results.success(executorService.updateExecutor(executor));
    }

    @ApiOperation(value = "删除执行器")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteExecutor(@RequestBody @Encrypt Executor executor) {
        SecurityTokenHelper.validToken(executor);
        executorService.deleteExecutor(executor.getExecutorId());
        return Results.success();
    }

    @ApiOperation(value = "客户端刷新执行器")
    @Permission(permissionWithin = true)
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshExecutor(@PathVariable Long organizationId, @RequestParam String executorCode, @RequestParam String serverName) {
        return Results.success(executorService.refreshExecutor(executorCode, serverName));
    }
}
