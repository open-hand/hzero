package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.helper.LanguageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroCacheKey;
import org.hzero.common.HZeroConstant;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.LovViewAggregateDTO;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.entity.LovViewLine;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.domain.repository.LovViewLineRepository;
import org.hzero.platform.domain.vo.LovViewVO;
import org.hzero.platform.domain.vo.LovViewVO.LovViewAccessStatus;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.convertor.LovViewConvertor;
import org.hzero.platform.infra.mapper.LovViewHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 值集视图头表仓库实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午5:27:48
 */
@Component
public class LovViewHeaderRepositoryImpl extends BaseRepositoryImpl<LovViewHeader> implements LovViewHeaderRepository {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private LovViewLineRepository lovViewLineRepository;
    @Autowired
    private LovRepository lovRepository;
    @Autowired
    private LovService lovService;
    @Autowired
    private LovViewHeaderMapper lovViewHeaderMapper;

    @Override
    public List<LovViewVO> queryLovViewDTOFromCacheByTenant(String viewCode, Long tenantId, String lang) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        List<String> lovViewJson = lovRepository.getLovOrViewFromRedis(HZeroCacheKey.Lov.VIEW_KEY_PREFIX, viewCode, tenantId, lang);
        return CollectionUtils.isEmpty(lovViewJson)
                // 理论上不存在
                ? Collections.singletonList(new LovViewVO().setAccessStatus(LovViewAccessStatus.NOT_FOUND))
                : lovViewJson.stream().map(item ->
        {
            // 缓存不存在
            if (item == null) {
                return new LovViewVO().setAccessStatus(LovViewAccessStatus.NOT_FOUND);
                // 拒绝
            } else if (LovViewAccessStatus.FORBIDDEN.name().equals(item)) {
                return new LovViewVO().setAccessStatus(LovViewAccessStatus.FORBIDDEN);
            } else {
                // 值集存在
                return redisHelper.fromJson(item, LovViewVO.class).setAccessStatus(LovViewAccessStatus.ACCESS);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public LovViewHeader selectByViewCode(String viewCode, Long tenantId) {
        Assert.notNull(viewCode, HpfmMsgCodeConstants.ERR_NO_PRIMARY_KEY);
        LovViewHeader queryParam = new LovViewHeader();
        queryParam.setViewCode(viewCode);
        queryParam.setTenantId(tenantId);
        queryParam.setEnabledFlag(BaseConstants.Flag.YES);
        List<LovViewHeader> queryRes = this.select(queryParam);
        if (CollectionUtils.isNotEmpty(queryRes)) {
            if (queryRes.size() != 1) {
                throw new CommonException(HpfmMsgCodeConstants.ERR_REPEAT);
            }
            return queryRes.get(0);
        } else {
            return null;
        }
    }

    @Override
    public LovViewHeader selectByViewCode(String viewCode, Long tenantId, String lang) {
        return lovViewHeaderMapper.selectLovViewByCode(viewCode, tenantId, lang);
    }

    @Override
    public LovViewVO queryLovViewDTOByLovViewHeader(LovViewHeader lovViewHeader, String lang) {
        Assert.notNull(lovViewHeader, BaseConstants.ErrorCode.DATA_INVALID);
        Lov lov = this.lovRepository.selectByPrimaryKey(lovViewHeader.getLovId());
        if (lov == null || !Objects.equals(lov.getEnabledFlag(), BaseConstants.Flag.YES)) {
            // 没有有效的Lov
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        lov = this.lovService.queryLovInfo(lov.getLovCode(), lovViewHeader.getTenantId(), lang);
        if (lov == null) {
            // Lov无权访问
            throw new CommonException(BaseConstants.ErrorCode.FORBIDDEN);
        }
        List<LovViewLine> lines = this.lovViewLineRepository.selectByHeaderId(lovViewHeader.getViewHeaderId(), lang);
        LovViewVO result = LovViewConvertor.assembleLovViewDTO(lovViewHeader, lines, lov);
        this.redisHelper.hshPut(HZeroCacheKey.Lov.VIEW_KEY_PREFIX + lovViewHeader.getViewCode(),
                lovRepository.hashKey(lovViewHeader.getTenantId(), lang), this.redisHelper.toJson(result));
        this.redisHelper.setExpire(HZeroCacheKey.Lov.VIEW_KEY_PREFIX + lovViewHeader.getViewCode(), HZeroConstant.Lov.Cache.EXPIRE);
        return result;
    }

    @Override
    public List<LovViewHeader> selectLovViewHeader(LovViewHeader queryParam, boolean isSite) {
        return this.lovViewHeaderMapper.selectLovViewHeader(queryParam, isSite);
    }

    @Override
    public List<String> selectViewCodeByLovCode(String lovCode) {
        return this.lovViewHeaderMapper.selectViewCodeByLovCode(lovCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteViewLineByviewHeaderId(Long viewHeaderId) {
        int deleteCount = this.lovViewHeaderMapper.deleteViewLineByviewHeaderId(viewHeaderId);
        this.cleanCache(viewHeaderId);
        return deleteCount;
    }

    @Override
    public boolean cleanCache(String lovViewCode, Long tenantId, String language) {
        if (StringUtils.isEmpty(lovViewCode)) {
            return false;
        }
        this.redisHelper.hshDelete(HZeroCacheKey.Lov.VIEW_KEY_PREFIX + lovViewCode, lovRepository.hashKey(tenantId, language));
        return true;
    }

    @Override
    public boolean cleanCache(Long viewHeaderId) {
        if (viewHeaderId == null) {
            return false;
        }
        LovViewHeader viewHeader = this.selectByPrimaryKey(viewHeaderId);
        LanguageHelper.languages().forEach(language ->
                this.cleanCache(viewHeader.getViewCode(), viewHeader.getTenantId(), language.getCode()));
        return true;
    }

    @Override
    public int selectRepeatCodeCount(LovViewHeader queryParam) {
        return this.lovViewHeaderMapper.selectRepeatCodeCount(queryParam);
    }

    @Override
    public int updateByPrimaryKey(LovViewHeader record) {
        int updatedCount = super.updateByPrimaryKey(record);
        this.cleanCache(record.getViewHeaderId());
        return updatedCount;
    }

    @Override
    public LovViewHeader selectLovViewHeaderByPrimaryKey(Long viewHeaderId, Long tenantId) {
        return this.lovViewHeaderMapper.selectLovViewHeaderByPrimaryKey(viewHeaderId, tenantId);
    }

    @Override
    public void refreshCacheExpire(String viewCode, Long tenantId, String lang) {

    }

    @Override
    public LovViewAggregateDTO selectLovViewAggregate(String viewCode, Long tenantId, String lang) {
        return lovViewHeaderMapper.selectLovViewAggregate(viewCode, tenantId, lang);
    }

}
