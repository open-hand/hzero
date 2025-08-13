package org.hzero.report.infra.feign;

import org.hzero.common.HZeroService;
import org.hzero.report.api.dto.ConcurrentRequest;
import org.hzero.report.infra.feign.fallback.SchedulerRemoteServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 调度服务远程调用
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/04 16:13
 */
@FeignClient(value = HZeroService.Scheduler.NAME, path = "/v1", fallback = SchedulerRemoteServiceFallback.class)
public interface SchedulerRemoteService {

    /**
     * 提交并发请求
     *
     * @param organizationId    租户ID
     * @param concurrentRequest 并发请求
     * @return 响应
     */
    @PostMapping("/{organizationId}/concurrent-requests/code")
    ResponseEntity<String> createRequest(@PathVariable("organizationId") Long organizationId,
                                         @RequestBody ConcurrentRequest concurrentRequest);
}
