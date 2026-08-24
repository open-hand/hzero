package org.hzero.admin.infra.repository

import org.hzero.admin.domain.repository.ServiceInitRegistryRepository
import org.hzero.admin.domain.vo.Service
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Title

/**
 * @author XCXCXCXCX* @date 2020/6/28 1:57 下午
 */
@Title("服务初始化注册仓库")
class ServiceInitRegistryRepositorySpec extends RepositorySpec {

    @Autowired
    ServiceInitRegistryRepository repository;
    Service service;

    def setup() {
        service = new Service();
        service.serviceName = "hzero-test";
        service.version = "test_version";
        service.healthUrl = "test.health";
        Map<String, String> metadata = new HashMap<>();
        metadata.put("TEST_KEY", "TEST_VALUE")
        service.metadata = metadata;
    }

    def cleanup() {
        repository.remove(service);
    }

    def "新增、获取、删除服务"() {
        when:
        repository.add(service);
        Set<Service> services = repository.get();
        println(services);

        then:
        services.contains(service);

        when:
        repository.remove(service);
        services = repository.get();
        println(services);

        then:
        !services.contains(service);
    }

    def "服务状态更新"() {
        when:
        repository.add(service);
        Set<Service> services = repository.get();

        then:
        services.contains(service);
        !service.initialized;

        when:
        service.initialized = true;
        repository.setInitialized(service);
        services = repository.get();

        then:
        services.forEach({ service ->
            if (service == this.service) {
                service.initialized
            }
        })
    }

}
