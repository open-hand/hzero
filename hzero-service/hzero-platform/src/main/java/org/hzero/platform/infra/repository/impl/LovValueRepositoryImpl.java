package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.common.HZeroCacheKey;
import org.hzero.common.HZeroConstant;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Language;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.infra.convertor.LovValueConvertor;
import org.hzero.platform.infra.mapper.LovValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * <b>name</b> LovRepositoryImpl
 * </p>
 * <p>
 * <b>description</b> 值集值RepositoryImpl
 * </p>
 *
 * @author gaokuo.dai@hand-china.com 2018年6月6日上午9:22:49
 * @version 1.0
 */
@Component
public class LovValueRepositoryImpl extends BaseRepositoryImpl<LovValue> implements LovValueRepository {

    @Autowired
    private LovValueMapper lovValueMapper;
    @Autowired
    private LovRepository lovRepository;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    public List<LovValue> listLovValueForCache(LovValue queryParam, String lang) {
        return this.lovValueMapper.listLovValueForCache(queryParam, lang);
    }

    @Override
    public boolean cleanCache(String lovCode, Long tenantId) {
        // 获取语言列表
        List<Language> languageList = LanguageHelper.languages();
        languageList.forEach(item -> {
            this.redisHelper.hshDelete(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, lovRepository.hashKey(tenantId, item.getCode()));
        });
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Object key) {
        LovValue value = this.selectByPrimaryKey(key);
        int deleteCount = super.deleteByPrimaryKey(key);
        if (value != null) {
            this.cleanCache(value.getLovCode(), value.getTenantId());
        }
        return deleteCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLovValue(LovValue lovValue) {
        int count = this.updateOptional(
                lovValue,
                LovValue.FIELD_MEANING,
                LovValue.FIELD_DESCRIPTION,
                LovValue.FIELD_ORDER_SEQ,
                LovValue.FIELD_START_DATE_ACTIVE,
                LovValue.FIELD_END_DATE_ACTIVE,
                LovValue.FIELD_ENABLED_FLAG,
                LovValue.FIELD_TAG
        );
        LanguageHelper.languages().forEach(language ->
                this.lovRepository.cleanCache(lovValue.getLovCode(), lovValue.getTenantId(), language.getCode())
        );
        return count;
    }

    @Override
    public List<LovValue> loadValueCacheFromDb(String lovCode, Long tenantId, String lang) {
        LovValue queryParam = new LovValue();
        queryParam.setLovCode(lovCode);
        queryParam.setTenantId(tenantId);
        List<LovValue> result = this.listLovValueForCache(queryParam, lang);
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        this.redisHelper.hshPut(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, lovRepository.hashKey(tenantId, lang), this.redisHelper.toJson(result
                .stream()
                .map(LovValueConvertor::entityToDto)
                .collect(Collectors.toList())));
        this.redisHelper.setExpire(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, HZeroConstant.Lov.Cache.EXPIRE);
        return result;
    }

    @Override
    public Page<LovValue> pageLovValue(LovValue lovValue, String lang, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> this.listLovValueForCache(lovValue, lang));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateLovValueByHeaderInfo(Lov header) {
        return this.lovValueMapper.updateLovValueByHeaderInfo(header);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByLovId(Long lovId) {
        return this.lovValueMapper.deleteByLovId(lovId);
    }

    @Override
    public Page<LovValue> pageAndSortByLovId(PageRequest pageRequest, Long lovId, Long tenantId, String value, String meaning) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.lovValueMapper.selectLovValueByLovId(lovId, tenantId, value, meaning));
    }

    @Override
    public int selectRepeatCodeCount(LovValue lovValue) {
        return this.lovValueMapper.selectRepeatCodeCount(lovValue);
    }

    @Override
    public Page<LovValue> pageAndSortByLovIdForDataGroup(PageRequest pageRequest, Long lovId, Long tenantId) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.lovValueMapper.selectLovValueForDataGroup(lovId, tenantId));
    }

    @Override
    public void batchDeleteLovValuesByPrimaryKey(List<LovValue> lovValues) {
        // 批量删除值集值
        this.batchDeleteByPrimaryKey(lovValues);
        // 批量删除缓存
        for (LovValue lovValue : lovValues) {
            // 获取所有语言信息
            List<Language> languages = LanguageHelper.languages();
            for (Language language : languages) {
                this.lovRepository.cleanCache(lovValue.getLovCode(), lovValue.getTenantId(), language.getCode());
            }
        }
    }
}
