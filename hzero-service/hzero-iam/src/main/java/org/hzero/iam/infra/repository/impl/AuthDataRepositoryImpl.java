package org.hzero.iam.infra.repository.impl;

import org.hzero.iam.domain.repository.AuthDataRepository;
import org.hzero.iam.domain.service.authdata.condition.AuthDataCondition;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;
import org.hzero.iam.infra.mapper.AuthDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 权限数据仓库接口实现
 *
 * @author bo.he02@hand-china.com 2020/06/05 11:58
 */
@Repository
public class AuthDataRepositoryImpl implements AuthDataRepository {
    /**
     * 权限数据mapper
     */
    private final AuthDataMapper authDataMapper;

    @Autowired
    public AuthDataRepositoryImpl(AuthDataMapper authDataMapper) {
        this.authDataMapper = authDataMapper;
    }

    @Override
    public AuthDataVo queryComDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryComDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryOuDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryOuDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryInvOrgDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryInvOrgDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryPurOrgDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryPurOrgDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryPurAgentDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryPurAgentDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryPurCatDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryPurCatDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryLovDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryLovDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }

    @Override
    public AuthDataVo queryLovViewDataSourceInfo(AuthDataCondition authDataCondition) {
        return this.authDataMapper.queryLovViewDataSourceInfo(authDataCondition.getTenantId(), authDataCondition.getDataCode(),
                authDataCondition.getDataName());
    }
}
