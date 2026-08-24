package org.hzero.platform.infra.repository.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateDTO;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateQueryDTO;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.hzero.platform.domain.repository.CommonTemplateRepository;
import org.hzero.platform.domain.vo.CommonTemplateVO;
import org.hzero.platform.infra.mapper.CommonTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用模板 资源库实现
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
@Component
public class CommonTemplateRepositoryImpl extends BaseRepositoryImpl<CommonTemplate> implements CommonTemplateRepository {
    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonTemplateRepositoryImpl.class);
    /**
     * RedisHelper对象
     */
    private final RedisHelper redisHelper;
    /**
     * Redis数据存放的数据库
     */
    @Value("${hzero.service.platform.redis-db:1}")
    private int redisDb;

    /**
     * 通用模板Mapper对象
     */
    private final CommonTemplateMapper commonTemplateMapper;

    @Autowired
    public CommonTemplateRepositoryImpl(RedisHelper redisHelper, CommonTemplateMapper commonTemplateMapper) {
        this.redisHelper = redisHelper;
        this.commonTemplateMapper = commonTemplateMapper;
    }

    @Override
    public List<CommonTemplateDTO> list(CommonTemplateQueryDTO queryDTO) {
        // 处理参数
        queryDTO = Optional.ofNullable(queryDTO).orElse(new CommonTemplateQueryDTO());

        // 按照条件查询
        return this.commonTemplateMapper.list(queryDTO);
    }

    @Override
    public List<CommonTemplate> list(Long organizationId, Set<Long> templateIds) {
        if (CollectionUtils.isEmpty(templateIds)) {
            return Collections.emptyList();
        }

        // 查询数据
        return ListUtils.partition(new ArrayList<>(templateIds), 1000)
                .stream()
                .map(subTemplateIds -> this.selectByCondition(Condition.builder(CommonTemplate.class)
                        .andWhere(Sqls.custom()
                                .andEqualTo(CommonTemplate.FIELD_TENANT_ID, organizationId, true)
                                .andIn(CommonTemplate.FIELD_TEMPLATE_ID, subTemplateIds)
                        ).build()))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public CommonTemplateDTO detail(Long organizationId, Long templateId) {
        // 查询数据
        return this.commonTemplateMapper.detail(organizationId, templateId);
    }

    @Override
    public CommonTemplateVO find(Long organizationId, String templateCode, String lang) {
        // 校验参数
        if (Objects.isNull(organizationId) || StringUtils.isBlank(templateCode) || StringUtils.isBlank(lang)) {
            // 参数未通过校验，直接返回null
            return null;
        }

        // 结果对象
        CommonTemplateVO commonTemplateVO;

        // 1. 从缓存中获取
        commonTemplateVO = this.findByMethod(organizationId, templateCode, lang, this::findInCache);
        if (Objects.nonNull(commonTemplateVO)) {
            return commonTemplateVO;
        }

        // 2. 从数据库中获取
        commonTemplateVO = this.findByMethod(organizationId, templateCode, lang, this::findInDb);

        // 返回结果对象
        return commonTemplateVO;
    }

    @Override
    public CommonTemplateVO cache(CommonTemplate commonTemplate) {
        if (Objects.isNull(commonTemplate)) {
            return null;
        }

        // 缓存值对象
        CommonTemplateVO commonTemplateVO = CommonTemplateVO.init(commonTemplate);
        // 缓存数据
        SafeRedisHelper.execute(this.redisDb, () -> {
            // 缓存数据
            this.redisHelper.hshPut(commonTemplateVO.getCacheKey(), commonTemplateVO.getTemplateCode(),
                    this.redisHelper.toJson(commonTemplateVO));
        });

        // 返回缓存的数据对象
        return commonTemplateVO;
    }

    @Override
    public void cacheAll() {
        // 查询所有的通用模板数据
        List<CommonTemplate> commonTemplates = this.selectAll();
        if (CollectionUtils.isEmpty(commonTemplates)) {
            return;
        }

        // 开始缓存
        commonTemplates.forEach(commonTemplate -> {
            // 缓存值对象
            CommonTemplateVO commonTemplateVO = CommonTemplateVO.init(commonTemplate);

            // 缓存数据
            SafeRedisHelper.execute(this.redisDb, () -> {
                // 缓存数据
                this.redisHelper.hshPut(commonTemplateVO.getCacheKey(), commonTemplateVO.getTemplateCode(),
                        this.redisHelper.toJson(commonTemplateVO));
            });
        });
    }

    /**
     * 查询通用模板值对象的流程，需指定寻找的方式
     * 流程如下：
     * 1. 直接获取数据
     * 2. 若直接获取的数据为空，且租户ID不为0，就使用租户ID值0去寻找(即租户级的数据不存在就从平台级中获取)
     *
     * @param tenantId             租户ID
     * @param templateCode         通用模板编码
     * @param lang                 语言
     * @param findCommonTemplateVO 查询通用模板值对象的方式
     * @return 若寻找到了通用模板值对象就返回，未寻找到就返回null
     */
    private CommonTemplateVO findByMethod(Long tenantId, String templateCode, String lang, FindCommonTemplateVO findCommonTemplateVO) {
        // 结果对象
        CommonTemplateVO commonTemplateVO;

        // 1. 直接获取数据
        commonTemplateVO = findCommonTemplateVO.find(tenantId, templateCode, lang);
        if (Objects.nonNull(commonTemplateVO)) {
            return commonTemplateVO;
        }

        // 2. 如果租户ID不为0，就从租户为0的数据中获取(即租户级的数据不存在就从平台级中获取)
        if (0L != tenantId) {
            commonTemplateVO = findCommonTemplateVO.find(0L, templateCode, lang);
        }

        // 返回结果
        return commonTemplateVO;
    }

    /**
     * 从缓存中获取通用模板值对象
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 模板值对象
     */
    private CommonTemplateVO findInCache(Long tenantId, String templateCode, String lang) {
        // 获取结果
        return SafeRedisHelper.execute(this.redisDb, this.redisHelper, () -> {
            // 获取值
            String value = this.redisHelper.hshGet(CommonTemplateVO.generateCacheKey(tenantId, lang), templateCode);
            if (StringUtils.isBlank(value)) {
                LOGGER.warn("Not Found Common Template From Cache For TenantId {} And Template Code {} And Lang {}",
                        tenantId, templateCode, lang);
                return null;
            }

            // 解析结果
            return this.redisHelper.fromJson(value, CommonTemplateVO.class);
        });
    }

    /**
     * 从数据库中获取通用模板值对象
     *
     * @param tenantId     租户ID
     * @param templateCode 模板编码
     * @param lang         语言
     * @return 模板值对象
     */
    private CommonTemplateVO findInDb(Long tenantId, String templateCode, String lang) {
        // 查询数据
        List<CommonTemplate> commonTemplates = this.selectByCondition(Condition.builder(CommonTemplate.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(CommonTemplate.FIELD_TENANT_ID, tenantId)
                        .andEqualTo(CommonTemplate.FIELD_TEMPLATE_CODE, templateCode)
                        .andEqualTo(CommonTemplate.FIELD_LANG, lang)
                ).build());
        if (CollectionUtils.isEmpty(commonTemplates)) {
            return null;
        }

        // 缓存数据，并返回结果
        return this.cache(commonTemplates.get(0));
    }

    /**
     * <p>
     * 查询通用模板函数接口
     * </p>
     *
     * @author bergturing 2020/08/05 11:18
     */
    @FunctionalInterface
    private interface FindCommonTemplateVO {
        /**
         * 查询通用模板值对象
         *
         * @param tenantId     租户ID
         * @param templateCode 通用模板编码
         * @param lang         语言
         * @return 通用模板值对象
         */
        CommonTemplateVO find(Long tenantId, String templateCode, String lang);
    }
}
