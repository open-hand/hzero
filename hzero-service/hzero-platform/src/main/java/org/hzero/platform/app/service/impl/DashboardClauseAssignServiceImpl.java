package org.hzero.platform.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hzero.platform.app.service.DashboardClauseAssignService;
import org.hzero.platform.domain.entity.DashboardClauseAssign;
import org.hzero.platform.domain.repository.DashboardClauseAssignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * 工作台条目分配租户应用服务默认实现
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class DashboardClauseAssignServiceImpl implements DashboardClauseAssignService {
    @Autowired
    private DashboardClauseAssignRepository dashboardClauseAssignRepository;

    /**
     * 保存工作台条目分配租户
     *
     * @param dashboardClauseAssigns 工作台条目分配租户
     * @return 工作台条目分配租户Id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DashboardClauseAssign> saveDbdClauseAssigns(List<DashboardClauseAssign> dashboardClauseAssigns) {
        List<DashboardClauseAssign> updateTemps = new ArrayList<>();
        List<DashboardClauseAssign> insertTemps = new ArrayList<>();
        // 数据处理
        dashboardClauseAssigns.forEach(dbdClauseAssign -> {
            if (!ObjectUtils.isEmpty(dbdClauseAssign.getClauseAssignId()) && !dbdClauseAssign.getClauseAssignId().equals(0L)) {
                // 需要更新的数据
                updateTemps.add(dbdClauseAssign);
            } else {
                // 需要插入的数据
                insertTemps.add(dbdClauseAssign);
            }
        });
        // 插入数据
        dashboardClauseAssignRepository.batchInsertSelective(insertTemps);
        // 更新数据
        dashboardClauseAssignRepository.batchUpdateByPrimaryKeySelective(updateTemps);
        return dashboardClauseAssigns;
    }

    /**
     * 删除工作台条目分配租户
     *
     * @param dashboardClauseAssigns 待删除的分配租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDbdClauseAssign(List<DashboardClauseAssign> dashboardClauseAssigns) {
        // 删除分配的租户信息
        dashboardClauseAssignRepository.batchDeleteByPrimaryKey(dashboardClauseAssigns);
    }
}
