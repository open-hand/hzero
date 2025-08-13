package org.hzero.iam.infra.repository.impl;

import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.Group;
import org.hzero.iam.domain.repository.GroupRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 集团信息 资源库实现
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Component
public class GroupRepositoryImpl extends BaseRepositoryImpl<Group> implements GroupRepository {

    @Override
    public Group selectByTenantId(Long tenantId) {
        Group group = new Group();
        group.setTenantId(tenantId);
        return selectOne(group);
    }
}
