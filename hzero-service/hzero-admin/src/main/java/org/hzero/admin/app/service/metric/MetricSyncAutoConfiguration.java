package org.hzero.admin.app.service.metric;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/11 10:08 上午
 */
@ConditionalOnProperty(name = MetricSyncProperties.PREFIX + ".enable", havingValue = "true")
@EnableConfigurationProperties(MetricSyncProperties.class)
public class MetricSyncAutoConfiguration {

    @Bean
    public MetricSyncTimer metricSyncTimer(MetricSyncProperties properties, MetricSyncService metricSyncService){
        MetricSyncTimer timer = new MetricSyncTimer(properties, metricSyncService);
        timer.start();
        return timer;
    }

}
