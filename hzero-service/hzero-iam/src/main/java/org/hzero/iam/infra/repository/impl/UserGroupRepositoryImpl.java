package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.infra.mapper.UserGroupMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户组 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
@Component
public class UserGroupRepositoryImpl extends BaseRepositoryImpl<UserGroup> implements UserGroupRepository {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Override
    public Page<UserGroup> pageUserGroup(PageRequest pageRequest, UserGroup userGroup) {
        return PageHelper.doPage(pageRequest, () -> userGroupMapper.selectUserGroupList(userGroup));
    }

    @Override 
    public UserGroup selectUserGroupDetails(Long userGroupId) {
        UserGroup userGroup = new UserGroup();
        userGroup.setUserGroupId(userGroupId);
        List<UserGroup> userGroups = userGroupMapper.selectUserGroupList(userGroup);
        if (CollectionUtils.isEmpty(userGroups)) {
            return null;
        }else {
            return userGroups.get(0);
        }
    }
}
