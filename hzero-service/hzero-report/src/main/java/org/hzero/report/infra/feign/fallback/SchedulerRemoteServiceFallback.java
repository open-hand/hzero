package org.hzero.report.infra.feign.fallback;

import org.hzero.core.util.Results;
import org.hzero.report.api.dto.ConcurrentRequest;
import org.hzero.report.infra.feign.SchedulerRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 调度服务远程调用
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/04 16:16
 */
@Component
public class SchedulerRemoteServiceFallback implements SchedulerRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerRemoteServiceFallback.class);

    @Override
    public ResponseEntity<String> createRequest(Long organizationId, ConcurrentRequest concurrentRequest) {
        logger.error("Create concurrent request failed.");
        return Results.invalid();
    }
}
