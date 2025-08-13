package org.hzero.scheduler;

import org.hzero.autoconfigure.scheduler.EnableHZeroScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * HZERO 调度服务
 */
@EnableHZeroScheduler
@EnableDiscoveryClient
@SpringBootApplication
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
