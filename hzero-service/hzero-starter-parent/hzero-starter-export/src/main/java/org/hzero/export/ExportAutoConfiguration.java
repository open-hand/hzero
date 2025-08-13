package org.hzero.export;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import org.hzero.core.endpoint.request.EndpointKey;
import org.hzero.core.exception.ServiceStartException;
import org.hzero.core.metadata.MetadataEntry;
import org.hzero.core.redis.RedisHelper;
import org.hzero.export.config.ExportInitializeConfig;
import org.hzero.export.endpoint.AsyncExportEndpoint;
import org.hzero.export.feign.ExportHimpRemoteService;
import org.hzero.export.feign.fallback.ExportHimpRemoteServiceImpl;
import org.hzero.export.filler.csv.MultiCsvFiller;
import org.hzero.export.filler.csv.SingleCsvFiller;
import org.hzero.export.filler.excel.MultiSheetFiller;
import org.hzero.export.filler.excel.SingleSheetFiller;

/**
 * Auto Configuration
 *
 * @author bojiangzhou 2018/07/26
 */
@Configuration
@EnableConfigurationProperties(ExportProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableFeignClients(basePackageClasses = ExportHimpRemoteService.class)
public class ExportAutoConfiguration {

    @Bean
    public ExportHimpRemoteService exportHimpRemoteService() {
        return new ExportHimpRemoteServiceImpl();
    }

    @Bean
    public ExportColumnHelper exportColumnHelper(ExportProperties properties, RedisHelper redisHelper,
                                                 ExportHimpRemoteService himpRemoteService) {
        /* 如果设置默认异步请求，又没有开启异步配置，直接抛异常，提示用户重新配置。 */
        if (ExportProperties.ASYNC_REQUEST_MODE.equals(properties.getDefaultRequestMode())
                && !properties.getEnableAsync()) {
            throw new ServiceStartException("export.config.properties_conflict",
                    "Excel导出配置冲突，只有开启异步配置后，才能设置默认请求为异步模式！",
                    "defaultRequestMode=ASYNC&&enableAsync=false");
        }
        return new ExportColumnHelper(properties, redisHelper, himpRemoteService);
    }

    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public ExportFutureManager futureManager() {
        return new ExportFutureManager();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.info.InfoContributor")
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
    public ThreadPoolTaskExecutor exportDataThreadPool(ExportProperties properties) {
        ThreadPoolTaskExecutor exportDataThreadPool = new ThreadPoolTaskExecutor();
        exportDataThreadPool.setCorePoolSize(properties.getCorePoolSize());
        exportDataThreadPool.setMaxPoolSize(properties.getMaximumPoolSize());
        if (properties.getQueueSize() != null) {
            exportDataThreadPool.setQueueCapacity(properties.getQueueSize());
        }
        if (properties.getKeepAliveTime() != null) {
            exportDataThreadPool.setKeepAliveSeconds(Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(properties.getKeepAliveTime().toMillis())));
        }
        exportDataThreadPool.setThreadNamePrefix(properties.getAsyncThreadName() + "-%d");
        exportDataThreadPool.initialize();
        return exportDataThreadPool;
    }


    @Bean
    @ConditionalOnProperty(value = ExportProperties.PREFIX + ".enable-async", havingValue = "true")
    public ExportDataHelper asyncExportDataHelper(@Qualifier("exportDataThreadPool") ThreadPoolTaskExecutor exportDataThreadPool,
                                                  ExportFutureManager futureManager,
                                                  ExportColumnHelper exportColumnHelper) {
        return new ExportDataHelper(exportColumnHelper, exportDataThreadPool, futureManager);
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
    @ConditionalOnMissingBean
    public MultiSheetFiller multiSheetFiller() {
        return new MultiSheetFiller();
    }

    @Bean
    @ConditionalOnMissingBean
    public SingleSheetFiller singleSheetFiller() {
        return new SingleSheetFiller();
    }

    @Bean
    @ConditionalOnMissingBean
    public MultiCsvFiller multiCsvFiller() {
        return new MultiCsvFiller();
    }

    @Bean
    @ConditionalOnMissingBean
    public SingleCsvFiller singleCsvFiller() {
        return new SingleCsvFiller();
    }

    @Bean(initMethod = "init")
    public FillerHolder excelFillerHolder() {
        return new FillerHolder();
    }

}
