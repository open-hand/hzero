package org.hzero.export;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hzero.core.endpoint.request.EndpointKey;
import org.hzero.core.exception.ServiceStartException;
import org.hzero.core.metadata.MetadataEntry;
import org.hzero.core.redis.RedisHelper;
import org.hzero.export.config.ExportInitializeConfig;
import org.hzero.export.endpoint.AsyncExportEndpoint;
import org.hzero.export.filler.MultiSheetFiller;
import org.hzero.export.filler.SingleSheetFiller;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Auto Configuration
 *
 * @author bojiangzhou 2018/07/26
 */
@Configuration
@EnableConfigurationProperties(ExportProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ExportAutoConfiguration {

    @Bean
    public ExportColumnHelper exportColumnHelper(ExportProperties properties, RedisHelper redisHelper) {
        /* 如果设置默认异步请求，又没有开启异步配置，直接抛异常，提示用户重新配置。 */
        if (ExportProperties.ASYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())
                && !properties.getEnableAsync()) {
            throw new ServiceStartException("export.config.properties_conflict",
                    "Excel导出配置冲突，只有开启异步配置后，才能设置默认请求为异步模式！",
                    "defaultRequestMode=ASYNC&&enableAsync=false");
        }
        return new ExportColumnHelper(properties, redisHelper);
    }

    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public ExportFutureManager futureManager() {
        return new ExportFutureManager();
    }

    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public AsyncExportEndpoint asyncExportEndpoint() {
        return new AsyncExportEndpoint();
    }

    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public MetadataEntry asyncExportMetadataEntry() {
        return new MetadataEntry(EndpointKey.ASYNC_EXPORT_ENDPOINT_KEY.getKey(), "/async-export-endpoint");
    }

    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public ExportDataHelper asyncExportDataHelper(ExportProperties properties,
                                                  ExportFutureManager futureManager,
                                                  ExportColumnHelper exportColumnHelper) {
        ExecutorService executorService = new ThreadPoolExecutor(properties.getCorePoolSize(), properties.getMaximumPoolSize(),
                properties.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS,
                properties.getQueueSize() == null ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(properties.getQueueSize()),
                new ThreadFactoryBuilder().setNameFormat(properties.getAsyncThreadName() + "-%d").build());
        return new ExportDataHelper(exportColumnHelper, executorService, futureManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExportDataHelper exportDataHelper(ExportColumnHelper exportColumnHelper) {
        return new ExportDataHelper(exportColumnHelper);
    }

    @Bean
    public ExcelExportAop excelExportAop(ExportDataHelper exportDataHelper, ExportColumnHelper exportColumnHelper) {
        return new ExcelExportAop(exportDataHelper, exportColumnHelper);
    }

    @Bean
    public ExportInitializeConfig exportInitializeConfig() {
        return new ExportInitializeConfig();
    }

    @Bean
    public MultiSheetFiller multiSheetFiller() {
        return new MultiSheetFiller();
    }

    @Bean
    public SingleSheetFiller singleSheetFiller() {
        return new SingleSheetFiller();
    }

    @Bean(initMethod = "init")
    public ExcelFillerHolder excelFillerHolder() {
        return new ExcelFillerHolder();
    }
}
