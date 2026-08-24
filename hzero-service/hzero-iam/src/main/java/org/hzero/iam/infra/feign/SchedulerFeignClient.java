package org.hzero.iam.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.iam.api.dto.JobInfoDTO;
import org.hzero.iam.infra.feign.fallback.SchedulerFeignFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 调度服务调度任务feign调用
 *
 * @author yuqing.zhang@hand-china.com 2020/05/11 13:55
 */
@FeignClient(value = HZeroService.Scheduler.NAME, path = "v1", fallback = SchedulerFeignFallbackImpl.class)
public interface SchedulerFeignClient {

    /**
     * 查询调度任务
     *
     * @param organizationId 租户ID
     * @param jobId 调度任务ID
     * @return JobInfoDTO
     */
    @GetMapping("/{organizationId}/job-info/{jobId}")
    ResponseEntity<String> detailJob(@PathVariable("organizationId") Long organizationId, @PathVariable("jobId") Long jobId);

    /**
     * 更新调度任务
     *
     * @param organizationId 租户ID
     * @param jobInfo 调度任务
     * @return JobInfoDTO
     */
    @PutMapping("/{organizationId}/job-info")
    ResponseEntity<String> updateJob(@PathVariable("organizationId") Long organizationId, @RequestBody JobInfoDTO jobInfo);

    /**
     * 暂停调度任务
     *
     * @param organizationId 租户ID
     * @param jobInfo 调度任务
     * @return Void
     */
    @PostMapping("/{organizationId}/job-info/pause")
    ResponseEntity<Void> pauseJob(@PathVariable("organizationId") Long organizationId, @RequestBody JobInfoDTO jobInfo);

    /**
     * 恢复调度任务
     *
     * @param organizationId 租户ID
     * @param jobInfo 调度任务
     * @return Void
     */
    @PostMapping("/{organizationId}/job-info/resume")
    ResponseEntity<Void> resumeJob(@PathVariable("organizationId") Long organizationId, @RequestBody JobInfoDTO jobInfo);

    /**
     * 创建调度任务
     *
     * @param organizationId 租户ID
     * @param jobInfo 调度任务
     * @return JobInfoDTO
     */
    @PostMapping("/{organizationId}/job-info")
    ResponseEntity<String> createJob(@PathVariable("organizationId") Long organizationId, @RequestBody JobInfoDTO jobInfo);

    /**
     * 查询执行器
     * 
     * @param organizationId 租户ID
     * @param executorCode 执行器编码
     * @return Page<ExecutorDTO>
     */
    @GetMapping("/{organizationId}/executors")
    ResponseEntity<String> pageExecutor(@PathVariable("organizationId") Long organizationId, @RequestParam("executorCode") String executorCode);
}
