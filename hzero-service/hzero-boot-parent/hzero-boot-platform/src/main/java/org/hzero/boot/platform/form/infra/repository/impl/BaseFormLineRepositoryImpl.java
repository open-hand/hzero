package org.hzero.boot.platform.form.infra.repository.impl;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.form.constant.FormConstants;
import org.hzero.boot.platform.form.domain.repository.BaseFormLineRepository;
import org.hzero.boot.platform.form.domain.vo.FormLineVO;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 基础表单配置行资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019/11/22 10:33
 */
@Component
public class BaseFormLineRepositoryImpl implements BaseFormLineRepository {

    @Autowired
    private LovAdapter lovAdapter;

    /**
     * 表单配置行缓存Key
     */
    private static final String CACHE_FORM_LINE_CONFIGURATION = HZeroService.Platform.CODE + ":form-configuration:";
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseFormLineRepositoryImpl.class);

    private final RedisHelper redisHelper;

    public BaseFormLineRepositoryImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public void saveFormLineCache(String formHeaderCode, FormLineVO formLineVO) {
        if (Objects.equals(null, formLineVO) || Objects.equals(null, formLineVO.getTenantId())
                || Objects.equals(null, formLineVO.getItemCode())) {
            LOGGER.error("Form configuration line cache failed due to invalid parameters");
            throw new CommonException(BaseConstants.ErrorCode.NOT_NULL);
        }
        String cacheKey = StringUtils.join(CACHE_FORM_LINE_CONFIGURATION, formHeaderCode, BaseConstants.Symbol.COLON,
                formLineVO.getTenantId());
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.hshPut(cacheKey, formLineVO.getItemCode(), redisHelper.toJson(formLineVO)));
    }

    @Override
    public void deleteFormLineCache(String formHeaderCode, Long tenantId, String itemCode) {
        String cacheKey = StringUtils.join(CACHE_FORM_LINE_CONFIGURATION, formHeaderCode, BaseConstants.Symbol.COLON,
                tenantId);
        LOGGER.info("========>>>start deleting form line cache......");
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.hshDelete(cacheKey, itemCode));
    }


    @Override
    public FormLineVO getOneFromLineCache(String formHeaderCode, Long tenantId, String itemCode) {
        String cacheKey = StringUtils.join(CACHE_FORM_LINE_CONFIGURATION, formHeaderCode, BaseConstants.Symbol.COLON,
                tenantId);
        String formLineCache = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.hshGet(cacheKey, itemCode));
        return redisHelper.fromJson(formLineCache, FormLineVO.class);
    }

    @Override
    public List<FormLineVO> getAllFormLineCache(String formHeaderCode, Long tenantId) {
        String cacheKey = StringUtils.join(CACHE_FORM_LINE_CONFIGURATION, formHeaderCode, BaseConstants.Symbol.COLON,
                tenantId);
        List<String> formLineCaches = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                () -> redisHelper.hshVals(cacheKey));
        List<FormLineVO> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(formLineCaches)) {
            formLineCaches.forEach(formLineCache ->
                    resultList.add(redisHelper.fromJson(formLineCache, FormLineVO.class)));
        }
        return resultList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String translateAndProcessFormLineData(String formHeaderCode, Long tenantId, String translateJson) {
        // 缓存中获取 获取的到则做下面的操作，获取不到就直接返回即可
        Map<String, String> resultMap = (Map<String, String>) redisHelper.fromJson(translateJson, Map.class);
        if (MapUtils.isEmpty(resultMap)) {
            LOGGER.info(">>>>>>translateJson has no content and cannot be converted to Map");
            return translateJson;
        }
        List<FormLineVO> allFormLineCache = this.getAllFormLineCache(formHeaderCode, tenantId);
        if (CollectionUtils.isEmpty(allFormLineCache)) {
            LOGGER.info(">>>>>>The form line cache does not exist, please refresh the form line cache");
            return translateJson;
        }
        allFormLineCache.forEach(formLineVO -> {
            switch (formLineVO.getItemTypeCode()) {
                case FormConstants.LOV_VIEW_ITEM_CODE:
                    // 存在要翻译的值集视图值，从翻译Json中选出要做值集视图翻译的SQL，进行转换
                    LovViewDTO lovViewDTO =
                            lovAdapter.queryLovViewInfo(formLineVO.getValueSet(), formLineVO.getTenantId());
                    if (lovViewDTO == null) {
                        LOGGER.info(">>>>>>The lov view not exist, lov view code is: {}, tenantId is: {}", formLineVO.getValueSet(), formLineVO.getTenantId());
                        return;
                    }
                    List<String> params = new ArrayList<>();
                    params.add(resultMap.get(formLineVO.getItemCode()));
                    List<LovValueDTO> lovViewMeaning = lovAdapter.queryLovValue(lovViewDTO.getLovCode(), lovViewDTO.getTenantId(),params);
                    if (lovViewMeaning != null && lovViewMeaning.size() == 1) {
                        // 设置翻译之后的值
                        Map<String, String> strMap = new HashMap<>(4);
                        strMap.put("value", resultMap.get(formLineVO.getItemCode()));
                        strMap.put("meaning", lovViewMeaning.get(0).getMeaning());
                        resultMap.put(formLineVO.getItemCode(), redisHelper.toJson(strMap));
                    }
                    break;
                case FormConstants.LOV_ITEM_CODE:
                    // 翻译独立值集类型内容
                    String lovMeaning = lovAdapter.queryLovMeaning(formLineVO.getValueSet(), formLineVO.getTenantId(),
                            resultMap.get(formLineVO.getItemCode()));
                    if (lovMeaning != null) {
                        // 设置翻译之后的值
                        Map<String, String> strMap = new HashMap<>(4);
                        strMap.put("value", resultMap.get(formLineVO.getItemCode()));
                        strMap.put("meaning", lovMeaning);
                        resultMap.put(formLineVO.getItemCode(), redisHelper.toJson(strMap));
                    }
                    break;
                case FormConstants.PASSWORD_ITEM_CODE:
                    // 密码类型的内容需从参数列表中去除掉
                    resultMap.remove(formLineVO.getItemCode());
                    break;
                default:
                    // 无需做特殊处理，直接返回即可
                    break;
            }
        });
        return redisHelper.toJson(resultMap);
    }
}
