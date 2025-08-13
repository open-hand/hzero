package org.hzero.scheduler.api.controller.v1;

import io.swagger.annotations.*;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.app.service.ExecutableService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.Executable;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
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
 * 并发可执行 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-10 10:30:58
 */
@Api(tags = SchedulerSwaggerApiConfig.EXECUTABLE)
@RestController("executableController.v1")
@RequestMapping("/v1/{organizationId}/executables")
public class ExecutableController extends BaseController {

    private final ExecutableService executableService;
    private final ExecutableRepository executableRepository;

    @Autowired
    public ExecutableController(ExecutableService executableService,
                                ExecutableRepository executableRepository) {
        this.executableService = executableService;
        this.executableRepository = executableRepository;
    }

    @ApiOperation(value = "并发可执行列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "executorId", value = "执行器Id", paramType = "query"),
            @ApiImplicitParam(name = "executableName", value = "可执行名称", paramType = "query"),
            @ApiImplicitParam(name = "executableDesc", value = "可执行描述", paramType = "query")
    })
    public ResponseEntity<Page<Executable>> pageExecution(@PathVariable Long organizationId, Long executorId, String executableName, String executableDesc,
                                                          @ApiIgnore @SortDefault(value = Executable.FIELD_EXECUTABLE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(executableRepository.pageExecutable(organizationId, executorId, executableName, executableDesc, pageRequest));
    }

    @ApiOperation(value = "并发可执行明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{executableId}")
    public ResponseEntity<Executable> detailExecutable(@PathVariable Long organizationId,
                                                       @PathVariable @ApiParam(value = "可执行ID", required = true) @Encrypt Long executableId) {
        return Results.success(executableRepository.selectExecutableById(executableId, organizationId));
    }

    @ApiOperation(value = "创建并发可执行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<Executable> createExecutable(@PathVariable Long organizationId, @RequestBody @Encrypt Executable executable) {
        executable.setTenantId(organizationId);
        validObject(executable);
        return Results.success(executableService.createExecutable(executable));
    }

    @ApiOperation(value = "修改并发可执行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<Executable> updateExecution(@PathVariable Long organizationId, @RequestBody @Encrypt Executable executable) {
        executable.setTenantId(organizationId);
        SecurityTokenHelper.validToken(executable);
        validObject(executable, Executable.Validate.class);
        return Results.success(executableService.updateExecutable(executable));
    }

    @ApiOperation(value = "删除并发可执行")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> deleteExecution(@PathVariable Long organizationId, @RequestBody @Encrypt Executable executable) {
        executable.setTenantId(organizationId);
        SecurityTokenHelper.validToken(executable);
        executableService.deleteExecutable(executable.getExecutableId());
        return Results.success();
    }
}