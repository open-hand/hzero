package org.hzero.iam.infra.repository.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.domain.entity.UserGroup;
import org.hzero.iam.domain.entity.UserGroupAssign;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
import org.hzero.iam.infra.mapper.UserGroupAssignMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户组分配 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
@Component
public class UserGroupAssignRepositoryImpl extends BaseRepositoryImpl<UserGroupAssign>
                implements UserGroupAssignRepository {

    @Autowired
    private UserGroupAssignMapper userGroupAssignMapper;

    @Override
    public Page<UserGroupAssign> pageUserGroupAssign(PageRequest pageRequest, UserGroupAssign userGroupAssign) {
        return PageHelper.doPageAndSort(pageRequest,
                        () -> userGroupAssignMapper.selectUserGroupAssignList(userGroupAssign));
    }

    @Override
    public Page<UserGroup> selectExcludeUserGroups(UserGroupAssign userGroupAssign, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,
                        () -> userGroupAssignMapper.selectExcludeUserGroups(userGroupAssign));
    }

    @Override
    public Page<UserGroupAssign> selectExcludeUsers(UserGroupAssign userGroupAssign, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> userGroupAssignMapper.selectExcludeUsers(userGroupAssign));
    }

    @Override
    public Set<Receiver> listUserGroupAssign(List<UserGroupAssign> userGroupAssigns, List<String> typeCode) {
        Set<Receiver> resultSet = new HashSet<>();
        for (UserGroupAssign userGroupAssign : userGroupAssigns) {
            List<Receiver> receivers = userGroupAssignMapper.selectAssignUserIds(userGroupAssign, typeCode);
            if (CollectionUtils.isNotEmpty(receivers)) {
                resultSet.addAll(receivers);
            }
        }
        return resultSet;
    }

    @Override
    public Set<Receiver> listOpenUserGroupAssign(List<UserGroupAssign> userGroupAssigns, String thirdPlatformType) {
        Set<Receiver> resultSet = new HashSet<>();
        for (UserGroupAssign userGroupAssign : userGroupAssigns) {
            List<Receiver> receivers = userGroupAssignMapper.selectOpenAssignUserIds(userGroupAssign, thirdPlatformType);
            if (CollectionUtils.isNotEmpty(receivers)) {
                resultSet.addAll(receivers);
            }
        }
        return resultSet;
    }
}
