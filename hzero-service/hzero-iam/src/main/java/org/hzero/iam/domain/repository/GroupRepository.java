package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.Group;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 集团信息资源库
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface GroupRepository extends BaseRepository<Group> {

    /**
     * 根据租户ID查询集团
     *
     * @param tenantId 租户ID
     * @return Group
     */
    Group selectByTenantId(Long tenantId);

}
