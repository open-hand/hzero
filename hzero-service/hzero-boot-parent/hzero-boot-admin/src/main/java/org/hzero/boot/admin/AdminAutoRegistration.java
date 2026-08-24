package org.hzero.boot.admin;

import org.hzero.boot.admin.exception.ServiceRegisterException;
import org.hzero.boot.admin.exception.ServiceUnregisterException;
import org.hzero.boot.admin.exception.TransportException;
import org.hzero.boot.admin.registration.AutoRegistration;
import org.hzero.boot.admin.registration.Registration;
import org.hzero.boot.admin.transport.AdminClientProperties;
import org.hzero.boot.admin.transport.Transport;
import org.hzero.common.HZeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 5:34 下午
 */
public class AdminAutoRegistration implements AutoRegistration, SmartLifecycle, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAutoRegistration.class);

    private final AtomicBoolean running = new AtomicBoolean(false);

    private Transport registerTransport;
    private Transport unregisterTransport;
    private Registration registration;
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>() {
                @Override
                public boolean offer(Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
                    return false;
                }
                @Override
                public boolean offer(Runnable runnable) {
                    return false;
                }
            },
            runnable -> new Thread(runnable, "Admin-Auto-Registration-Executor"),
            new ThreadPoolExecutor.DiscardPolicy());

    private ApplicationContext context;

    private AdminClientProperties properties;

    public AdminAutoRegistration(Transport registerTransport,
                                 Transport unregisterTransport,
                                 Registration registration,
                                 AdminClientProperties properties) {
        this.registerTransport = registerTransport;
        this.unregisterTransport = unregisterTransport;
        this.registration = registration;
        this.properties = properties;
    }

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
        if (running.get() || running.compareAndSet(false, true)) {
            register(registration);
        }
    }

    @Override
    public void stop() {
        if (running.compareAndSet(true, false)) {
            // 不再注销，让admin服务定期清理无用实例
            //unregister(registration);
            if (executor != null) {
                executor.shutdownNow();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    private void retryAlwaysUntilSuccess(Supplier<Boolean> supplier, String operation) {
        executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(properties.getRetryInternal());
                    if (supplier.get()) {
                        return;
                    }
                } catch (ServiceRegisterException | InterruptedException e) {
                    //ignore auto-end
                    LOGGER.error("============= retry to [" + operation + "] failed", e);
                }
            }
        });
    }

    @Override
    public void register(Registration registration) {
        try {
            registerTransport.transport(registration);
        } catch (TransportException e){
            String message = "Register to [" + HZeroService.getRealName(HZeroService.Admin.NAME) + "] server failed!";
            LOGGER.error(message + " retry to execute until success...");
            retryAlwaysUntilSuccess(() -> {
                try {
                    registerTransport.transport(registration);
                } catch (TransportException ex) {
                    throw new ServiceRegisterException(message, ex);
                }
                return true;
            }, "register");
        }
    }

    @Override
    public void unregister(Registration registration) {
        try {
            unregisterTransport.transport(registration);
        } catch (TransportException e){
            String message = "Unregister from [" + HZeroService.getRealName(HZeroService.Admin.NAME) + "] server failed!";
            throw new ServiceUnregisterException(message, e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @EventListener(WebServerInitializedEvent.class)
    public void onApplicationEvent(WebServerInitializedEvent event) {
        //服务可提供web服务后，第一时间通知治理服务
        start();
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationEvent(ContextClosedEvent event) {
        if(event.getApplicationContext() == context) {
            stop();
        }
    }

}
