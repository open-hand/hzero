package org.hzero.boot.scheduler.infra.init;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.configure.SchedulerConfig;
import org.hzero.boot.scheduler.infra.annotation.JobHandler;
import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;
import org.hzero.boot.scheduler.infra.feign.SchedulerFeignClient;
import org.hzero.boot.scheduler.infra.handler.IJobHandler;
import org.hzero.boot.scheduler.infra.registry.JobRegistry;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/23 17:13
 */
@Component
@Order(1)
public class ExecutorInit implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorInit.class);

    private final SchedulerConfig schedulerConfig;
    private final SchedulerFeignClient feignClient;

    @Value("${spring.application.name}")
    private String serverName;

    @Autowired
    public ExecutorInit(SchedulerConfig schedulerConfig,
                        SchedulerFeignClient feignClient) {
        this.schedulerConfig = schedulerConfig;
        this.feignClient = feignClient;
    }

    @Override
    public void run(String... args) throws Exception {
        // 扫描JobHandler注解
        scanJobHandler();

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("scheduler-register-runner-%d").build();
        ExecutorService executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        executor.execute(new Registry());
    }

    class Registry implements Runnable {

        @Override
        public void run() {
            // 检查执行器编码
            if (StringUtils.isBlank(schedulerConfig.getExecutorCode())) {
                logger.error("Can not find executorCode, please specify the executor code or turn off auto registration!");
                return;
            }
            if (StringUtils.isBlank(serverName)) {
                logger.error("Failed to obtain service name, unable to register automatically!");
                return;
            }
            if (schedulerConfig.isAutoRegister()) {
                int time = 1;
                while (true) {
                    if (time > schedulerConfig.getRetryTime()) {
                        logger.warn("Executor auto register has been retried {} times,  all failed!", schedulerConfig.getRetryTime());
                        return;
                    }
                    time++;
                    // 请求调度服务进行注册
                    String result = feignClient.refreshExecutor(BaseConstants.DEFAULT_TENANT_ID, schedulerConfig.getExecutorCode(), serverName).getBody();
                    if (Objects.equals(result, BootSchedulerConstant.Response.SUCCESS)) {
                        return;
                    }
                    // 等待一定时间再次重试
                    logger.warn("Executor failed to register automatically, try again in {} seconds", schedulerConfig.getRetry());
                    try {
                        TimeUnit.SECONDS.sleep(schedulerConfig.getRetry());
                    } catch (Exception e) {
                        logger.error("Actuator registration encountered an error");
                        return;
                    }
                }
            }
        }
    }

    /**
     * 启动后扫描JobHandler注解
     */
    private void scanJobHandler() {
        Map<String, Object> map = ApplicationContextHelper.getContext().getBeansWithAnnotation(JobHandler.class);
        for (Object service : map.values()) {
            if (service instanceof IJobHandler) {
                JobHandler jobHandler = ProxyUtils.getUserClass(service).getAnnotation(JobHandler.class);
                if (ObjectUtils.isEmpty(jobHandler)) {
                    logger.debug("could not get target bean , jobHandler : {}", service);
                } else {
                    JobRegistry.addJobHandler(jobHandler.value(), service);
                }
            }
        }
    }
}
