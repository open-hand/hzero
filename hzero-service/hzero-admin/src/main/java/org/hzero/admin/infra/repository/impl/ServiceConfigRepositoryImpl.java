package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.admin.domain.repository.ServiceConfigRepository;
import org.hzero.admin.infra.mapper.ServiceConfigMapper;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 服务配置 资源库实现
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class ServiceConfigRepositoryImpl extends BaseRepositoryImpl<ServiceConfig> implements ServiceConfigRepository {

    @Autowired
    private ServiceConfigMapper configMapper;

    @Override
    public ServiceConfig selectSelf(Long serviceConfigId) {
        ServiceConfig param = new ServiceConfig();
        param.setServiceConfigId(serviceConfigId);

        ServiceConfig self = selectOneOptional(param, new Criteria().select(
                ServiceConfig.FIELD_SERVICE_CONFIG_ID,
                ServiceConfig.FIELD_SERVICE_CODE,
                ServiceConfig.FIELD_SERVICE_ID,
                ServiceConfig.FIELD_CONFIG_VERSION,
                ServiceConfig.FIELD_CONFIG_YAML,
                ServiceConfig.FIELD_OBJECT_VERSION_NUMBER
        ));

        Assert.notNull(self, BaseConstants.ErrorCode.DATA_EXISTS);

        return self;

    }

    @Override
    public Page<ServiceConfig> pageServiceConfigList(ServiceConfig serviceConfig, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> configMapper.selectServiceConfigList(serviceConfig));
    }

    @Override
    public ServiceConfig selectConfigWithVersion(String serviceCode, String version) {
        return configMapper.selectConfigWithVersion(serviceCode, version);
    }

    @Override
    public int selectConfigCount(String serviceCode, String serviceVersion) {
        return configMapper.selectConfigCount(serviceCode, serviceVersion);
    }
}
