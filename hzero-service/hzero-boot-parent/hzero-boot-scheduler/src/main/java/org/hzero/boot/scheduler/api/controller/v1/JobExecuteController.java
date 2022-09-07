package org.hzero.boot.scheduler.api.controller.v1;

import org.hzero.boot.scheduler.api.dto.JobDataDTO;
import org.hzero.boot.scheduler.app.service.JobExecuteService;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import io.choerodon.swagger.annotation.Permission;

/**
 * 客户端job执行接口
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 14:10
 */
@RestController
public class JobExecuteController {

    @Autowired
    private JobExecuteService jobExecuteService;

    @Permission(permissionWithin = true)
    @PostMapping(BootSchedulerConstant.EXECUTOR_PATH)
    public ResponseEntity<Void> runJob(@RequestBody JobDataDTO jobDataDTO) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
        SecurityContext context = SecurityContextHolder.getContext();
        jobExecuteService.jobExecute(jobDataDTO, context);
        return Results.success();
    }

    @Permission(permissionWithin = true)
    @PostMapping(BootSchedulerConstant.STOP_JOB_PATH)
    public ResponseEntity<String> stopJob(@RequestBody JobDataDTO jobDataDTO) {
        return Results.success(jobExecuteService.stopJob(jobDataDTO.getJobId()));
    }

    @Permission(permissionWithin = true)
    @PostMapping(BootSchedulerConstant.PAUSE_JOB_PATH)
    public ResponseEntity<String> pauseJob(@RequestBody JobDataDTO jobDataDTO) {
        return Results.success(jobExecuteService.pauseJob(jobDataDTO.getJobId()));
    }
}
