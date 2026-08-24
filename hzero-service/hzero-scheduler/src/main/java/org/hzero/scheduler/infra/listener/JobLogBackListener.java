package org.hzero.scheduler.infra.listener;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.redis.handler.IBatchQueueHandler;
import org.hzero.core.redis.handler.QueueHandler;
import org.hzero.scheduler.domain.service.IJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 客户端redis通道消息监听
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/24 10:49
 */
@QueueHandler(BootSchedulerConstant.QUEUE_NAME)
public class JobLogBackListener implements IBatchQueueHandler {

    private static final Logger logger = LoggerFactory.getLogger(JobLogBackListener.class);

    private final IJobService jobService;
    private final ObjectMapper objectMapper;

    @Autowired
    public JobLogBackListener(IJobService jobService,
                              ObjectMapper objectMapper) {
        this.jobService = jobService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void process(List<String> messages) {
        for (String message : messages) {
            JobLogDTO jobLogDTO;
            try {
                jobLogDTO = objectMapper.readValue(message, JobLogDTO.class);
            } catch (Exception e) {
                logger.error("Json format error！data : {}", message);
                continue;
            }
            jobService.callback(jobLogDTO);
        }
    }
}