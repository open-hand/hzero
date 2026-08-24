package org.hzero.platform.infra.repository.impl;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.platform.domain.repository.DataHierarchyRepository;
import org.hzero.platform.infra.mapper.DataHierarchyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com
 */
@Component
public class DataHierarchyRepositoryImpl extends BaseRepositoryImpl<DataHierarchy> implements DataHierarchyRepository {
    private static final String DATA_HIERARCHY_CACHE_KEY = HZeroService.Platform.CODE + ":data-hierarchy:";
    private DataHierarchyMapper dataHierarchyMapper;
    private RedisHelper redisHelper;

    @Autowired
    public DataHierarchyRepositoryImpl(DataHierarchyMapper dataHierarchyMapper,
                                       RedisHelper redisHelper) {
        this.dataHierarchyMapper = dataHierarchyMapper;
        this.redisHelper = redisHelper;
    }

    @Override
    @ProcessLovValue
    public List<DataHierarchyDTO> listDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag) {
        return dataHierarchyMapper.listDataHierarchy(tenantId, dataHierarchyCode, dataHierarchyName, enabledFlag);
    }

    @Override
    public DataHierarchyDTO getDataHierarchy(Long tenantId, Long dataHierarchyId) {
        return dataHierarchyMapper.getDataHierarchy(tenantId, dataHierarchyId);
    }

    @Override
    public DataHierarchyDTO getDataHierarchy(Long tenantId, String dataHierarchyCode) {
        return dataHierarchyMapper.getDataHierarchyByCode(tenantId, dataHierarchyCode);
    }

    @Override
    public List<DataHierarchy> listDataHierarchyChildrenByLevelPath(long tenantId, String levelPath) {
        return dataHierarchyMapper.listDataHierarchyChildren(tenantId, levelPath);
    }

    @Override
    public List<DataHierarchy> listDataHierarchyChildren(long tenantId, String dataHierarchyCode) {
        return dataHierarchyMapper.listDataHierarchyChildrenWithUnique(tenantId, dataHierarchyCode);
    }

    @Override
    public void refreshCache(long tenantId, List<DataHierarchyDTO> dataHierarchyList) {
        int count = getCount(dataHierarchyList);
        if (count > 0) {
            redisHelper.strSet(DATA_HIERARCHY_CACHE_KEY + tenantId, String.valueOf(count));
        } else {
            redisHelper.delKey(DATA_HIERARCHY_CACHE_KEY + tenantId);
        }

    }

    private int getCount(List<DataHierarchyDTO> dataHierarchyList) {
        return getCount(0, dataHierarchyList);
    }

    private int getCount(int count, List<DataHierarchyDTO> dataHierarchyList) {
        if (CollectionUtils.isEmpty(dataHierarchyList)) {
            return 0;
        }
        count += dataHierarchyList.stream().filter(item -> BaseConstants.Flag.YES.equals(item.getEnabledFlag())).count();
        for (DataHierarchyDTO dataHierarchy : dataHierarchyList) {
            if (!CollectionUtils.isEmpty(dataHierarchy.getChildren())) {
                count = getCount(count, dataHierarchy.getChildren());
            }
        }
        return count;
    }
}
