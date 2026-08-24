package org.hzero.admin.app.service.metric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/11 10:05 上午
 */
public class MetricSyncTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricSyncTimer.class);

    private final MetricSyncProperties properties;
    private final MetricSyncService metricSyncService;
    private final ScheduledThreadPoolExecutor scheduler;

    public MetricSyncTimer(MetricSyncProperties properties,
                           MetricSyncService metricSyncService) {
        this.properties = properties;
        this.metricSyncService = metricSyncService;
        scheduler = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "Metric-Sync-Timer"));
    }

    public void start(){
        long period = properties.getSyncInterval();
        scheduler.scheduleAtFixedRate(() -> {
                    try {
                        metricSyncService.sync();
                    }catch (Throwable e) {
                        LOGGER.error("metric sync task execute failed.", e);
                    }
                },
                period, period, TimeUnit.MILLISECONDS);
    }

}
