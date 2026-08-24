package org.hzero.platform.app.service.impl;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.api.dto.LovAggregateDTO;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.domain.service.LovDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.mybatis.helper.LanguageHelper;

/**
 * 值集服务类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月12日下午2:38:19
 */
@Service
public class LovServiceImpl implements LovService {

    @Autowired
    private LovRepository lovRepository;
    @Autowired
    private LovValueRepository lovValueRepository;
    @Autowired
    private LovDomainService lovDomainService;

    @Override
    public Lov queryLovInfo(String lovCode, Long tenantId, String lang, boolean onlyPublic) {
        return lovDomainService.queryLovInfo(lovCode, tenantId, lang, onlyPublic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteLovHeaderByPrimaryKey(Lov lovHeader) {
        return lovDomainService.deleteLovHeader(lovHeader);
    }

    @Override
    public String queryLovSql(String lovCode, Long tenantId, String lang, boolean onlyPublic) {
        return lovDomainService.queryLovSql(lovCode, tenantId, lang, onlyPublic);
    }

    @Override
    public String queryLovTranslationSql(String lovCode, Long tenantId, String lang, boolean onlyPublic) {
        return lovDomainService.queryLovTranslationSql(lovCode, tenantId, lang, onlyPublic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Lov addLov(Lov lov) {
        return lovDomainService.addLov(lov);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Lov updateLov(Lov lov) {
        return lovDomainService.updateLov(lov);
    }

    @Override
    public List<Map<String, Object>> queryLovData(String lovCode, Long tenantId, String tag, Integer page, Integer size, Map<String, String> params, boolean onlyPublic) {
        return lovDomainService.queryLovData(lovCode, tenantId, tag, page, size, params, null, onlyPublic);
    }

    @Override
    public List<Map<String, Object>> queryPubLovData(String lovCode, Long tenantId, String tag, Integer page,
            Integer size, Map<String, String> params, String lang, boolean onlyPublic) {
        return lovDomainService.queryLovData(lovCode, tenantId, tag, page, size, params, lang, onlyPublic);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyLov(Long tenantId, String lovCode, Long lovId, Integer siteFlag) {
        lovDomainService.copyLov(tenantId, lovCode, lovId, siteFlag);
    }

    @Override
    public void initLovCache() {
        lovDomainService.deleteLovCache();
    }

    @Override
    public LovAggregateDTO queryLovAggregateLovValues(String lovCode, Long tenantId, String lang, String tag) {
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        if (StringUtils.isBlank(lang)) {
            lang = LanguageHelper.language();
        }
        LovAggregateDTO lovAggregateValuesDTO =
                lovRepository.selectLovAggregateLovValues(lovCode, tenantId, lang, tag);
        if (lovAggregateValuesDTO == null && !tenantId.equals(BaseConstants.DEFAULT_TENANT_ID)) {
            // 当前租户查不到查平台的
            lovAggregateValuesDTO = lovRepository.selectLovAggregateLovValues(lovCode, BaseConstants.DEFAULT_TENANT_ID, lang, tag);
        }
        return lovAggregateValuesDTO;
    }

}
