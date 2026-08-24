package org.hzero.plugin.platform.hr.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.plugin.platform.hr.domain.entity.HrSyncDept;
import org.hzero.plugin.platform.hr.domain.repository.HrSyncDeptRepository;
import org.hzero.plugin.platform.hr.infra.mapper.HrSyncDeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * HR部门数据同步 资源库实现
 *
 * @author zifeng.ding@hand-china.com 2019-12-25 09:35:06
 */
@Component
public class HrSyncDeptRepositoryImpl extends BaseRepositoryImpl<HrSyncDept> implements HrSyncDeptRepository {

    @Autowired
    private HrSyncDeptMapper hrSyncDeptMapper;

    @Override
    public List<HrSyncDept> getCreateDept(String syncTypeCode, Long tenantId) {
        return hrSyncDeptMapper.getCreateDept(syncTypeCode, tenantId);
    }

    @Override
    public List<HrSyncDept> getDeleteDept(String syncTypeCode, Long tenantId) {
        return hrSyncDeptMapper.getDeleteDept(syncTypeCode, tenantId);
    }

    @Override
    public List<HrSyncDept> getUpdateDept(String syncTypeCode, Long tenantId) {
        return hrSyncDeptMapper.getUpdateDept(syncTypeCode, tenantId);
    }

    @Override
    public List<HrSyncDept> getByDeptIds(String syncTypeCode, Long tenantId, List<Long> deptIds) {
        return this.selectByCondition(Condition
                .builder(HrSyncDept.class)
                .andWhere(Sqls.custom().andEqualTo(HrSyncDept.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(HrSyncDept.FIELD_SYNC_TYPE_CODE, syncTypeCode)
                        .andIn(HrSyncDept.FIELD_DEPARTMENT_ID, deptIds))
                .build());
    }

    @Override
    public List<HrSyncDept> getByUnitIds(String syncTypeCode, Long tenantId, List<Long> unitIds) {
        return this.selectByCondition(Condition
                .builder(HrSyncDept.class)
                .andWhere(Sqls.custom().andEqualTo(HrSyncDept.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(HrSyncDept.FIELD_SYNC_TYPE_CODE, syncTypeCode)
                        .andIn(HrSyncDept.FIELD_UNIT_ID, unitIds))
                .build());
    }
}
