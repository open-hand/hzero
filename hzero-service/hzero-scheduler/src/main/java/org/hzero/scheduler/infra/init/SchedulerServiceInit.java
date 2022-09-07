package org.hzero.scheduler.infra.init;

import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.core.redis.RedisQueueHelper;
import org.hzero.scheduler.app.service.JobInfoService;
import org.hzero.scheduler.domain.repository.JobLogRepository;
import org.hzero.scheduler.infra.listener.JobLogBackListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 执行器注册  程序启动后自动执行
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 19:58
 */
@Component
public class SchedulerServiceInit implements CommandLineRunner {

    private final JobInfoService jobInfoService;
    private final RedisQueueHelper redisQueueHelper;
    private final JobLogBackListener logBackListener;
    private final JobLogRepository jobLogRepository;

    @Autowired
    public SchedulerServiceInit(JobInfoService jobInfoService,
                                RedisQueueHelper redisQueueHelper,
                                JobLogBackListener logBackListener,
                                JobLogRepository jobLogRepository) {
        this.jobInfoService = jobInfoService;
        this.redisQueueHelper = redisQueueHelper;
        this.logBackListener = logBackListener;
        this.jobLogRepository = jobLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 消费队列中的任务日志
        logBackListener.process(redisQueueHelper.pullAll(BootSchedulerConstant.QUEUE_NAME));

        // 清理异常调度日志
        jobLogRepository.updateErrorLog();

        // 初始化任务
        jobInfoService.initJob();
    }
}
