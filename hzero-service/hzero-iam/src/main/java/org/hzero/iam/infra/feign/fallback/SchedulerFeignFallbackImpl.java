package org.hzero.iam.infra.feign.fallback;

import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.JobInfoDTO;
import org.hzero.iam.infra.feign.SchedulerFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * 调度服务调度任务失败回调
 *
 * @author yuqing.zhang@hand-china.com 2020/05/11 13:56
 */
@Component
public class SchedulerFeignFallbackImpl implements SchedulerFeignClient {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerFeignFallbackImpl.class);

    @Override
    public ResponseEntity<String> detailJob(Long organizationId, Long jobId) {
        logger.info("Error detailJob {}", jobId);
        return Results.invalid();
    }

    @Override
    public ResponseEntity<String> updateJob(Long organizationId, JobInfoDTO jobInfo) {
        logger.info("Error updateJob {}", jobInfo);
        return Results.invalid();
    }

    @Override
    public ResponseEntity<Void> pauseJob(Long organizationId, JobInfoDTO jobInfo) {
        logger.info("Error pauseJob {}", jobInfo);
        return Results.invalid();
    }

    @Override
    public ResponseEntity<Void> resumeJob(Long organizationId, JobInfoDTO jobInfo) {
        logger.info("Error resumeJob {}", jobInfo);
        return Results.invalid();
    }

    @Override
    public ResponseEntity<String> createJob(Long organizationId, JobInfoDTO jobInfo) {
        logger.info("Error createJob {}", jobInfo);
        return Results.invalid();
    }

    @Override
    public ResponseEntity<String> pageExecutor(Long organizationId, String executorCode) {
        logger.info("Error pageExecutor {}", executorCode);
        return Results.invalid();
    }
}
