package org.hzero.platform.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.hzero.platform.app.service.DashboardTenantCardService;
import org.hzero.platform.domain.entity.DashboardTenantCard;
import org.hzero.platform.domain.repository.DashboardTenantCardRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;

/**
 * 租户卡片分配应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
@Service
public class DashboardTenantCardServiceImpl implements DashboardTenantCardService {

    @Autowired
    private DashboardTenantCardRepository tenantCardRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DashboardTenantCard> createAssignCardToTenants(List<DashboardTenantCard> tenantCards) {
        // 去除前端可能会传递的重复数据
        List<DashboardTenantCard> distinctList = tenantCards.stream().distinct().collect(Collectors.toList());
        if (distinctList.size() != tenantCards.size()) {
            // 存在重复数据，抛出异常
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CARD_TENANT_REPEAT);
        }
        for (DashboardTenantCard card : distinctList) {
            card.validate(tenantCardRepository);
        }
        tenantCardRepository.batchInsertSelective(tenantCards);
        return distinctList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveAssignTenantCards(List<DashboardTenantCard> tenantCards) {
        tenantCardRepository.batchDeleteByPrimaryKey(tenantCards);
    }
}
