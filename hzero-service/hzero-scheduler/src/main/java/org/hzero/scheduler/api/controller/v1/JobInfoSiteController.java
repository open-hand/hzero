package org.hzero.scheduler.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.scheduler.api.dto.JobInfoQueryDTO;
import org.hzero.scheduler.app.service.JobInfoService;
import org.hzero.scheduler.config.SchedulerSwaggerApiConfig;
import org.hzero.scheduler.domain.entity.JobInfo;
import org.hzero.scheduler.domain.repository.JobInfoRepository;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.domain.service.IJobService;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
 * 任务管理 API
 *
 * @author shuangfei.zhu@hand-china.com 2019-01-10 10:57:29
 */
@Api(tags = SchedulerSwaggerApiConfig.JOB_INFO_SITE)
@RestController("jobInfoSiteController.v1")
@RequestMapping("/v1/job-info")
public class JobInfoSiteController extends BaseController {

    private final IJobService jobService;
    private final JobInfoService jobInfoService;
    private final JobLogRepository jobLogRepository;
    private final JobInfoRepository jobInfoRepository;

    @Autowired
    public JobInfoSiteController(IJobService jobService,
                                 JobInfoService jobInfoService,
                                 JobLogRepository jobLogRepository,
                                 JobInfoRepository jobInfoRepository) {
        this.jobService = jobService;
        this.jobInfoService = jobInfoService;
        this.jobLogRepository = jobLogRepository;
        this.jobInfoRepository = jobInfoRepository;
    }

    @ApiOperation(value = "任务列表")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping
    @CustomPageRequest
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "任务Id", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户Id", paramType = "query"),
            @ApiImplicitParam(name = "jobCode", value = "任务编码", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "任务描述", paramType = "query"),
            @ApiImplicitParam(name = "jobHandler", value = "jobHandler", paramType = "query"),
            @ApiImplicitParam(name = "glueType", value = "任务类型", paramType = "query"),
            @ApiImplicitParam(name = "jobStatus", value = "任务状态", paramType = "query"),
            @ApiImplicitParam(name = "sourceFlag", value = "来源标识", paramType = "query")
    })
    public ResponseEntity<Page<JobInfo>> pageJob(JobInfoQueryDTO jobInfoQueryDTO,
                                                 @ApiIgnore @SortDefault(value = JobInfo.FIELD_JOB_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(jobInfoService.pageJobInfo(jobInfoQueryDTO, pageRequest));
    }

    @ApiOperation(value = "任务明细")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{jobId}")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<JobInfo> detailJob(@PathVariable @Encrypt Long jobId) {
        return Results.success(jobInfoService.detailJobInfo(jobId, null));
    }

    @ApiOperation(value = "任务复制")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping("/{jobId}/copy")
    @ProcessLovValue(targetField = BaseConstants.FIELD_BODY)
    public ResponseEntity<JobInfo> copyJob(@PathVariable @Encrypt Long jobId) {
        return Results.success(jobInfoService.detailJobInfo(jobId, null).setJobId(null));
    }

    @ApiOperation(value = "任务创建")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping
    public ResponseEntity<JobInfo> createJob(@RequestBody @Encrypt JobInfo jobInfo) {
        validObject(jobInfo);
        return Results.success(jobInfoService.createJob(jobInfo));
    }

    @ApiOperation(value = "任务修改")
    @Permission(level = ResourceLevel.SITE)
    @PutMapping
    public ResponseEntity<JobInfo> updateJob(@RequestBody @Encrypt JobInfo jobInfo) {
        SecurityTokenHelper.validToken(jobInfo);
        return Results.success(jobInfoService.updateJob(jobInfo));
    }

    @ApiOperation(value = "批量任务删除")
    @Permission(level = ResourceLevel.SITE)
    @DeleteMapping
    public ResponseEntity<Void> deleteJob(@RequestBody @Encrypt List<JobInfo> jobInfoList) {
        // 校验任务状态
        jobInfoList.forEach(item ->
                Assert.isTrue(Objects.equals(jobService.getJobStatus(item.getJobId(), item.getTenantId()), HsdrConstant.JobStatus.NONE), HsdrErrorCode.JOB_IS_RUNNING));
        SecurityTokenHelper.validToken(jobInfoList);
        jobInfoService.deleteJob(jobInfoList);
        return Results.success();
    }

    @ApiOperation(value = "立即执行")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/trigger")
    public ResponseEntity<Void> triggerJob(@RequestBody @Encrypt JobInfo jobInfo) {
        JobInfo info = jobInfoRepository.selectByPrimaryKey(jobInfo.getJobId());
        if (info != null) {
            jobService.trigger(jobInfo.getJobId(), info.getTenantId());
        }
        return Results.success();
    }

    @ApiOperation(value = "任务挂起")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/pause")
    public ResponseEntity<Void> pauseJob(@RequestBody @Encrypt JobInfo jobInfo, HttpServletRequest request) {
        JobInfo info = jobInfoRepository.selectByPrimaryKey(jobInfo.getJobId());
        if (info != null) {
            String jwtToken = request.getHeader("jwt_token");
            jobInfoService.pauseClient(info, jwtToken);
            jobService.pauseJob(jobInfo.getJobId(), info.getTenantId());
        }
        return Results.success();
    }

    @ApiOperation(value = "恢复任务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/resume")
    public ResponseEntity<Void> resumeJob(@RequestBody @Encrypt JobInfo jobInfo) {
        JobInfo info = jobInfoRepository.selectByPrimaryKey(jobInfo.getJobId());
        if (info != null) {
            jobService.resumeJob(info.getJobId(), info.getTenantId());
        }
        return Results.success();
    }

    @ApiOperation(value = "终止任务")
    @Permission(level = ResourceLevel.SITE)
    @PostMapping("/stop")
    public ResponseEntity<Void> stopJob(@RequestBody @Encrypt JobInfo jobInfo, HttpServletRequest request) {
        JobInfo info = jobInfoRepository.selectByPrimaryKey(jobInfo.getJobId());
        if (info != null) {
            String jwtToken = request.getHeader("jwt_token");
            jobInfoService.stopClient(info, jwtToken);
            jobService.removeJob(info.getJobId(), info.getExecutorId(), info.getTenantId());
            // 任务状态为执行中的日志改为失败
            jobLogRepository.updateLogByJobId(jobInfo.getJobId(), new Date());
        }
        return Results.success();
    }
}
