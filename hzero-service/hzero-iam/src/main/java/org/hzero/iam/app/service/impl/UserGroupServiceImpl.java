package org.hzero.iam.app.service.impl;

import org.hzero.iam.app.service.UserGroupService;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
import org.hzero.iam.domain.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户组应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;
    @Autowired
    private UserGroupAssignRepository userGroupAssignRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserGroup createUserGroup(UserGroup userGroup) {
        // 校验数据是否已经存在
        userGroup.validate(userGroupRepository);
        userGroupRepository.insertSelective(userGroup);
        return userGroup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserGroup updateUserGroup(UserGroup userGroup) {
        userGroupRepository.updateOptional(userGroup, UserGroup.FIELD_GROUP_NAME, UserGroup.FIELD_REMARK,
                        UserGroup.FIELD_ENABLED_FLAG);
        return userGroup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserGroup(UserGroup userGroup) {
        // 删除时校验当前用户组是否已经进行分配
        userGroup.checkUserGroupAssign(userGroupAssignRepository);
        userGroupRepository.deleteByPrimaryKey(userGroup.getUserGroupId());
    }
}
