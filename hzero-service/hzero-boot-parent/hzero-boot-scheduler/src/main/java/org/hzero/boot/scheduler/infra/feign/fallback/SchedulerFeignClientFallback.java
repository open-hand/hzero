package org.hzero.boot.scheduler.infra.feign.fallback;

import org.hzero.boot.scheduler.api.dto.ChildJobDTO;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.feign.SchedulerFeignClient;
import org.hzero.core.util.Results;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 调度服务feign调用失败回调
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/14 10:24
 */
@Component
public class SchedulerFeignClientFallback implements SchedulerFeignClient {

    @Override
    public ResponseEntity<String> createChildJob(Long organizationId, ChildJobDTO childJobDTO) {
        return Results.invalid();
    }

    @Override
    public ResponseEntity<String> refreshExecutor(Long organizationId, String executorCode, String serverName) {
        return Results.invalid();
    }

    @Override
    public ResponseEntity<String> callback(Long organizationId, JobLogDTO jobLogDTO) {
        return Results.success(BootSchedulerConstant.Response.FAILURE);
    }
}
