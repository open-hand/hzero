package org.hzero.admin.app.service.impl;

import org.hzero.admin.api.dto.ServiceRegistryResponse;
import org.hzero.admin.app.service.ServiceInitRegistry;
import org.hzero.admin.config.ServiceInitRegistryProperties;
import org.hzero.admin.domain.repository.ServiceInitRegistryRepository;
import org.hzero.admin.domain.vo.InitChainContext;
import org.hzero.admin.domain.vo.Service;
import org.hzero.admin.infra.chain.InitChain;
import org.hzero.admin.infra.factory.InitChainFactoryBean;
import org.hzero.admin.infra.util.SetUtils;
import org.hzero.core.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.SmartLifecycle;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:36 上午
 */
@org.springframework.stereotype.Service
public class RestServiceInitRegistry implements ServiceInitRegistry, SmartLifecycle {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceInitRegistry.class);
    private static final String STATUS = "status";

    private final AtomicBoolean running = new AtomicBoolean(false);

    @Autowired
    private ServiceInitRegistryRepository serviceInitRegistryRepository;
    @Autowired
    private InitChainFactoryBean chainFactoryBean;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ServiceInitRegistryProperties properties;
    @Qualifier("serviceInitRegistryRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    private ThreadLocal<EvaluationContext> contextThreadLocal = ThreadLocal.withInitial(StandardEvaluationContext::new);
    private ThreadLocal<ExpressionParser> parserThreadLocal = ThreadLocal.withInitial(SpelExpressionParser::new);

    private final Lock lock = new ReentrantLock();
    private final Condition empty = lock.newCondition();
    private ThreadPoolExecutor initExecutor;
    private ThreadPoolExecutor checkExecutor;
    private Set<Service> cleanServices = new HashSet<>();

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            doStart();
            LOGGER.info(this.getClass().getName() + "start success!");
        }
        LOGGER.error(this.getClass().getName() + "is running!");
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            doStop();
            LOGGER.info(this.getClass().getName() + "stop success!");
        }
        LOGGER.error(this.getClass().getName() + "has stopped!");
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    private void doStart() {
        this.initExecutor = new ThreadPoolExecutor(1, 1,
                0, NANOSECONDS, new LinkedBlockingQueue<>(),
                runnable -> new Thread(runnable, "Service-Init-Scheduler"));
        this.checkExecutor = new ThreadPoolExecutor(1, 1,
                0, NANOSECONDS, new LinkedBlockingQueue<>(),
                runnable -> new Thread(runnable, "Service-Init-Checker"));
        this.initExecutor.execute(() -> {
            //可能存在之前未处理的服务，尝试初始化一次
            // never throw ex
            init(getUnInitializedServices());
            while (!Thread.currentThread().isInterrupted()) {
                lock.lock();
                try {
                    if (init(getUnInitializedServices())) {
                        empty.await();
                    } else {
                        empty.await(5, SECONDS);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Thread[" + Thread.currentThread().getName() + "] is interrupted", e);
                } finally {
                    lock.unlock();
                }
            }
        });
        this.checkExecutor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                lock.lock();
                try {
                    //清理已下线的过期服务
                    Set<Service> toClean = getAndCacheCleanServices(discoveryClient);
                    for (Service service : toClean) {
                        unregister(service);
                    }

                    //如果存在未刷新服务，唤醒工作线程
                    if (!getUnInitializedServices().isEmpty()) {
                        empty.signal();
                    }
                } finally {
                    lock.unlock();
                }
                try {
                    //类似于重试间隔时间
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread[" + Thread.currentThread().getName() + "] is interrupted", e);
                }
            }
        });
    }

    /**
     * 清除两次检测都不在线的服务
     *
     * @param discoveryClient
     * @return
     */
    private Set<Service> getAndCacheCleanServices(DiscoveryClient discoveryClient) {
        Set<Service> services = getServices();
        Set<Service> cleanServices = new HashSet<>();
        for (Service service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service.getServiceName());
            boolean up = false;
            for (ServiceInstance instance : instances) {
                String version = ServiceInstanceUtils.getVersionFromMetadata(instance);
                if (version.equals(service.getVersion())) {
                    up = true;
                    break;
                }
            }
            if (!up) {
                cleanServices.add(service);
            }
        }
        Set<Service> toClean = SetUtils.getIntersection(this.cleanServices, cleanServices);
        this.cleanServices = cleanServices;
        return toClean;
    }

    private void doStop() {
        if (this.initExecutor != null) {
            this.initExecutor.shutdown();
        }
        if (this.checkExecutor != null) {
            this.checkExecutor.shutdown();
        }
    }

    @Override
    public ServiceRegistryResponse register(Service service) {
        try {
            assertHealth(service);
            serviceInitRegistryRepository.add(service);
        } catch (Throwable e) {
            LOGGER.error("service register error, cause: {}", e.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("register ex: ", e);
            }
            return ServiceRegistryResponse
                    .create().setSuccess(false).setMessage("service register error, cause: " + e.getMessage());
        }
        if (lock.tryLock()) {
            try {
                empty.signal();
            } finally {
                lock.unlock();
            }
        }
        return ServiceRegistryResponse
                .create().setSuccess(true).setMessage("service register success");
    }

    private void assertHealth(Service service) throws IOException {
        if (!isHealth(service)) {
            throw new ConnectException("Service[serviceName=" +
                    service.getServiceName() + ",version=" +
                    service.getVersion() + ",healthUrl=" +
                    service.getHealthUrl() + "] health check error!");
        }
    }

    private boolean isHealth(Service service) {
        String healthUrl = service.getHealthUrl();
        if (StringUtils.isEmpty(healthUrl)) {
            return false;
        }
        ResponseEntity<Map> responseEntity = restTemplate.exchange(healthUrl, HttpMethod.GET, null, Map.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            return false;
        }
        Map map = responseEntity.getBody();
        return map != null && Status.UP.getCode().equals(String.valueOf(map.get(STATUS)));
    }

    @Override
    public ServiceRegistryResponse unregister(Service service) {
        try {
            serviceInitRegistryRepository.remove(service);
        } catch (Throwable e) {
            LOGGER.error("service unregister error, cause: {}", e.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("unregister ex: ", e);
            }
            ServiceRegistryResponse
                    .create().setSuccess(false).setMessage("service unregister error, cause: " + e.getMessage());
        }
        return ServiceRegistryResponse
                .create().setSuccess(true).setMessage("service unregister success");
    }

    @Override
    public Set<Service> getServices() {
        return serviceInitRegistryRepository.get();
    }

    @Override
    public Set<Service> getInitializedServices() {
        return getServices().stream()
                .filter(Service::getInitialized)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Service> getUnInitializedServices() {
        return getServices().stream()
                .filter(service -> !service.getInitialized())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean doInit(Service service) throws RuntimeException {
        try {
            executeInit(service);
        } catch (Throwable e) {
            throw new RuntimeException("service [" + service.getServiceName() + ":" + service.getVersion() + "] doInit() failed, cause: " + e.getMessage(), e);
        }
        return true;
    }

    @Override
    public boolean shouldSkip(Service service) {
        if (StringUtils.isEmpty(properties.getSkipExpression())) {
            return false;
        }
        EvaluationContext context = this.contextThreadLocal.get();
        ExpressionParser parser = this.parserThreadLocal.get();
        context.setVariable("service", service);
        Boolean b = parser.parseExpression(properties.getSkipExpression()).getValue(context, Boolean.class);
        return b == null ? false : b;
    }

    @Override
    public void skip(Service service) {
        service.setInitialized(true);
        setInitialized(service);
    }

    /**
     * 默认初始化链
     * 0.admin服务刷新路由
     * 1.通知iam服务刷新权限
     * 2.通知swagger服务刷新文档
     *
     * @param service
     */
    private void executeInit(Service service) {
        InitChainContext context = InitChainContext.Builder.create().service(service).build();
        buildInitChain().doChain(context);
    }

    private InitChain buildInitChain() {
        return chainFactoryBean.getObject();
    }

    @Override
    public void setInitialized(Service service) {
        serviceInitRegistryRepository.setInitialized(service);
    }

}
