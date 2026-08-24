package io.choerodon.mybatis.helper.snowflake;

import org.springframework.context.ApplicationContext;

import java.util.Random;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class NoneSnowflakeMetaProvider implements SnowflakeMetaProvider {
    private final int maxDataCenter;
    private final int maxWorker;

    public NoneSnowflakeMetaProvider() {
        this(BIT_DATA_CENTER, BIT_WORKER);
    }

    public NoneSnowflakeMetaProvider(Integer bitDataCenterId,
                                     Integer bitWorkerId) {
        this.maxDataCenter = Math.toIntExact(bitDataCenterId != null ? ~(-1L << bitDataCenterId) : MAX_DATA_CENTER);
        this.maxWorker = Math.toIntExact(bitWorkerId != null ? ~(-1L << bitWorkerId) : MAX_WORKER);
    }

    /**
     * @param context 应用程序上下文
     * @return random[0, maxDataCenter)
     */
    @Override
    public long dataCenterId(ApplicationContext context) {
        return new Random().nextInt(maxDataCenter);
    }

    /**
     * @param context 应用程序上下文
     * @return Math.abs(当前服务名.hashCode &gt;&gt; 1) % maxWorker
     */
    @Override
    public long workerId(ApplicationContext context) {
        String serviceName = null;
        if (context != null) {
            serviceName = context.getEnvironment().getProperty("spring.application.name");
        }
        return serviceName != null ? (Math.abs(serviceName.hashCode() >> 1) % maxWorker) : 0L;
    }
}
