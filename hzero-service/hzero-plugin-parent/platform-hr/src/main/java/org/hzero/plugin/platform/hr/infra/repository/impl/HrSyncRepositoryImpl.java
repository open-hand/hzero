package org.hzero.plugin.platform.hr.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.plugin.platform.hr.domain.entity.HrSync;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncRepository;
import org.hzero.plugin.platform.hr.infra.mapper.HrSyncMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * hr基础数据同步外部系统 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-10-14 21:20:14
 */
@Component
public class HrSyncRepositoryImpl extends BaseRepositoryImpl<HrSync> implements HrSyncRepository {

    @Autowired
    private HrSyncMapper hrSyncMapper;
    
    @Override
    public Page<HrSync> listHrSync(PageRequest pageRequest, Long tenantId, String syncTypeCode, String authType,
            Integer enabledFlag) {
        return PageHelper.doPageAndSort(pageRequest, () -> hrSyncMapper.listHrSync(tenantId, syncTypeCode, authType, enabledFlag));
    }

    @Override
    public HrSync getHrSyncById(Long syncId) {
        return hrSyncMapper.getHrSyncById(syncId);
    }

  
}
