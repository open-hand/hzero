package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.ServiceVersionService;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.admin.domain.repository.ServiceVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:42 下午
 */
@Service
public class ServiceVersionServiceImpl implements ServiceVersionService {

    @Lazy
    @Autowired
    private ServiceVersionRepository serviceVersionRepository;

    @Override
    public Page<ServiceVersion> page(PageRequest pageRequest, Long serviceId) {
        return serviceVersionRepository.page(pageRequest, new ServiceVersion().setServiceId(serviceId));
    }
}
