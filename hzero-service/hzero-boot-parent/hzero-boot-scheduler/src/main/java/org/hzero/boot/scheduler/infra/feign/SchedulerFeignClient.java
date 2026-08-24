package org.hzero.boot.scheduler.infra.feign;

import org.hzero.boot.scheduler.api.dto.ChildJobDTO;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.infra.feign.fallback.SchedulerFeignClientFallback;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 调度服务feign调用
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 10:23
 */
@FeignClient(value = HZeroService.Scheduler.NAME, path = "/v1", fallback = SchedulerFeignClientFallback.class)
public interface SchedulerFeignClient {

    /**
     * 新建子任务
     *
     * @param organizationId 租户Id
     * @param childJobDTO    子任务
     * @return response
     */
    @PostMapping("/{organizationId}/job-info/child-job")
    ResponseEntity<String> createChildJob(@PathVariable("organizationId") Long organizationId,
                                          @RequestBody ChildJobDTO childJobDTO);

    /**
     * 刷新执行器状态
     *
     * @param organizationId 租户Id
     * @param executorCode   执行器编码
     * @param serverName     服务名称
     * @return 创建结果
     */
    @PostMapping("/{organizationId}/executors/refresh")
    ResponseEntity<String> refreshExecutor(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("executorCode") String executorCode,
                                           @RequestParam("serverName") String serverName);

    /**
     * 客户端日志回写
     *
     * @param organizationId 租户Id
     * @param jobLogDTO      任务日志
     * @return 回写结果
     */
    @PostMapping("/{organizationId}/job-logs/callback")
    ResponseEntity<String> callback(@PathVariable("organizationId") Long organizationId,
                                    @RequestBody JobLogDTO jobLogDTO);
}
