package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.api.dto.LovAggregateDTO;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.Lov.LovAccessStatus;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.LovMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 值集头RepositoryImpl
 *
 * @author gaokuo.dai@hand-china.com    2018年6月6日上午9:22:49
 */
@Component
public class LovRepositoryImpl extends BaseRepositoryImpl<Lov> implements LovRepository {

    @Autowired
    private LovMapper lovMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private LovViewHeaderRepository lovViewHeaderRepository;

    @Override
    public Lov selectByPrimaryKeyOrCode(Lov queryParam) {
        if (queryParam == null) {
            return null;
        }
        Lov result;
        Long lovId = queryParam.getLovId();
        String lovCode = queryParam.getLovCode();
        if (lovId != null) {
            result = this.selectByPrimaryKey(lovId);
        } else if (StringUtils.isNotEmpty(lovCode)) {
            Lov codeQueryParam = new Lov();
            codeQueryParam.setLovCode(lovCode);
            Long tenantId = queryParam.getTenantId() == null ? BaseConstants.DEFAULT_TENANT_ID : queryParam.getTenantId();
            codeQueryParam.setTenantId(tenantId);
            List<Lov> queryRes = this.select(codeQueryParam);
            if (CollectionUtils.isNotEmpty(queryRes)) {
                if (queryRes.size() != 1) {
                    throw new CommonException(HpfmMsgCodeConstants.ERR_REPEAT);
                }
            } else {
                return null;
            }
            result = queryRes.get(0);
        } else {
            throw new CommonException(HpfmMsgCodeConstants.ERR_NO_PRIMARY_KEY);
        }
        if (result == null || !Objects.equals(result.getEnabledFlag(), BaseConstants.Flag.YES)) {
            return null;
        }
        return result;
    }

    @Override
    public List<Lov> queryLovFromCacheByTenant(String lovCode, Long tenantId, String lang) {
        Assert.notNull(tenantId, "tenantId should not be null");
        List<String> lovJson = this.getLovOrViewFromRedis(HZeroCacheKey.Lov.HEADER_KEY_PREFIX, lovCode, tenantId, lang);
        return CollectionUtils.isEmpty(lovJson)
                // 理论上不存在
                ? Collections.singletonList(new Lov().setAccessStatus(LovAccessStatus.NOT_FOUND))
                : lovJson.stream().map(item ->
        {
            // 缓存不存在
            if (item == null) {
                return new Lov().setAccessStatus(LovAccessStatus.NOT_FOUND);
                // 拒绝
            } else if (LovAccessStatus.FORBIDDEN.name().equals(item)) {
                return new Lov().setAccessStatus(LovAccessStatus.FORBIDDEN);
            } else {
                // 值集存在
                return redisHelper.fromJson(item, Lov.class).setAccessStatus(LovAccessStatus.ACCESS);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<Lov> selectLovHeaders(Lov queryParam) {
        return this.lovMapper.selectLovHeaders(queryParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLov(Lov lov) {
        int count = this.updateOptional(
                lov,
                Lov.FIELD_LOV_NAME,
                Lov.FIELD_ENABLED_FLAG,
                Lov.FIELD_VALUE_FIELD,
                Lov.FIELD_DISPLAY_FIELD,
                Lov.FIELD_MUST_PAGE_FLAG,
                Lov.FIELD_DESCRIPTION,
                Lov.FIELD_CUSTOM_SQL,
                Lov.FIELD_CUSTOM_URL,
                Lov.FIELD_TRANSLATION_SQL,
                Lov.FIELD_PUBLIC_FLAG,
                Lov.FIELD_ENCRYPT_FIELD
        );
        this.cleanCache(lov);
        return count;
    }

    @Override
    public boolean cleanCache(String lovCode, Long tenantId, String lang) {
        // 删除 lov container
        this.redisHelper.hshDelete(HZeroCacheKey.Lov.HEADER_KEY_PREFIX + lovCode, hashKey(tenantId, lang));
        // 删除sql
        this.redisHelper.hshDelete(HZeroCacheKey.Lov.SQL_KEY_PREFIX + lovCode, hashKey(tenantId));
        // 删除翻译sql
        this.redisHelper.hshDelete(HZeroCacheKey.Lov.TRANSLATION_SQL_KEY_PREFIX + lovCode, hashKey(tenantId));
        // 删除values
        this.redisHelper.hshDelete(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, hashKey(tenantId, lang));
        // 删除view
        List<String> viewCodes = this.lovViewHeaderRepository.selectViewCodeByLovCode(lovCode);
        if (CollectionUtils.isNotEmpty(viewCodes)) {
            for (String viewCode : viewCodes) {
                this.redisHelper.hshDelete(HZeroCacheKey.Lov.VIEW_KEY_PREFIX + viewCode, hashKey(tenantId, lang));
            }
        }
        return true;
    }

    @Override
    public boolean cleanCache(Long lovId) {
        Lov lov = this.selectByPrimaryKey(lovId);
        if (lov == null) {
            return false;
        }
        LanguageHelper.languages().forEach(language ->
                cleanCache(lov.getLovCode(), lov.getTenantId(), language.getCode())
        );
        return true;
    }

    /**
     * 清空缓存
     *
     * @param lov 值集对象,ID和Code中至少一个不能为空
     * @return 是否清除成功
     */
    private boolean cleanCache(Lov lov) {
        boolean validatePass = (lov != null && (StringUtils.isNotEmpty(lov.getLovCode()) || lov.getLovId() != null));
        if (!validatePass) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        if (StringUtils.isNotEmpty(lov.getLovCode())) {
            LanguageHelper.languages().forEach(language ->
                    this.cleanCache(lov.getLovCode(), lov.getTenantId(), language.getCode())
            );
            return true;
        } else {
            return this.cleanCache(lov.getLovId());
        }
    }

    @Override
    public int selectRepeatCodeCount(Lov queryParam) {
        return this.lovMapper.selectRepeatCodeCount(queryParam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Object key) {
        if (key == null) {
            return 0;
        }
        int deleteCount = super.deleteByPrimaryKey(key);
        Lov lov;
        if (key instanceof Lov) {
            lov = (Lov) key;
        } else {
            lov = new Lov((Long) key, null, null);
        }
        this.cleanCache(lov);
        return deleteCount;
    }

    @Override
    public Page<Lov> pageAndSort(Lov record, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.selectLovHeaders(record));
    }

    @Override
    public Lov selectLovHeaderByLovId(Long lovId, Long tenantId, boolean sqlTypeControl) {
        Lov lov = this.lovMapper.selectLovHeaderByLovId(lovId, tenantId, sqlTypeControl);
        if (StringUtils.isBlank(lov.getParentLovName()) && StringUtils.isNotBlank(lov.getParentLovCode())
                && lov.getParentTenantId() != null) {
            // 此时父级值集在当前租户下不存在，查询0租户下的父级值集，若存在则返回可用的LOV信息
            Lov platformLov = lovMapper.selectLovHeaderByCodeAndTenant(lov.getParentLovCode(),
                    BaseConstants.DEFAULT_TENANT_ID);
            if (platformLov != null) {
                lov.setParentTenantId(BaseConstants.DEFAULT_TENANT_ID).setParentLovName(platformLov.getLovName());
            }

        }
        return lov;
    }

    @Override
    public Page<Lov> pageLovForDataGroupDimension(Lov record, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> lovMapper.listLovForDataGroupDimension(record));
    }

    @Override
    public String getLovOrViewFromRedis(String failFastCachePrefix, String cachePrefix, String code, Long tenantId, String lang) {
        final String failFastCacheKey = failFastCachePrefix + code + ':' + tenantId + ':' + lang;
        final String failFastFlag = this.redisHelper.strGet(failFastCacheKey);
        if (StringUtils.isNotEmpty(failFastFlag)) {
            // 如果有被拒绝的记录
            return Constants.FORBIDDEN;
        }
        final String cacheKey = cachePrefix + code + ':' + tenantId + ':' + lang;
        return this.redisHelper.strGet(cacheKey);
    }

    @Override
    public List<String> getLovOrViewFromRedis(String cachePrefix, String code, Long tenantId, String lang) {
        return redisHelper.hshMultiGet(cachePrefix + code, listHashKey(tenantId, lang));
    }

    @Override
    public Lov selectLovByViewCodeAndTenant(String viewCode, Long tenantId) {
        Lov lov = lovMapper.selectLovByViewCodeAndTenant(viewCode, tenantId);
        if (Objects.isNull(lov)) {
            lov = lovMapper.selectLovByViewCodeAndTenant(viewCode, BaseConstants.DEFAULT_TENANT_ID);
        }
        Assert.notNull(lov, BaseConstants.ErrorCode.NOT_NULL);
        return lov;
    }

    @Override
    public LovAggregateDTO selectLovAggregateLovValues(String lovCode, Long tenantId, String lang, String tag) {
        return lovMapper.selectLovAggregateLovValues(lovCode, tenantId, lang, tag);
    }

    @Override
    public Lov selectByIdAndLang(Long lovId, String lang) {
        return lovMapper.selectByIdAndLang(lovId, lang);
    }

    @Override
    public Lov selectByCodeAndLang(String lovCode, Long tenantId, String lang) {
        return lovMapper.selectByCodeAndLang(lovCode, tenantId, lang);
    }

    private Collection<String> listHashKey(Long tenantId, String lang) {
        List<String> hashKeys = new LinkedList<>();
        if (!BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            hashKeys.add(hashKey(BaseConstants.DEFAULT_TENANT_ID, lang));
        }
        hashKeys.add(hashKey(tenantId, lang));
        return hashKeys;
    }

}
