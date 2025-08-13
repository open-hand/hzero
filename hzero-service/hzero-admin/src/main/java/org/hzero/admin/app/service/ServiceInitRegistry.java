package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.ServiceRegistryResponse;
import org.hzero.admin.domain.vo.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:11 上午
 */
public interface ServiceInitRegistry {

    Logger LOGGER = LoggerFactory.getLogger(ServiceInitRegistry.class);

    /**
     * 服务注册
     * @param service
     * @return
     */
    ServiceRegistryResponse register(Service service);

    /**
     * 取消注册
     * ps. 由于注册上的服务需要保证服务初始化成功，
     * 如果存在异常服务无法初始化成功，则需要取消注册，避免系统中不断重试报错。
     * @param dto
     * @return
     */
    ServiceRegistryResponse unregister(Service dto);

    /**
     * 获取所有已注册服务
     * @return
     */
    Set<Service> getServices();

    /**
     * 获取所有已注册且已初始化服务
     * @return
     */
    Set<Service> getInitializedServices();

    /**
     * 获取所有已注册且未初始化服务
     * @return
     */
    Set<Service> getUnInitializedServices();

    /**
     * 初始化服务
     * @param services
     * @return
     */
    default boolean init(Collection<Service> services) {
        if (services == null || services.isEmpty()) {
            return true;
        }
        boolean success = true;
        for (Service service : services) {
            try {
                if (shouldSkip(service)) {
                    skip(service);
                    continue;
                }
                init(service);
            } catch (RuntimeException e) {
                success = false;
                LOGGER.error("service [{}] init failed, ex = [{}]", service.toString(), e.getMessage());
            }
        }
        return success;
    }

    default void init(Service service) {
        if (!service.getInitialized() && doInit(service)) {
            service.setInitialized(true);
            setInitialized(service);
        }
    }

    /**
     * 返回true后，服务不会再执行doInit()初始化操作
     * @param service
     * @return
     * @throws RuntimeException
     */
    boolean doInit(Service service) throws RuntimeException;

    boolean shouldSkip(Service service);

    void skip(Service service);

    /**
     * 更新服务标识，标记为已初始化状态
     * @param service
     */
    void setInitialized(Service service);

}
