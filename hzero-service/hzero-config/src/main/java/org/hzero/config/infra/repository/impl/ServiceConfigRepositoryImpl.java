package org.hzero.config.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.config.domain.entity.ServiceConfig;
import org.hzero.config.domain.repository.ServiceConfigRepository;
import org.hzero.config.domain.vo.ConfigParam;
import org.hzero.config.infra.mapper.ServiceConfigMapper;
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
    public ServiceConfig selectDefaultConfig(ConfigParam param) {
        return configMapper.selectDefaultConfig(param);
    }

}
