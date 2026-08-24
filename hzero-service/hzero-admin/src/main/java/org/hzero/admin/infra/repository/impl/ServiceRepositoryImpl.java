package org.hzero.admin.infra.repository.impl;

import org.hzero.admin.domain.entity.HService;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.infra.mapper.ServiceMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用服务 资源库实现
 *
 * @author bojiangzhou
 */
@Component
public class ServiceRepositoryImpl extends BaseRepositoryImpl<HService> implements ServiceRepository {

    @Autowired
    private ServiceMapper serviceMapper;


    @Override
    public List<HService> selectDefaultServices(HService param) {
        return serviceMapper.selectDefaultServices(param);
    }

    @Override
    public List<HService> selectDefaultServicesWithVersion(HService param) {
        return serviceMapper.selectDefaultServicesWithVersion(param);
    }

    @Override
    public HService selectServiceDetails(Long serviceId) {
        return serviceMapper.selectServiceDetails(serviceId);
    }
}
