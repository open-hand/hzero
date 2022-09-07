package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncEmployee;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncEmployeeRepository;
import org.hzero.plugin.platform.hr.infra.mapper.HrSyncEmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HR员工数据同步 资源库实现
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@Component
public class HrSyncEmployeeRepositoryImpl extends BaseRepositoryImpl<HrSyncEmployee> implements HrSyncEmployeeRepository {
    @Autowired
    private HrSyncEmployeeMapper hrSyncEmployeeMapper;

    @Override
    public List<HrSyncEmployee> getCreateEmployee(String syncTypeCode, Long tenantId) {
        return hrSyncEmployeeMapper.getCreateEmployee(syncTypeCode, tenantId);
    }

    @Override
    public List<HrSyncEmployee> getDeleteEmployee(String syncTypeCode, Long tenantId) {
        return hrSyncEmployeeMapper.getDeleteEmployee(syncTypeCode, tenantId);
    }

    @Override
    public List<HrSyncEmployee> getUpdateEmployee(String syncTypeCode, Long tenantId) {
        return hrSyncEmployeeMapper.getUpdateEmployee(syncTypeCode, tenantId);
    }

}
