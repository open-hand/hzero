package org.hzero.iam.domain.service.authdata.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.service.authdata.AuthDataManager;
import org.hzero.iam.domain.service.authdata.AuthDataProvider;
import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.exception.AuthDataVoIsRequiredException;
import org.hzero.iam.domain.service.authdata.exception.AuthorityTypeCodeIsRequiredException;
import org.hzero.iam.domain.service.authdata.exception.NotProviderProcessException;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户权限数据管理器默认实现
 *
 * @author bo.he02@hand-china.com 2020/05/26 10:45
 */
public class DefaultAuthDataManager implements AuthDataManager {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthDataManager.class);

    /**
     * 权限数据提供器
     */
    private Map<String, List<AuthDataProvider>> authDataProviderCaches;

    @Override
    public void init(List<AuthDataProvider> authDataProviders) {
        authDataProviders = Optional.ofNullable(authDataProviders).orElse(Collections.emptyList());

        // 按权限码分类
        this.authDataProviderCaches = authDataProviders.stream().filter(Objects::nonNull)
                .filter(authDataProvider -> Objects.nonNull(authDataProvider.getAuthorityTypeCode()))
                .collect(Collectors.groupingBy(AuthDataProvider::getAuthorityTypeCode));

        // 处理同权限码提供器的优先级
        this.authDataProviderCaches.forEach((authorityTypeCode, innerAuthDataProviders) -> {
            innerAuthDataProviders.sort(Comparator.comparingInt(Ordered::getOrder));
        });
    }

    @Override
    public List<AuthDataVo> findAuthData(String authorityTypeCode, AuthDataCondition authDataCondition) {
        LOGGER.debug("Start Process Find Data Id: AuthorityTypeCode Is [{}] And Auth Data Vo Is [{}]",
                authorityTypeCode, authDataCondition);

        if (StringUtils.isBlank(authorityTypeCode)) {
            throw new AuthorityTypeCodeIsRequiredException();
        }
        if (Objects.isNull(authDataCondition)) {
            throw new AuthDataVoIsRequiredException();
        }

        // 判断当前权限类型码是否有对应的权限数据提供器
        if (this.authDataProviderCaches.containsKey(authorityTypeCode)) {
            for (AuthDataProvider authDataProvider : this.authDataProviderCaches.get(authorityTypeCode)) {
                try {
                    // 处理请求
                    List<AuthDataVo> dataIds = authDataProvider.findAuthData(authorityTypeCode, authDataCondition);
                    if (CollectionUtils.isNotEmpty(dataIds)) {
                        return dataIds;
                    }
                } catch (Exception e) {
                    if (LOGGER.isWarnEnabled()) {
                        // 处理异常，使用下面的格式打印日志，可以将异常信息保留
                        LOGGER.warn(String.format("Provider [%s] Process Authority Type Code [%s] Fail, Auth Data Vo Is [%s]",
                                authDataProvider, authorityTypeCode, authDataCondition), e);
                    }
                }
            }

            // 权限类型码对应的所有提供器都处理失败
            LOGGER.debug("All Provider Process Authority Type Code [{}] Fail", authorityTypeCode);

            // 返回空结果
            return Collections.emptyList();
        }

        // 没有可使用的权限数据提供器处理
        throw new NotProviderProcessException(authorityTypeCode, String.valueOf(authDataCondition));
    }
}
