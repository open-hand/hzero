package org.hzero.platform.app.service.impl;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.app.assembler.LovValueAssembler;
import org.hzero.platform.app.service.LovService;
import org.hzero.platform.app.service.LovValueService;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;
import org.hzero.platform.domain.repository.LovRepository;
import org.hzero.platform.domain.repository.LovValueRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.convertor.LovValueConvertor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 值集值App服务实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月25日下午6:37:42
 */
@Service
public class LovValueServiceImpl implements LovValueService {

    @Autowired
    private LovService lovService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private LovValueRepository lovValueRepository;
    @Autowired
    private LovRepository lovRepository;

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag) {
        return queryLovValue(lovCode, tenantId, tag, null, this.getCurrentLanguage());
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, String lang) {
        return queryLovValue(lovCode, tenantId, tag, null, lang);
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, Lov lov) {
        return queryLovValue(lovCode, tenantId, tag, lov, this.getCurrentLanguage());
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, Lov lov, String lang) {
        if (StringUtils.isEmpty(lovCode)) {
            return Collections.emptyList();
        }
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        if (StringUtils.isBlank(lang)) {
            lang = this.getCurrentLanguage();
        }
        lov = lov == null ? this.lovService.queryLovInfo(lovCode, tenantId, lang) : lov;
        if (lov == null || !Objects.equals(lov.getLovTypeCode(), FndConstants.LovTypeCode.INDEPENDENT)) {
            return Collections.emptyList();
        }
        Long lovTenantId = lov.getTenantId();
        // 通过权限检查和类型检查
        String values = this.redisHelper.hshGet(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, lovRepository.hashKey(tenantId, lang));
        if (Lov.LovAccessStatus.FORBIDDEN.name().equals(values)) {
            // 检查是否快速失败
            return Collections.emptyList();
        }
        List<LovValue> valueList;
        if (StringUtils.isBlank(values)) {
            valueList = this.lovValueRepository.loadValueCacheFromDb(lovCode, lovTenantId, lang);
        } else {
            valueList = this.redisHelper.fromJsonList(values, LovValue.class);
        }
        if (valueList == null) {
            // 如果无法从数据库中加载,则标记为快速失败
            this.redisHelper.hshPut(HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode, lovRepository.hashKey(tenantId, lang), Lov.LovAccessStatus.FORBIDDEN.name());
            return Collections.emptyList();
        }
        List<LovValueDTO> dtoList = new ArrayList<>();
        for (LovValue value : valueList.stream()
                .filter(value -> StringUtils.isEmpty(tag) || tag.equals(value.getTag()))
                .sorted(Comparator.comparingInt(LovValue::getOrderSeq))
                .collect(Collectors.toList())) {
            dtoList.add(LovValueConvertor.entityToDto(value));
        }
        return dtoList;
    }

    @Override
    public Page<LovValueDTO> pageLovValue(LovValue lovValue, PageRequest pageRequest) {
        if (StringUtils.isEmpty(lovValue.getLovCode())) {
            return new Page<>();
        }
        if (lovValue.getTenantId() == null) {
            lovValue.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        Lov vo = this.lovService.queryLovInfo(lovValue.getLovCode(), lovValue.getTenantId(), null);
        if (vo == null || !Objects.equals(vo.getLovTypeCode(), FndConstants.LovTypeCode.INDEPENDENT)) {
            return new Page<>();
        }
        String lang = this.getCurrentLanguage();
        lovValue.setTenantId(vo.getTenantId());
        Page<LovValue> valueList = this.lovValueRepository.pageLovValue(lovValue, lang, pageRequest);
        Page<LovValueDTO> dtoList = new Page<>();
        BeanUtils.copyProperties(valueList, dtoList, "content");
        for (LovValue value : valueList.getContent()) {
            dtoList.getContent().add(LovValueConvertor.entityToDto(value));
        }
        return dtoList;
    }

    @Override
    public List<LovValueDTO> queryLovValueByParentValue(String lovCode, String parentValue, Long tenantId) {
        List<LovValueDTO> result = this.queryLovValue(lovCode, tenantId, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result.stream().filter(x ->
                x != null && (x.getParentValue() == null || x.getParentValue().equals(parentValue))
        ).collect(Collectors.toList());
    }

    @Override
    public List<LovValueDTO> queryLovValueByTag(String lovCode, String tag, Long tenantId) {
        List<LovValueDTO> result = this.queryLovValue(lovCode, tenantId, null);
        if (result == null) {
            return Collections.emptyList();
        }
        return result.stream().filter(x ->
                x != null && (x.getTag() == null || x.getTag().equals(tag))
        ).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<LovValueDTO>> batchQueryLovValue(Map<String, String> queryMap, Long tenantId, String lang) {
        if (queryMap == null) {
            return MapUtils.EMPTY_SORTED_MAP;
        }
        if (queryMap.containsKey(LovValue.FIELD_TENANT_ID)) {
            tenantId = Long.parseLong(queryMap.remove(LovValue.FIELD_TENANT_ID));
        }
        Map<String, List<LovValueDTO>> result = new HashMap<>(queryMap.size());
        Long finalTenantId = tenantId;
        queryMap.forEach((key, value) -> result.put(key, this.queryLovValue(value, finalTenantId, null, lang)));
        return result;
    }

    @Override
    public List<LovValueDTO> queryLovValueTree(Map<String, String> queryMap, Long tenantId) {
        if (queryMap == null) {
            return Collections.emptyList();
        }
        queryMap.remove(LovValue.FIELD_TENANT_ID);
        // 按value大小逆序排序
        List<String> lovCodes = queryMap.entrySet().stream()
                .peek(entry -> {
                    Assert.notNull(entry, BaseConstants.ErrorCode.DATA_INVALID);
                    Assert.notNull(entry.getKey(), BaseConstants.ErrorCode.DATA_INVALID);
                    Assert.notNull(entry.getValue(), BaseConstants.ErrorCode.DATA_INVALID);
                    Assert.isTrue(NumberUtils.isParsable(entry.getValue()), BaseConstants.ErrorCode.DATA_INVALID);
                })
                .sorted((a, b) -> Integer.compare(Integer.parseInt(b.getValue()), Integer.parseInt(a.getValue())))
                .map(Entry::getKey)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(lovCodes)) {
            return Collections.emptyList();
        }
        String currentCode = lovCodes.get(0);
        List<LovValueDTO> currentLovValue = this.queryLovValue(currentCode, tenantId, null), previousLovValue;
        for (int i = 1; i < lovCodes.size(); i++) {
            previousLovValue = currentLovValue;

            currentCode = lovCodes.get(i);
            currentLovValue = this.queryLovValue(currentCode, tenantId, null);
            currentLovValue = LovValueAssembler.generateTree(currentLovValue, previousLovValue);
        }
        return currentLovValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLovValuesByPrimaryKey(List<LovValue> lovValues) {
        lovValueRepository.batchDeleteLovValuesByPrimaryKey(lovValues);
    }

    @Override
    public LovValue addLovValue(LovValue lovValue) {
        Assert.notNull(lovValue, BaseConstants.ErrorCode.DATA_INVALID);
        lovValue.validate(this.lovValueRepository);
        if (lovValue.getTenantId() == null) {
            lovValue.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        this.lovValueRepository.insertSelective(lovValue);
        LanguageHelper.languages().forEach(language ->
                this.lovRepository.cleanCache(lovValue.getLovCode(), lovValue.getTenantId(), language.getCode())
        );
        return lovValue;
    }

    @Override
    public LovValue updateLovValue(LovValue lovValue) {
        Assert.notNull(lovValue, BaseConstants.ErrorCode.DATA_INVALID);
        lovValue.validate(this.lovValueRepository);
        Lov lov = this.lovRepository.selectByPrimaryKey(lovValue.getLovId());
        Assert.notNull(lov, BaseConstants.ErrorCode.DATA_INVALID);
        lovValue.setTenantId(lov.getTenantId());
        this.lovValueRepository.updateLovValue(lovValue);
        return lovValue;
    }

    private String getCurrentLanguage() {
        String lang = BaseConstants.DEFAULT_LOCALE_STR;
        CustomUserDetails user = DetailsHelper.getUserDetails();
        if (user != null && user.getLanguage() != null) {
            lang = user.getLanguage();
        }
        return lang;
    }
}
