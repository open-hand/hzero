package org.hzero.core.hystrix;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Reno Chou
 * Hystrix does not provide request attributes yet. Therefor a custom
 * {@link HystrixConcurrencyStrategy} is necessary that injects request attributes. This
 * enables the use of request and response inside hystrix fallbacks.
 * <p>
 * Hystrix 的默认隔离策略是<strong>THREAD</strong>，线程级别的隔离导致在Feign拦截器中无法获取线程变量
 * 解决方法1：调整Hystrix隔离级别为信号量级别（Hystrix官方强烈建议使用THREAD，使用信号量时熔断策略失效）
 * <code>hystrix.command.default.execution.isolation.strategy: SEMAPHORE</code>
 * 解决方法2：自定义并发策略，代码如下
 * 参考博客：http://www.itmuch.com/spring-cloud-sum/hystrix-threadlocal/
 * </p>
 */
public class RequestAttributeHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private static final Log log = LogFactory
            .getLog(RequestAttributeHystrixConcurrencyStrategy.class);

    private List<HystrixCallableWrapper> wrappedCallableSet = new ArrayList<>();

    private HystrixConcurrencyStrategy delegate;

    public RequestAttributeHystrixConcurrencyStrategy(List<HystrixCallableWrapper> wrappedCallableList) {
        if (!CollectionUtils.isEmpty(wrappedCallableList)) {
            wrappedCallableSet = wrappedCallableList.stream()
                    .sorted(Comparator.comparingInt(HystrixCallableWrapper::order).reversed())
                    .collect(Collectors.toList());
        }
        try {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof RequestAttributeHystrixConcurrencyStrategy) {
                return;
            }

            HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();
            HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher, propertiesStrategy);
            HystrixPlugins.reset();
            HystrixPlugins.getInstance().registerConcurrencyStrategy(this);
            HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
            HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
            HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
            HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
        } catch (Exception var7) {
            log.error("Failed to register Request Attribute Hystrix Concurrency Strategy", var7);
        }
    }

    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher,
                                                 HystrixPropertiesStrategy propertiesStrategy) {
        if (log.isDebugEnabled()) {
            log.debug("Current Hystrix plugins configuration is ["
                    + "concurrencyStrategy [" + this.delegate + "]," + "eventNotifier ["
                    + eventNotifier + "]," + "metricPublisher [" + metricsPublisher + "],"
                    + "propertiesStrategy [" + propertiesStrategy + "]," + "]");
            log.debug("Registering Sleuth Hystrix Concurrency Strategy.");
        }
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        for (HystrixCallableWrapper hystrixCallableWrapper : wrappedCallableSet) {
            if (hystrixCallableWrapper.shouldWrap()) {
                callable = hystrixCallableWrapper.wrapCallable(callable);
            }
        }
        return this.delegate.wrapCallable(callable);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixThreadPoolProperties threadPoolProperties) {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return this.delegate.getRequestVariable(rv);
    }
}