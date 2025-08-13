package org.hzero.boot.scheduler.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.scheduler.api.dto.JobLogDTO;
import org.hzero.boot.scheduler.app.service.JobLogBackService;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.feign.SchedulerFeignClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisQueueHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;

/**
 * 日志应用服务实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/24 10:10
 */
@Service
public class JobLogBackServiceImpl implements JobLogBackService {

    private final ObjectMapper objectMapper;
    private final RedisQueueHelper queueHelper;
    private final SchedulerFeignClient feignClient;

    @Autowired
    public JobLogBackServiceImpl(ObjectMapper objectMapper,
                                 RedisQueueHelper queueHelper,
                                 SchedulerFeignClient feignClient) {
        this.objectMapper = objectMapper;
        this.queueHelper = queueHelper;
        this.feignClient = feignClient;
    }

    @Override
    public void updateLog(JobLogDTO jobLogDTO) {
        // 直接请求服务端回写日志，若回写失败，使用redis消息队列
        ResponseEntity<String> response = feignClient.callback(BaseConstants.DEFAULT_TENANT_ID, jobLogDTO);
        String result = response.getBody();
        if (!BootSchedulerConstant.Response.SUCCESS.equals(result)) {
            try {
                queueHelper.push(BootSchedulerConstant.QUEUE_NAME, objectMapper.writeValueAsString(jobLogDTO));
            } catch (JsonProcessingException e) {
                throw new CommonException(e);
            }
        }
    }
}
