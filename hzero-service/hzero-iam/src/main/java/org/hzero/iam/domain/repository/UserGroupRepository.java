package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.iam.domain.entity.UserGroup;

/**
 * 用户组资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
public interface UserGroupRepository extends BaseRepository<UserGroup> {

    /**
     * 分页查询用户组信息 - 租户级
     *
     * @param pageRequest 分页参数
     * @param userGroup 查询条件
     * @return Page<UserGroup>
     */
    Page<UserGroup> pageUserGroup(PageRequest pageRequest, UserGroup userGroup);

    /**
     * 查询用户组明细信息
     *
     * @param userGroupId 主键
     * @return UserGroup
     */
    UserGroup selectUserGroupDetails(Long userGroupId);
}
