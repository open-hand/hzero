package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ServiceVersion;
import org.hzero.admin.domain.repository.ServiceVersionRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Component;

/**
 * 服务版本 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-08-23 14:35:21
 */
@Component
public class ServiceVersionRepositoryImpl extends BaseRepositoryImpl<ServiceVersion> implements ServiceVersionRepository {

    @Override
    public Page<ServiceVersion> page(PageRequest pageRequest, ServiceVersion queryParam) {
        return PageHelper.doPageAndSort(pageRequest, () -> select(queryParam));
    }
}
