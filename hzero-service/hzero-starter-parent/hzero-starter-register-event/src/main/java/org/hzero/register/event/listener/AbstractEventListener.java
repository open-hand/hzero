package org.hzero.register.event.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import org.hzero.register.event.config.RegisterEventListenerProperties;
import org.hzero.register.event.event.InstanceAddedEvent;
import org.hzero.register.event.event.InstanceRemovedEvent;
import org.hzero.register.event.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 抽象事件监听器
 *
 * @author XCXCXCXCX
 */
public abstract class AbstractEventListener implements ApplicationEventPublisherAware{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEventListener.class);

    private DiscoveryClient discoveryClient;

    private RegisterEventListenerProperties properties;

    private ApplicationEventPublisher publisher;

    private ScheduledExecutorService scheduler;

    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public void setProperties(RegisterEventListenerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        start();
    }

    private void start(){
        scheduler = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "register-event-scheduler"));
        scheduler.scheduleAtFixedRate(new PollTask(), 0, properties.getPollInterval(), TimeUnit.SECONDS);
    }

    class PollTask implements Runnable{

        /**
         * 适配eureka
         */
        private CustomPropertyPreFilter filter = new CustomPropertyPreFilter("leaseInfo","lastDirtyTimestamp","lastUpdatedTimestamp");

        private String cacheMD5 = "";

        private Set<String> serviceCache = new HashSet<>();

        private Map<String, Set<String>> instanceMapCache = new HashMap<>();

        private int serviceDownCount = 0;
        private Map<String, AtomicInteger> instanceDownCountMap = new HashMap<>();

        @Override
        public void run() {
            Set<String> services = serviceCache;
            Map<String, Set<String>> instanceMap = instanceMapCache;
            String MD5 = cacheMD5;
            try {
                long start = System.currentTimeMillis();
                services = new HashSet<>(discoveryClient.getServices());
                if(services.isEmpty()){
                    serviceDownCount++;
                    if(serviceDownCount > properties.getMaxServiceDownTimes()){
                        publishServiceChangedEvent(serviceCache, services, instanceMap, new HashMap<>(0));
                        return;
                    }
                    return;
                }
                serviceDownCount = 0;
                final Map<String, ServiceInstance> serviceInstanceMap = new HashMap<>(16);
                instanceMap = services.stream()
                        .collect(
                                Collectors.toMap(
                                        service -> service,
                                        service ->{
                                            List<ServiceInstance> serviceInstances =
                                                    discoveryClient.getInstances(service);
                                            AtomicInteger count = instanceDownCountMap.computeIfAbsent(service, k -> new AtomicInteger(0));
                                            if(serviceInstances.isEmpty()){
                                                if(count.incrementAndGet() > properties.getMaxInstanceDownTimes()){
                                                    return Collections.emptySet();
                                                }
                                                Set<String> instances = instanceMapCache.get(service);
                                                return instances == null ? Collections.emptySet() : instances;
                                            }else{
                                                count.set(0);
                                                return serviceInstances.stream()
                                                        .map(serviceInstance -> {
                                                            String json = JSON.toJSONString(serviceInstance, filter);
                                                            serviceInstanceMap.put(json, serviceInstance);
                                                            return json;
                                                        }).collect(Collectors.toSet());
                                            }
                                        }
                                )
                        );
                MD5 = MD5Util.encrypt(instanceMap.toString());
                if(cacheMD5.equals(MD5)){
                    return;
                }
                publishServiceChangedEvent(serviceCache, services, instanceMap, serviceInstanceMap);
                long end2 = System.currentTimeMillis();
                LOGGER.debug("getList and publish event cost : " + (end2 - start) + "ms");
            }catch (Throwable e){
                LOGGER.error("pollTask throw exception :" , e);
            }finally {
                serviceCache = services;
                instanceMapCache = instanceMap;
                cacheMD5 = MD5;
            }
        }

        private void publishServiceChangedEvent(Set<String> oldServices, Set<String> newServices, Map<String, Set<String>> instanceMap, Map<String, ServiceInstance> serviceInstanceMap) {
            compareAndExecute(oldServices, newServices,
                    this::publishInstanceRemovedEvent,
                    this::publishInstanceAddedEvent);

            //比对相同服务中的节点变化情况
            for (String service : oldServices){
                Set<String> oldInstanceSet = instanceMapCache.get(service);
                Set<String> newInstanceSet = instanceMap.get(service);
                if(oldInstanceSet.equals(newInstanceSet)){
                    continue;
                }
                compareAndExecute(oldInstanceSet, newInstanceSet,
                        removedInstanceSet -> {
                            for(String serviceInstanceJson : removedInstanceSet){
                                try {
                                    publisher.publishEvent(new InstanceRemovedEvent(this, service, serviceInstanceMap.get(serviceInstanceJson)));
                                }catch (Throwable e){
                                    LOGGER.error("publishEvent error", e);
                                    //ignore
                                }
                            }
                        },
                        addedInstanceSet -> {
                            for(String serviceInstanceJson : addedInstanceSet){
                                try {
                                    publisher.publishEvent(new InstanceAddedEvent(this, service, serviceInstanceMap.get(serviceInstanceJson)));
                                }catch (Throwable e){
                                    LOGGER.error("publishEvent error", e);
                                    //ignore
                                }
                            }
                        });
            }
        }

        private void compareAndExecute(Set<String> set1, Set<String> set2, FunctionX f1, FunctionX f2){
            if(set1 == null){
                set1 = new HashSet<>();
            }
            if(set2 == null){
                set2 = new HashSet<>();
            }
            Set<String> oldTmp = new HashSet<>(set1);
            Set<String> newTmp = new HashSet<>(set2);
            oldTmp.removeAll(set2);
            newTmp.removeAll(set1);
            if(!oldTmp.isEmpty()){
                f1.apply(oldTmp);
            }
            if(!newTmp.isEmpty()){
                f2.apply(newTmp);
            }
        }

        private void publishInstanceAddedEvent(Set<String> addedServices) {
            for(String service : addedServices){
                List<ServiceInstance> serviceInstances = discoveryClient.getInstances(service);
                for(ServiceInstance serviceInstance : serviceInstances){
                    try {
                        publisher.publishEvent(new InstanceAddedEvent(this, service, serviceInstance));
                    }catch (Throwable e){
                        LOGGER.error("publishEvent error", e);
                        //ignore
                    }
                }
            }
        }

        private void publishInstanceRemovedEvent(Set<String> removedServices) {
            for(String service : removedServices){
                List<ServiceInstance> serviceInstances = discoveryClient.getInstances(service);
                for(ServiceInstance serviceInstance : serviceInstances){
                    try {
                        publisher.publishEvent(new InstanceRemovedEvent(this, service, serviceInstance));
                    }catch (Throwable e){
                        LOGGER.error("publishEvent error", e);
                        //ignore
                    }
                }
            }
        }
    }

    /**
     * 自定义属性拦截器，用于fastjson忽略特定的field
     */
    static class CustomPropertyPreFilter implements PropertyFilter{

        private String[] excludes;

        public CustomPropertyPreFilter(String... excludes) {
            this.excludes = excludes;
        }

        @Override
        public boolean apply(Object object, String name, Object value) {
            for(String exclude : excludes){
                if(exclude == null){
                    return true;
                }
                if(exclude.equals(name)){
                    return false;
                }
            }
            return true;
        }

    }

}

interface FunctionX{
    /**
     *  Functional programming interface method
     * @param set
     */
    void apply(Set<String> set);
}

