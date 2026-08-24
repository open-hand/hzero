package org.hzero.boot.platform.common.domain.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.common.domain.service.ContentTemplateClientService;
import org.hzero.boot.platform.common.infra.constant.CommonConstants;
import org.hzero.boot.platform.common.vo.TemplateConfigVO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 内容模板服务实现
 *
 * @author bergturing 2020/08/18 15:06
 */
@Service
public class ContentTemplateClientServiceImpl implements ContentTemplateClientService {
    /**
     * RedisHelper
     */
    private final RedisHelper redisHelper;

    @Autowired
    public ContentTemplateClientServiceImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String sourceType, String templateCode,
                                                         String configCode) {
        return this.getTemplateConfigValue(domainUrl, sourceType, templateCode, configCode);
    }

    @Override
    public Set<TemplateConfigVO> getTemplateConfigValues(String domainUrl, String templateCode, String configCode) {
        return this.getTemplateConfigValue(domainUrl, CommonConstants.SSO_SOURCE_TYPE, templateCode,
                configCode);
    }

    @Override
    public TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String sourceType, String templateCode,
                                                      String configCode) {
        Set<TemplateConfigVO> templateConfigValue =
                this.getTemplateConfigValue(domainUrl, sourceType, templateCode, configCode);
        if (CollectionUtils.isNotEmpty(templateConfigValue)) {
            return templateConfigValue.iterator().next();
        }
        return null;
    }

    @Override
    public TemplateConfigVO getOneTemplateConfigValue(String domainUrl, String templateCode, String configCode) {
        Set<TemplateConfigVO> templateConfigValue = this.getTemplateConfigValue(domainUrl,
                CommonConstants.SSO_SOURCE_TYPE, templateCode, configCode);
        if (CollectionUtils.isNotEmpty(templateConfigValue)) {
            return templateConfigValue.iterator().next();
        }
        return null;
    }

    @Override
    public Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String sourceType, String configCode) {
        return this.getTemplateConfigValue(domainUrl, sourceType, null, configCode);
    }

    @Override
    public Set<TemplateConfigVO> getDefaultTplConfigValues(String domainUrl, String configCode) {
        return this.getTemplateConfigValue(domainUrl, CommonConstants.SSO_SOURCE_TYPE, null, configCode);
    }

    @Override
    public TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String sourceType, String configCode) {
        Set<TemplateConfigVO> templateConfigValue =
                this.getTemplateConfigValue(domainUrl, sourceType, null, configCode);
        if (CollectionUtils.isNotEmpty(templateConfigValue)) {
            return templateConfigValue.iterator().next();
        }
        return null;
    }

    @Override
    public TemplateConfigVO getOneDefaultTplConfigValue(String domainUrl, String configCode) {
        Set<TemplateConfigVO> templateConfigValue =
                this.getTemplateConfigValue(domainUrl, CommonConstants.SSO_SOURCE_TYPE, null, configCode);
        if (CollectionUtils.isNotEmpty(templateConfigValue)) {
            return templateConfigValue.iterator().next();
        }
        return null;
    }

    /**
     * 生成缓存Key
     *
     * @return key
     */
    private String generateCacheKey(String domainUrl, String sourceType, String templateCode, String configCode) {
        // 解析domainUrl，去除http://(https://)后面的'//'
        String tempDomainUrl = StringUtils.replace(domainUrl, BaseConstants.Symbol.DOUBLE_SLASH, StringUtils.EMPTY);
        return StringUtils.join(CommonConstants.CONTENT_TEMPLATE_CACHE_KEY, sourceType, BaseConstants.Symbol.COLON,
                tempDomainUrl, BaseConstants.Symbol.COLON, templateCode, BaseConstants.Symbol.COLON,
                configCode);
    }

    /**
     * 生成默认模板缓存Key 默认模板缓存key hpfm:default-template:{SSO}:{domainUrl}:{configCode}
     *
     * @return 默认模板缓存Key
     */
    private String generateDefaultTplCacheKey(String domainUrl, String sourceType, String configCode) {
        String tempDomainUrl = StringUtils.replace(domainUrl, BaseConstants.Symbol.DOUBLE_SLASH, StringUtils.EMPTY);
        // 格式hpfm:default-template:SSO:domainUrl:configCode
        return StringUtils.join(CommonConstants.DEFAULT_TEMPLATE_CACHE_KEY, sourceType,
                BaseConstants.Symbol.COLON, tempDomainUrl, BaseConstants.Symbol.COLON, configCode);

    }

    /**
     * 获取缓存值
     *
     * @return Set<TemplateConfigVO>
     */
    private Set<TemplateConfigVO> getTemplateConfigValue(String domainUrl, String sourceType, String templateCode,
                                                         String configCode) {
        Set<String> configSet;
        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            String uniqueKey = this.generateCacheKey(domainUrl, sourceType, templateCode, configCode);
            configSet = redisHelper.zSetRangeByScore(uniqueKey, 0D, 100D);
            if (CollectionUtils.isEmpty(configSet)) {
                // 获取默认模板缓存Key
                String defaultTplCacheKey = this.generateDefaultTplCacheKey(domainUrl, sourceType, configCode);
                configSet = redisHelper.zSetRangeByScore(defaultTplCacheKey, 0D, 100D);
                if (CollectionUtils.isEmpty(configSet)) {
                    return Collections.emptySet();
                }
            }
        } finally {
            redisHelper.clearCurrentDatabase();
        }

        Set<TemplateConfigVO> resultSet = new HashSet<>();
        configSet.forEach(config -> {
            TemplateConfigVO templateConfigVO = redisHelper.fromJson(config, TemplateConfigVO.class);
            resultSet.add(templateConfigVO);
        });
        return resultSet;
    }
}
