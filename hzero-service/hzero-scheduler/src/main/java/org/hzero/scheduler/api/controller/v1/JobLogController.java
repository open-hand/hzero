package org.hzero.scheduler.api.controller.v1;

import java.util.Date;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.core.util.ValidUtils;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.api.dto.JobLogQueryDTO;
import org.hzero.scheduler.app.service.JobLogService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.JobLog;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IJobService;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-23 11:36:39
 */
@Api(tags = SchedulerSwaggerApiConfig.JOB_LOG)
@RestController("jobLogController.v1")
@RequestMapping("/v1/{organizationId}/job-logs")
public class JobLogController extends BaseController {

    private final IJobService jobService;
    private final JobLogService jobLogService;
    private final JobLogRepository jobLogRepository;

    @Autowired
    public JobLogController(IJobService jobService,
                            JobLogService jobLogService,
                            JobLogRepository jobLogRepository) {
        this.jobService = jobService;
        this.jobLogService = jobLogService;
        this.jobLogRepository = jobLogRepository;
    }

    @ApiOperation(value = "任务日志列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "任务Id", paramType = "query"),
            @ApiImplicitParam(name = "jobCode", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "任务描述", paramType = "query"),
            @ApiImplicitParam(name = "jobResult", value = "调度结果", paramType = "query"),
            @ApiImplicitParam(name = "clientResult", value = "执行结果", paramType = "query"),
            @ApiImplicitParam(name = "executorName", value = "执行器名称", paramType = "query"),
            @ApiImplicitParam(name = "timeStart", value = "调度时间从", paramType = "query"),
            @ApiImplicitParam(name = "timeEnd", value = "调度时间至", paramType = "query")
    })
    public ResponseEntity<Page<JobLog>> pageLog(@PathVariable Long organizationId, @Encrypt JobLogQueryDTO queryDTO,
                                                @ApiIgnore @SortDefault(value = JobLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        if (queryDTO.getTimeStart() != null && queryDTO.getTimeEnd() != null) {
            ValidUtils.isSameOrAfterDay(queryDTO.getTimeStart(), queryDTO.getTimeEnd());
        }
        queryDTO.setTenantId(organizationId);
        return Results.success(jobLogRepository.pageLog(queryDTO, pageRequest));
    }

    @ApiOperation(value = "单个任务日志列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @GetMapping("/{jobId}/logs")
    @CustomPageRequest
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobResult", value = "调度结果", paramType = "query"),
            @ApiImplicitParam(name = "clientResult", value = "执行结果", paramType = "query"),
            @ApiImplicitParam(name = "timeStart", value = "调度时间从", paramType = "query"),
            @ApiImplicitParam(name = "timeEnd", value = "调度时间至", paramType = "query")
    })
    public ResponseEntity<Page<JobLog>> pageLogByJobId(@PathVariable Long organizationId, @PathVariable @Encrypt Long jobId, String jobResult, String clientResult, Date timeStart, Date timeEnd,
                                                       @ApiIgnore @SortDefault(value = JobLog.FIELD_LOG_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        if (timeStart != null && timeEnd != null) {
            ValidUtils.isSameOrAfterDay(timeStart, timeEnd);
        }
        return Results.success(jobLogService.pageLog(organizationId, jobId, jobResult, clientResult, timeStart, timeEnd, pageRequest));
    }

    @ApiOperation(value = "查询任务进度")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{logId}/progress")
    public ResponseEntity<JobProgress> jobProgress(@PathVariable Long organizationId,
                                                   @PathVariable @Encrypt Long logId) {
        return Results.success(jobLogService.jobProgress(logId, organizationId));
    }

    @ApiOperation(value = "查询任务异常详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{logId}/error-detail")
    public ResponseEntity<String> errorDetail(@PathVariable Long organizationId,
                                              @PathVariable @Encrypt Long logId) {
        return Results.success(jobLogService.errorDetail(logId, organizationId));
    }

    @ApiOperation(value = "查询任务日志详情")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{logId}/log-detail")
    public ResponseEntity<String> logDetail(@PathVariable Long organizationId,
                                            @PathVariable @Encrypt Long logId) {
        return Results.success(jobLogService.logDetail(logId, organizationId));
    }

    @ApiOperation(value = "单条删除日志")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<Void> removeLog(@RequestBody @Encrypt JobLog jobLog) {
        SecurityTokenHelper.validToken(jobLog);
        jobLogService.deleteByLogId(jobLog.getLogId());
        return Results.success();
    }

    @ApiOperation(value = "日志清理")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearLog(@PathVariable Long organizationId, @RequestParam String clearType, @RequestParam(required = false) @Encrypt Long jobId) {
        jobLogService.clearLog(clearType, jobId, organizationId);
        return Results.success();
    }

    @ApiOperation(value = "客户端日志回写")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @PostMapping("/callback")
    public ResponseEntity<String> callback(@PathVariable Long organizationId, @RequestBody JobLogDTO jobLogDTO) {
        jobService.callback(jobLogDTO);
        return Results.success(BootSchedulerConstant.Response.SUCCESS);
    }
}
