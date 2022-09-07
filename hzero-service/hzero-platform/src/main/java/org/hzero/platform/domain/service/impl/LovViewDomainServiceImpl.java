package org.hzero.platform.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.common.Criteria;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.entity.LovViewLine;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.domain.repository.LovViewLineRepository;
import org.hzero.platform.domain.service.LovViewDomainService;
import org.hzero.platform.domain.vo.LovViewVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 值集视图逻辑统一处理Service实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2019/05/21 15:51
 */
@Component
public class LovViewDomainServiceImpl implements LovViewDomainService {

    @Autowired
    private LovViewHeaderRepository lovViewHeaderRepository;
    @Autowired
    private LovViewLineRepository lovViewLineRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private LovRepository lovRepository;

    private Logger logger = LoggerFactory.getLogger(LovViewDomainServiceImpl.class);
    /*获取SQL值集值公共API*/
    private static final String PUBLIC_QUERY_SQL_URL = "/v1/pub/lovs/sql/data";
    private static final String PUBLIC_ORG_QUERY_SQL_URL = "/v1/pub/{organizationId}/lovs/sql/data";

    @Override
    public LovViewVO queryLovViewInfo(String viewCode, Long tenantId, String lang, boolean onlyPublic) {
        LovViewVO bestMatch;
        List<LovViewVO> cacheResults;
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        if (StringUtils.isBlank(lang)) {
            lang = LanguageHelper.language();
        }
        // 先从租户级缓存中查询
        cacheResults = this.lovViewHeaderRepository.queryLovViewDTOFromCacheByTenant(viewCode, tenantId, lang);
        bestMatch = cacheResults.get(cacheResults.size() - 1);
        cacheResults.remove(bestMatch);

        Assert.notNull(bestMatch, BaseConstants.ErrorCode.NOT_NULL);
        if (bestMatch.getAccessStatus() == LovViewVO.LovViewAccessStatus.FORBIDDEN) {
            // 该租户ID存在于租级局黑名单中,拒绝访问
            this.logger.debug("lov view illegal access: viewCode [{}] tenantId [{}]", viewCode, tenantId);
        } else if (bestMatch.getAccessStatus() == LovViewVO.LovViewAccessStatus.NOT_FOUND) {
            // 如果租户级缓存没有命中且没有触发黑名单,则根据租户ID去数据库中查询一次
            bestMatch = this.queryLovViewInfoFromDb(viewCode, tenantId, lang);
            Assert.notNull(bestMatch, BaseConstants.ErrorCode.NOT_NULL);
        }
        // 租户级缓存未命中且传入租户ID不为0时,从全局级缓存中查询
        if (bestMatch.getAccessStatus() != LovViewVO.LovViewAccessStatus.ACCESS && !Objects
                .equals(BaseConstants.DEFAULT_TENANT_ID, tenantId)) {
            bestMatch = cacheResults.get(0);
            if (bestMatch.getAccessStatus() == LovViewVO.LovViewAccessStatus.FORBIDDEN) {
                // 该租户ID存在于全局级黑名单中,拒绝访问
                this.logger.warn("lov view illegal access: viewCode [{}] tenantId [{}]", viewCode, tenantId);
            } else if (bestMatch.getAccessStatus() == LovViewVO.LovViewAccessStatus.NOT_FOUND) {
                // 全局级缓存也未命中,访问数据库查询全局数据
                bestMatch = this.queryLovViewInfoFromDb(viewCode, BaseConstants.DEFAULT_TENANT_ID, lang);
                Assert.notNull(bestMatch, BaseConstants.ErrorCode.NOT_NULL);
            }
        }
        lovViewHeaderRepository.refreshCacheExpire(viewCode, tenantId, lang);
        bestMatch = bestMatch.getAccessStatus() == LovViewVO.LovViewAccessStatus.ACCESS ? bestMatch : null;
        if (bestMatch != null && onlyPublic && FndConstants.LovTypeCode.SQL.equals(bestMatch.getLovTypeCode())) {
            // 将queryUrl替换为 public API
            Lov dbLov = lovRepository.selectOne(new Lov().setTenantId(bestMatch.getTenantId()).setLovCode(bestMatch.getLovCode()));
            if (dbLov == null) {
                dbLov = lovRepository.selectOne(new Lov().setTenantId(BaseConstants.DEFAULT_TENANT_ID).setLovCode(bestMatch.getLovCode()));
            }
            if (BaseConstants.DEFAULT_TENANT_ID.equals(bestMatch.getTenantId())) {
                bestMatch.setQueryUrl(StringUtils.join("/", dbLov.getRouteName(), PUBLIC_QUERY_SQL_URL));
            } else {
                bestMatch.setQueryUrl(StringUtils.join("/", dbLov.getRouteName(), PUBLIC_ORG_QUERY_SQL_URL));
            }
        }
        return bestMatch;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LovViewHeader updateByPrimaryKey(LovViewHeader header) {
        if (header == null) {
            return null;
        }
        header.validate(this.lovViewHeaderRepository);
        LovViewHeader lovViewHeader = lovViewHeaderRepository.selectByPrimaryKey(header.getViewHeaderId());
        this.lovViewHeaderRepository.updateByPrimaryKey(header);
        // 处理行上的lovId
        if (lovViewHeader.getLovId().equals(header.getLovId())) {
            // 未修改视图头关联的值集，无需更新行上的lovId
            return header;
        }
        // 修改了视图头关联的值集，更新行上的lovId
        LovViewLine lovViewLine = new LovViewLine();
        lovViewLine.setViewHeaderId(header.getViewHeaderId());
        List<LovViewLine> headerLines = lovViewLineRepository.select(lovViewLine);
        headerLines.forEach(line -> line.setLovId(header.getLovId()));
        // 批量更新值集视图行
        lovViewLineRepository.batchUpdateOptional(headerLines, LovViewLine.FIELD_LOV_ID);
        return header;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteByPrimaryKey(List<LovViewHeader> headers) {
        if (CollectionUtils.isEmpty(headers)) {
            return 0;
        }
        headers.forEach(header -> header.cascadeDelete(this.lovViewHeaderRepository));
        return headers.size();
    }

    @Override
    public LovViewHeader insertSelective(LovViewHeader header) {
        if (header == null) {
            return null;
        }
        header.validate(this.lovViewHeaderRepository);
        this.lovViewHeaderRepository.insertSelective(header);
        io.choerodon.mybatis.helper.LanguageHelper.languages().forEach(language ->
                this.lovViewHeaderRepository.cleanCache(header.getViewCode(), header.getTenantId(), language.getCode())
        );
        return header;
    }

    @Override
    public void copyLovView(Long tenantId, String viewCode, Long viewHeaderId, Integer siteFlag) {
        LovViewHeader lovViewHeader = lovViewHeaderRepository.selectByPrimaryKey(viewHeaderId);
        Assert.notNull(lovViewHeader, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (siteFlag.equals(BaseConstants.Flag.YES)) {
            if (!lovViewHeader.getTenantId().equals(DetailsHelper.getUserDetails().getTenantId()) ||
                    lovViewHeader.getTenantId().equals(tenantId)) {
                // 平台级，可以将自己的值集复制给指定租户，判断复制的值集是否是当前租户的，并且传入的租户也与数据库中不同
                throw new CommonException(HpfmMsgCodeConstants.ERROR_LOV_VIEW_SITE_COPY_FAIL);
            }
        } else {
            // 租户级，仅可以复制预定义的，判断当前租户Id与要复制的值集的租户Id是否相同，相同则报错
            Assert.isTrue(!lovViewHeader.getTenantId().equals(tenantId), HpfmMsgCodeConstants.ERROR_LOV_VIEW_TENANT_REPEAT);
        }
        lovViewHeader.setTenantId(tenantId);
        lovViewHeader.setViewHeaderId(null);
        this.copyDuplicateLovView(lovViewHeader, viewHeaderId, tenantId);
    }

    @Override
    public void deleteLovViewHeader(LovViewHeader lovViewHeader) {
        // 判断值集视图是否为启用状态，启用状态的数据不允许删除
        LovViewHeader viewHeader = lovViewHeaderRepository.selectByPrimaryKey(lovViewHeader.getViewHeaderId());
        if (BaseConstants.Flag.YES.equals(viewHeader.getEnabledFlag())) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_ENABLED_LOV_VIEW_NOT_DELETE);
        }
        viewHeader.cascadeDelete(lovViewHeaderRepository);
    }

    //-------------------------------------------私有方法-----------------------------------------------

    /**
     * 复制值集视图，关联复制值集视图行
     */
    private void copyDuplicateLovView(LovViewHeader lovViewHeader, Long lovViewId, Long tenantId) {
        // 插入值集视图头
        LovViewHeader resultViewHeader = this.insertSelective(lovViewHeader);
        LovViewLine lovViewLine = new LovViewLine();
        lovViewLine.setViewHeaderId(lovViewId);
        List<LovViewLine> viewLines = lovViewLineRepository.selectOptional(lovViewLine,
                new Criteria().where(LovViewLine.FIELD_VIEW_HEADER_ID));
        if (CollectionUtils.isNotEmpty(viewLines)) {
            // 复制值集视图行
            viewLines.forEach(viewLine -> {
                viewLine.setViewLineId(null);
                viewLine.setViewHeaderId(resultViewHeader.getViewHeaderId());
                viewLine.setTenantId(tenantId);
                lovViewLineRepository.insertSelective(viewLine);
            });
        }
    }

    /**
     * 从数据库中加载Lov视图
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @param lang     语言
     * @return 带可访问性标记的Lov视图
     */
    private LovViewVO queryLovViewInfoFromDb(String viewCode, Long tenantId, String lang) {
        LovViewHeader lovViewHeader = this.lovViewHeaderRepository.selectByViewCode(viewCode, tenantId, lang);
        if (lovViewHeader == null) {
            // 数据库中未命中,设置拒绝缓存
            this.redisHelper.hshPut(HZeroCacheKey.Lov.VIEW_KEY_PREFIX + viewCode, lovRepository.hashKey(tenantId, lang),
                    LovViewVO.LovViewAccessStatus.FORBIDDEN.name());
            return new LovViewVO(LovViewVO.LovViewAccessStatus.NOT_FOUND);
        }
        //数据库数据命中,检查权限
        // 有权访问,组织缓存
        LovViewVO lovView = this.lovViewHeaderRepository.queryLovViewDTOByLovViewHeader(lovViewHeader, lang);
        lovView.setAccessStatus(LovViewVO.LovViewAccessStatus.ACCESS);
        return lovView;
    }

}
