package org.hzero.platform.app.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.api.dto.LovViewAggregateDTO;
import org.hzero.platform.app.service.LovViewHeaderService;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.domain.service.LovViewDomainService;
import org.hzero.platform.domain.vo.LovViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 值集视图App服务实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午5:23:05
 */
@Service
public class LovViewHeaderServiceImpl implements LovViewHeaderService {
    
    @Autowired
    private LovViewHeaderRepository lovViewHeaderRepository;
    @Autowired
    private LovViewDomainService lovViewDomainService;
    
    @Override
    public LovViewVO queryLovViewInfo(String viewCode, Long tenantId, String lang, boolean onlyPublic) {
        return lovViewDomainService.queryLovViewInfo(viewCode, tenantId, lang, onlyPublic);
    }
    
    @Override
    public Page<LovViewHeader> pageLovViewHeaders(LovViewHeader queryParam, boolean isSite, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.lovViewHeaderRepository.selectLovViewHeader(queryParam, isSite));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LovViewHeader updateByPrimaryKey(LovViewHeader header) {
        return lovViewDomainService.updateByPrimaryKey(header);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteByPrimaryKey(List<LovViewHeader> headers) {
        return lovViewDomainService.batchDeleteByPrimaryKey(headers);
    }
    
    @Override
    public LovViewHeader insertSelective(LovViewHeader header) {
        return lovViewDomainService.insertSelective(header);
    }

    @Override
    public void copyLovView(Long tenantId, String viewCode, Long viewHeaderId, Integer siteFlag) {
        lovViewDomainService.copyLovView(tenantId, viewCode, viewHeaderId, siteFlag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLovViewHeader(LovViewHeader lovViewHeader) {
        lovViewDomainService.deleteLovViewHeader(lovViewHeader);
    }

    @Override
    public LovViewAggregateDTO queryLovViewAggregate(String viewCode, Long tenantId, String lang) {
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        if (StringUtils.isBlank(lang)) {
            lang = LanguageHelper.language();
        }
        return lovViewHeaderRepository.selectLovViewAggregate(viewCode, tenantId, lang);
    }

}
