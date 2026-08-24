package org.hzero.iam.infra.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.core.util.DomainUtils;
import org.hzero.iam.api.dto.DomainDTO;
import org.hzero.iam.domain.entity.Domain;
import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.iam.domain.repository.DomainAssignRepository;
import org.hzero.iam.domain.repository.DomainRepository;
import org.hzero.iam.domain.vo.DomainAssignCacheVO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.DomainMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 门户分配 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
@Component
public class DomainRepositoryImpl extends BaseRepositoryImpl<Domain> implements DomainRepository {

    @Autowired
    private DomainMapper domainMapper;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private DomainAssignRepository domainAssignRepository;

    @Override
    public List<Domain> selectByOptions(Domain domain) {
        return domainMapper.selectByOptions(domain);
    }

    @Override
    public Domain selectByDomainId(Long domainId) {
        return domainMapper.selectByDomainId(domainId);
    }

    @Override
    public void initCacheDomain() {
        List<DomainDTO> cacheList = domainMapper.getAllDomain();

        // 根据 key - 域名；value - domain
        Map<String, String> hostDomain = cacheList.stream().collect(
                        Collectors.toMap(d -> DomainUtils.getDomain(d.getDomainUrl()), d -> redisHelper.toJson(d)));

        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            redisHelper.hshPutAll(Constants.HIAM_DOMAIN, hostDomain);
        });
    }

    @Override
    public void updateDomainCache(Domain oldDomain, Domain domain) {
        if (oldDomain != null) {
            deleteDomainCache(oldDomain);
        }
        DomainDTO cacheDTO = DomainDTO.from(domain);
        List<DomainAssign> domainAssignCacheValues = getDomainAssignCacheValues(domain.getDomainId());
        cacheDTO.setTenants(DomainAssignCacheVO.from(domainAssignCacheValues));
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            redisHelper.hshPut(Constants.HIAM_DOMAIN, DomainUtils.getDomain(domain.getDomainUrl()),
                            redisHelper.toJson(cacheDTO));
        });
    }

    @Override
    public void deleteDomainCache(Domain domain) {
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            redisHelper.hshDelete(Constants.HIAM_DOMAIN, DomainUtils.getDomain(domain.getDomainUrl()));
        });
    }

    /**
     * 获取域名分配信息
     * @param domainId 域名Id
     * @return 缓存结果
     */
    private List<DomainAssign> getDomainAssignCacheValues(Long domainId) {
        return domainAssignRepository.selectByCondition(Condition.builder(DomainAssign.class)
                        .select(DomainAssign.FIELD_TENANT_ID, DomainAssign.FIELD_COMPANY_ID)
                        .andWhere(Sqls.custom()
                                .andEqualTo(DomainAssign.FIELD_DOMAIN_ID, domainId))
                        .build());
    }
}
