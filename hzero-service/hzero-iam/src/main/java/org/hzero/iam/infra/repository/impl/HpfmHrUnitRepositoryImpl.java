package org.hzero.iam.infra.repository.impl;

import java.util.Collections;
import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.iam.domain.entity.HrUnit;
import org.hzero.iam.domain.repository.HpfmHrUnitRepository;
import org.hzero.iam.infra.common.utils.HpfmHrUnitUtils;
import org.hzero.iam.infra.mapper.HrUnitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/6/26
 */
@Repository
public class HpfmHrUnitRepositoryImpl extends BaseRepositoryImpl<HrUnit> implements HpfmHrUnitRepository {
    @Autowired
    private HrUnitMapper hrUnitMapper;

    @Override
    public List<HrUnit> queryHrUnitBelongedByUserId(Long userId) {
        return hrUnitMapper.queryHrUnitBelongedByUserId(userId);
    }

    @Override
    public HrUnit queryHrUnitParentTreeByUnitId(Long unitId) {
        return hrUnitMapper.queryHrUnitParentTreeByUnitId(unitId);
    }

    @Override
    public List<HrUnit> queryWholeHrUnitTreeByTenantId(Long tenantId, Long userId) {
        /**
         * 依据租户ID/用户ID, 查询所有组织
         */
        List<HrUnit> hrUnitList = hrUnitMapper.queryHrUnitTreeBelongedByUserId(tenantId, userId);
        if (CollectionUtils.isEmpty(hrUnitList)) {
            return Collections.emptyList();
        }

        return HpfmHrUnitUtils.formatHrUnitTree(hrUnitList);
    }

    @Override
    public List<HrUnit> batchQueryHrUnitsByUnitIds(List<Long> unitIdList) {
        return hrUnitMapper.batchQueryHrUnitsByUnitIds(unitIdList);
    }

    @Override
    public List<HrUnit> queryHrUnitSubTree(Long tenantId, Long parentHrUnitId,String unitCode,String unitName) {
        List<HrUnit> hrUnitList = hrUnitMapper.queryHrUnitSubTree(tenantId, parentHrUnitId);

        if (CollectionUtils.isEmpty(hrUnitList)) {
            return Collections.emptyList();
        }

        return HpfmHrUnitUtils.formatHrUnitTreeByCodeAndName(hrUnitList,unitCode,unitName);
    }
}
