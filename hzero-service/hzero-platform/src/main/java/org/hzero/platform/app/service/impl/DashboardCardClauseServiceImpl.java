package org.hzero.platform.app.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.DashboardCardClauseService;
import org.hzero.platform.domain.entity.DashboardCardClause;
import org.hzero.platform.domain.repository.DashboardCardClauseRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * 应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
@Service
public class DashboardCardClauseServiceImpl implements DashboardCardClauseService {
    @Autowired
    private DashboardCardClauseRepository dashboardCardClauseRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DashboardCardClause> batchInsertDashboardCardClause(List<DashboardCardClause> dashboardCardClauses) {
        if (CollectionUtils.isEmpty(dashboardCardClauses)) {
            return Collections.emptyList();
        }
        // 校验传输数据中是否存在重复值
        List<DashboardCardClause> distinctList = dashboardCardClauses.stream().distinct().collect(Collectors.toList());
        if (distinctList.size() != dashboardCardClauses.size()) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CARD_CLAUSE_REPEAT);
        }
        // 校验传入数据在数据库中是否已经存在，存在则抛出异常
        dashboardCardClauses.forEach(dashboardCardClause -> {
            // 1.校验数据有效性
            dashboardCardClause.checkCardClauseRepeat(dashboardCardClauseRepository);
            // 2.校验rank值并初始化
            Integer maxRankValue = dashboardCardClauseRepository.selectMaxRankValue(dashboardCardClause.getCardId());
            if (maxRankValue == null){
                // 初始化Rank值
                dashboardCardClause.setOrderSeq(BaseConstants.Digital.ZERO);
            } else {
                dashboardCardClause.setOrderSeq(++maxRankValue);
            }
            // 3.插入数据
            dashboardCardClauseRepository.insertSelective(dashboardCardClause);
        });
        return dashboardCardClauses;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteDashboardCardClause(List<DashboardCardClause> dashboardCardClauses) {
        if (CollectionUtils.isNotEmpty(dashboardCardClauses)) {
            dashboardCardClauseRepository.batchDeleteByPrimaryKey(dashboardCardClauses);
        }
    }
}
