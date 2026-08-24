package org.hzero.admin.domain.repository;

import org.hzero.admin.domain.vo.Service;

import java.util.Set;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:41 上午
 */
public interface ServiceInitRegistryRepository {

    void add(Service service);

    void remove(Service service);

    Set<Service> get();

    void setInitialized(Service service);

}
