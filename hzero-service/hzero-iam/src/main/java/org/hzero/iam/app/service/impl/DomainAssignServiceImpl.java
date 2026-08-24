package org.hzero.iam.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.DomainAssignService;
import org.hzero.iam.domain.entity.Domain;
import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.iam.domain.repository.DomainAssignRepository;
import org.hzero.iam.domain.repository.DomainRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单点二级域名分配应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
@Service
public class DomainAssignServiceImpl extends BaseAppService implements DomainAssignService {

    private final DomainAssignRepository assignRepository;
    private final DomainRepository domainRepository;

    @Autowired
    @Lazy
    public DomainAssignServiceImpl(DomainAssignRepository assignRepository, DomainRepository domainRepository) {
        this.assignRepository = assignRepository;
        this.domainRepository = domainRepository;
    }


    @Override
    public Page<DomainAssign> pageDomainAssign(Long domainId, DomainAssign domainAssign, PageRequest pageRequest) {
        return assignRepository.pageDomainAssign(domainAssign.setDomainId(domainId), pageRequest);
    }

    @Override
    public DomainAssign getDomainAssignDetail(Long domainId, Long domainAssignId) {
        return assignRepository.getDomainAssignDetail(domainId, domainAssignId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomainAssign createDomainAssign(Long domainId, DomainAssign domainAssign) {
        validObject(domainAssign.setDomainId(domainId));
        // 校验域名下是否已经分配传入参数中包含的租户和公司信息（注意判断公司值为null的情况）
        this.checkDomainAssignExistWhenInsert(domainAssign);
        assignRepository.insertSelective(domainAssign);
        // FIX 20201015 分配域名租户信息后需同步更新
        this.processDomainTenantId(domainId, domainAssign.getTenantId());
        Domain cacheDomain = domainRepository.selectByPrimaryKey(domainId);
        // 处理缓存
        domainRepository.updateDomainCache(cacheDomain, cacheDomain);
        return domainAssign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomainAssign updateDomainAssign(DomainAssign domainAssign) {
        SecurityTokenHelper.validToken(domainAssign);
        // 校验数据
        checkDomainAssignExistWhenUpdate(domainAssign);
        assignRepository.updateOptional(domainAssign, DomainAssign.FIELD_TENANT_ID, DomainAssign.FIELD_COMPANY_ID);
        // 处理缓存
        Domain domain = domainRepository.selectByPrimaryKey(domainAssign.getDomainId());
        domainRepository.updateDomainCache(domain, domain);
        return domainAssign;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveDomainAssigns(Long domainId, List<DomainAssign> domainAssigns) {
        SecurityTokenHelper.validToken(domainAssigns);
        domainAssigns.forEach(domainAssign -> {
            DomainAssign dbDomainAssign = assignRepository.selectByPrimaryKey(domainAssign.getDomainAssignId());
            if (dbDomainAssign != null && dbDomainAssign.getDomainId().equals(domainId)) {
                // 可以删除
                assignRepository.deleteByPrimaryKey(domainAssign.getDomainAssignId());
            } else {
                throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
            }
        });
        // 处理缓存
        List<DomainAssign> assigns = assignRepository.selectByCondition(Condition.builder(DomainAssign.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DomainAssign.FIELD_DOMAIN_ID, domainId)

                ).orderByDesc(DomainAssign.FIELD_LAST_UPDATE_DATE)
                .build());
        if (CollectionUtils.isEmpty(assigns)) {
            // 无分配租户，设置当前租户为主表租户Id
            processDomainTenantId(domainId, DetailsHelper.getUserDetails().getTenantId());
        } else {
            // 设置最近时间的租户Id
            processDomainTenantId(domainId, assigns.get(0).getTenantId());
        }
        Domain domain = domainRepository.selectByPrimaryKey(domainId);
        domainRepository.updateDomainCache(domain, domain);
    }

    /**
     * 新增校验参数唯一性
     *
     * @param domainAssign 需校验的数据
     */
    private void checkDomainAssignExistWhenInsert(DomainAssign domainAssign) {
        int count = assignRepository.selectCountByCondition(Condition.builder(DomainAssign.class)
                .andWhere(getValidSqls(domainAssign))
                .build());
        if (count != 0) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
        }
    }

    /**
     * 更新校验参数唯一性
     *
     * @param domainAssign 需校验的数据
     */
    private void checkDomainAssignExistWhenUpdate(DomainAssign domainAssign) {
        List<DomainAssign> dbDomainAssigns = assignRepository.selectByCondition(Condition.builder(DomainAssign.class)
                .andWhere(getValidSqls(domainAssign))
                .build());
        if (dbDomainAssigns.size() == 1) {
            DomainAssign dbDomainAssign = dbDomainAssigns.get(0);
            if (!dbDomainAssign.getDomainAssignId().equals(domainAssign.getDomainAssignId())) {
                // 数据存在且Id与传入的更新实体不一致，此时需报错
                throw new CommonException(BaseConstants.ErrorCode.DATA_EXISTS);
            }
        }
    }

    private Sqls getValidSqls(DomainAssign domainAssign) {
        Sqls sqls = Sqls.custom().andEqualTo(DomainAssign.FIELD_DOMAIN_ID, domainAssign.getDomainId())
                .andEqualTo(DomainAssign.FIELD_TENANT_ID, domainAssign.getTenantId());
        if (domainAssign.getCompanyId() == null) {
            sqls.andIsNull(DomainAssign.FIELD_COMPANY_ID);
        } else {
            sqls.andEqualTo(DomainAssign.FIELD_COMPANY_ID, domainAssign.getCompanyId());
        }
        return sqls;
    }

    /**
     * 处理域名信息，分配时进行更新，删除时需获取最近一条租户Id信息更新到头表
     */
    private void processDomainTenantId(Long domainId, Long tenantId) {
        Domain domain = domainRepository.selectByPrimaryKey(domainId);
        domain.setTenantId(tenantId);
        domainRepository.updateOptional(domain, Domain.FIELD_TENANT_ID);
    }
}
