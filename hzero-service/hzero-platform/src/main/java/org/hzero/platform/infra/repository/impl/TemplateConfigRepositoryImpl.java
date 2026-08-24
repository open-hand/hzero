package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.TemplateConfig;
import org.hzero.platform.domain.repository.TemplateConfigRepository;
import org.hzero.platform.domain.vo.TemplateConfigCacheVO;
import org.hzero.platform.domain.vo.TemplateConfigVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.TemplateConfigMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 模板配置 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-06-28 10:26:29
 */
@Component
public class TemplateConfigRepositoryImpl extends BaseRepositoryImpl<TemplateConfig> implements
                TemplateConfigRepository {

    @Autowired
    private TemplateConfigMapper configMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    @ProcessLovValue
    public Page<TemplateConfig> selectTemplateConfigs(TemplateConfig templateConfig, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> configMapper.selectTemplateConfigs(templateConfig));
    }

    @Override
    @ProcessLovValue
    public TemplateConfig selectTemplateConfigDetails(Long configId) {
        return configMapper.selectTemplateConfigDetails(configId);
    }

    @Override
    public void addOrUpdateTemplateConfigCache(TemplateConfigVO createCacheVO, TemplateConfigVO removeCacheVO) {
        // 生成缓存Key
        String cacheKey = this.generateCacheKey(createCacheVO);
        if (removeCacheVO != null) {
            // 删除历史数据
            redisHelper.zSetRemove(cacheKey, redisHelper.toJson(removeCacheVO));
        }
        // 添加缓存
        redisHelper.zSetAdd(cacheKey, redisHelper.toJson(createCacheVO), createCacheVO.getOrderSeq());

    }

    @Override
    public void clearCache(String cacheKey) {
        redisHelper.delKey(cacheKey);
    }


    /**
     * 生成缓存Key,SSO:domain会动态变化
     */
    @Override
    public String generateCacheKey(TemplateConfigVO cacheVO) {
        // 查询获取缓存所需参数
        TemplateConfigCacheVO cacheParam = configMapper.selectTemplateConfigWithCacheData(cacheVO);
        if (cacheParam.getDomainUrl() == null || cacheParam.getSourceType() == null
                        || cacheParam.getTemplateCode() == null) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CACHE_DATA_NULL, cacheParam.getDomainUrl(),
                            cacheParam.getSourceType());
        }
        String tempDomainUrl = this.parseDomainUrl(cacheParam.getDomainUrl());
        // 格式hpfm:template-config:SSO:domainUrl:templateCode:configCode
        return StringUtils.join(FndConstants.CacheKey.TEMPLATE_CACHE_KEY, cacheParam.getSourceType(),
                        BaseConstants.Symbol.COLON, tempDomainUrl, BaseConstants.Symbol.COLON,
                        cacheParam.getTemplateCode(), BaseConstants.Symbol.COLON, cacheVO.getConfigCode());
    }

    @Override
    public void removeTemplateConfigsWithCache(TemplateConfig templateConfig) {
        // 先查询出当前分配模板下存在的模板配置信息
        List<TemplateConfig> templateConfigs = configMapper.selectTemplateConfigs(templateConfig);
        if (CollectionUtils.isNotEmpty(templateConfigs)) {
            // 存在模板配置信息，删除模板配置信息及缓存信息，使用任意templateConfig值获取缓存Key进行删除
            TemplateConfigVO cacheVO = new TemplateConfigVO();
            BeanUtils.copyProperties(templateConfigs.get(0), cacheVO);
            // 清除缓存
            this.clearCache(this.generateCacheKey(cacheVO));
            // 删除配置信息
            this.batchDeleteByPrimaryKey(templateConfigs);
        }
    }

    @Override
    public void generateDefaultTplCache(Long templateAssignId) {
        // 生成默认模板配置，先获取默认模板下的配置信息
        List<TemplateConfigVO> configVOs = configMapper.selectTemplateConfigVOs(templateAssignId);
        if (CollectionUtils.isNotEmpty(configVOs)) {
            configVOs.forEach(configVO -> redisHelper.zSetAdd(this.generateDefaultTplCacheKey(configVO),
                            redisHelper.toJson(configVO), configVO.getOrderSeq()));
        }
    }

    @Override
    public void generateDefaultTplCache(TemplateConfig templateConfig) {
        // 创建模板配置时，数据是单条操作，因此只需单一添加缓存即可
        TemplateConfigVO configVO = new TemplateConfigVO();
        BeanUtils.copyProperties(templateConfig, configVO);
        redisHelper.zSetAdd(this.generateDefaultTplCacheKey(configVO), redisHelper.toJson(configVO),
                        configVO.getOrderSeq());
    }

    @Override
    public void clearDefaultTplCache(Long templateAssignId) {
        // 清除默认模板配置，先获取默认模板下的配置信息
        List<TemplateConfigVO> configVOs = configMapper.selectTemplateConfigVOs(templateAssignId);
        if (CollectionUtils.isNotEmpty(configVOs)) {
            configVOs.forEach(configVO -> this.clearCache(this.generateDefaultTplCacheKey(configVO)));
        }
    }

    /**
     * 生成默认模板缓存Key 默认模板缓存key hpfm:default-template:{SSO}:{domainUrl}:{configCode}
     *
     * @param configVO 配置VO
     * @return 默认模板缓存Key
     */
    @Override
    public String generateDefaultTplCacheKey(TemplateConfigVO configVO) {
        // 查询获取缓存所需参数
        TemplateConfigCacheVO cacheParam = configMapper.selectTemplateConfigWithCacheData(configVO);
        if (cacheParam.getDomainUrl() == null || cacheParam.getSourceType() == null) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CACHE_DATA_NULL, cacheParam.getDomainUrl(),
                            cacheParam.getSourceType());
        }
        String tempDomainUrl = this.parseDomainUrl(cacheParam.getDomainUrl());
        // 格式hpfm:default-template:SSO:domainUrl:configCode
        return StringUtils
                        .join(FndConstants.CacheKey.DEFAULT_TEMPLATE_CACHE_KEY, cacheParam.getSourceType(),
                                        BaseConstants.Symbol.COLON, tempDomainUrl, BaseConstants.Symbol.COLON,
                                        configVO.getConfigCode());

    }

    private String parseDomainUrl(String domainUrl) {
        // 解析domainUrl，去除http://(https://)后面的'//'
        return StringUtils.replace(domainUrl, BaseConstants.Symbol.DOUBLE_SLASH, StringUtils.EMPTY);
    }
}
