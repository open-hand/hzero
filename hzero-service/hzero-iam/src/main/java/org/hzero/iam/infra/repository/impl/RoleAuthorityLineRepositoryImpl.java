package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.repository.RoleAuthorityLineRepository;
import org.hzero.iam.infra.mapper.RoleAuthorityLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 角色数据权限行定义 资源库实现
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:51:40
 */
@Component
public class RoleAuthorityLineRepositoryImpl extends BaseRepositoryImpl<RoleAuthorityLine> implements RoleAuthorityLineRepository {

    @Autowired
    private RoleAuthorityLineMapper mapper;

    @Override
    public List<RoleAuthorityLine> selectByRoleAuthId(Long roleAuthId) {
        return mapper.selectByRoleAuthId(roleAuthId);
    }

    @Override
    public List<RoleAuthorityLine> selectDocRoleAuthLine() {
        return mapper.selectDocRoleAuthLine();
    }

    @Override
    public List<RoleAuthorityLine> selectRoleAuthLineByAuthTypeCode(String dimensionCode) {
        return mapper.selectRoleAuthLineByAuthTypeCode(dimensionCode);
    }
}
