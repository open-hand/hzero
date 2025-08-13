package org.hzero.platform.infra.repository.impl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.AssertUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.repository.StaticTextRepository;
import org.hzero.platform.domain.vo.StaticTextVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.StaticTextMapper;
import org.hzero.platform.infra.util.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.domain.PageInfo;
import io.choerodon.mybatis.helper.LanguageHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 平台静态信息 资源库实现
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
@Component
public class StaticTextRepositoryImpl extends BaseRepositoryImpl<StaticText> implements StaticTextRepository {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private StaticTextMapper staticTextMapper;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(StaticTextRepositoryImpl.class);

    @Override
    public Page<StaticTextVO> paging(StaticTextVO params, PageRequest pageRequest) {
        List<StaticTextVO> staticTextVOList = staticTextMapper.selectAllStaticText(params);
        if (CollectionUtils.isNotEmpty(staticTextVOList)) {
            Set<Long> parentIdSet = new HashSet<>();
            Set<Long> idSet = new HashSet<>();
            staticTextVOList.forEach(staticTextVO -> {
                String[] ids = staticTextVO.getParentIds().split(StaticText.SPLIT_POINT);
                if (ids.length == 1) {
                    parentIdSet.add(staticTextVO.getTextId());
                } else if (ids.length > 1) {
                    parentIdSet.add(Long.valueOf(ids[1]));
                }
            });

            if (CollectionUtils.isEmpty(parentIdSet)) {
                return null;
            }
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
            List<StaticTextVO> parentTextVOList = staticTextMapper.selectStaticTextByIds(new ArrayList<>(parentIdSet));
            staticTextVOList.forEach(staticTextVO -> {
                if (hasParentNode(parentTextVOList, staticTextVO)) {
                    idSet.add(staticTextVO.getTextId());
                    String[] ids = staticTextVO.getParentIds().split(StaticText.SPLIT_POINT);
                    for (String id : ids) {
                        if (!StaticText.ROOT_ID.toString().equals(id)) {
                            idSet.add(Long.valueOf(id));
                        }
                    }
                } else {
                    // 根节点参数
                    idSet.add(staticTextVO.getTextId());
                }
            });
            List<StaticTextVO> allTextVOList = staticTextMapper.selectStaticTextByIds(new ArrayList<>(idSet));
            Map<Long, List<StaticTextVO>> childTextVOMap =
                    allTextVOList.stream().collect(Collectors.groupingBy(StaticTextVO::getParentId));
            allTextVOList.forEach(vo -> vo.setChildren(childTextVOMap.get(vo.getTextId())));
            List<StaticTextVO> pageContent = allTextVOList.stream().filter(vo -> StaticText.ROOT_ID.equals(vo.getParentId()))
                            .collect(Collectors.toList());
            return new Page<>(
                    pageContent, new PageInfo(pageRequest.getPage(), pageRequest.getSize()), pageContent.size());
        }
        return null;
    }

    /**
     * 判断是否含有父节点
     *
     * @param staticTextVOList
     * @param staticTextVO
     * @return
     */
    private boolean hasParentNode(List<StaticTextVO> staticTextVOList, StaticTextVO staticTextVO) {
        for (StaticTextVO vo : staticTextVOList) {
            if (staticTextVO.getParentIds().split(StaticText.SPLIT_POINT).length > 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StaticTextVO getTextDetails(Long textId, String lang) {
        lang = Optional.ofNullable(lang).orElse(LanguageHelper.language());
        return staticTextMapper.selectStaticTextDetails(textId, lang);
    }

    @Override
    public StaticTextVO getTextHead(Long organizationId, Long companyId, String textCode) {
        if (StringUtils.isBlank(textCode)) {
            return null;
        }
        if (companyId == null) {
            companyId = BaseConstants.DEFAULT_TENANT_ID;
        }
        String key = FndConstants.CacheKey.STATIC_TEXT_HEAD + ":" + organizationId + ":" + companyId + ":" + textCode;
        String json = redisHelper.strGet(key);
        if (StringUtils.isBlank(json)) {
            StaticTextVO params = new StaticTextVO();
            params.setLang(LanguageHelper.language());
            params.setTextCode(textCode);
            params.setTenantId(organizationId);
            params.setCompanyId(companyId);
            params.setNow(new Date());
            StaticTextVO text = staticTextMapper.selectStaticTextByCode(params);
            AssertUtils.notNull(text, HpfmMsgCodeConstants.TEXT_CODE_NOT_EXISTS, textCode);
            try {
                long expire = Dates.getSecondsFromNowToDate(text.getEndDate());
                text.setEndDate(null);
                redisHelper.strSet(key, objectMapper.writeValueAsString(text), expire, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                logger.error("getTextHead objectMapper serialize error.", e);
            }
            return text;
        } else {
            try {
                return objectMapper.readValue(json, StaticTextVO.class);
            } catch (IOException e) {
                logger.error("getTextHead objectMapper serialize error.", e);
            }
        }
        return null;
    }

    @Override
    public List<Long> getAllChildTextId(Long textId) {
        List<StaticTextVO> children = staticTextMapper.selectAllChildTextId(textId);
        List<Long> ids = new ArrayList<>(8);
        collectAllChildTextId(ids, children);
        return ids;
    }

    @Override
    public void clearCache(StaticText staticText) {
        StaticTextVO params = new StaticTextVO();
        params.setTextId(staticText.getTextId());
        Set<String> codes = new HashSet<>(8);
        collectAllParentCode(codes, staticTextMapper.selectSelfAndParent(params));
        String key = null;
        for (String code : codes) {
            key = FndConstants.CacheKey.STATIC_TEXT_HEAD + ":" + staticText.getTenantId() + ":" + staticText.getCompanyId() + ":" + code;
            redisHelper.delKey(key);
            key = FndConstants.CacheKey.STATIC_TEXT_LINE + ":" + staticText.getTenantId() + ":" + staticText.getCompanyId() + ":" + code;
            String finalKey = key;
            LanguageHelper.languages().forEach(language ->
                    redisHelper.delKey(finalKey + "." + language.getCode())
            );
        }
    }

    private void collectAllParentCode(Set<String> codes, StaticTextVO text) {
        if (text != null) {
            codes.add(text.getTextCode());
            collectAllParentCode(codes, text.getParent());
        }
    }

    private void collectAllChildTextId(List<Long> ids, List<StaticTextVO> children) {
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(child -> {
                ids.add(child.getTextId());
                collectAllChildTextId(ids, child.getChildren());
            });
        }
    }

}
