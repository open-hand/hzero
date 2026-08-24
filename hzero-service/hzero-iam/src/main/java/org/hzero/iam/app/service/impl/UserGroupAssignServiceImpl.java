package org.hzero.iam.app.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hzero.boot.message.entity.Receiver;
import org.hzero.iam.app.service.UserGroupAssignService;
import org.hzero.iam.domain.entity.UserGroupAssign;
import org.hzero.iam.domain.repository.UserGroupAssignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户组分配应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-15 16:06:11
 */
@Service
public class UserGroupAssignServiceImpl implements UserGroupAssignService {

    @Autowired
    private UserGroupAssignRepository userGroupAssignRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserGroupAssign> createOrUpdateUserGroupAssign(List<UserGroupAssign> userGroupAssignList) {
        // 插入新建数据，更新修改了默认用户组的数据
        List<UserGroupAssign> insertList = new LinkedList<>();
        List<UserGroupAssign> updateList = new LinkedList<>();
        for (UserGroupAssign userGroupAssign : userGroupAssignList) {
            // 校验数据是否重复
            userGroupAssign.validate(userGroupAssignRepository);
            // 将新增的数据和更新的数据分开处理
            if (userGroupAssign.getAssignId() == null) {
                insertList.add(userGroupAssign);
            } else {
                updateList.add(userGroupAssign);
            }
        }
        userGroupAssignRepository.batchInsertSelective(insertList);
        userGroupAssignRepository.batchUpdateOptional(updateList, UserGroupAssign.FIELD_DEFAULT_FLAG);
        return userGroupAssignList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserGroupAssign(List<UserGroupAssign> userGroupAssignList) {
        // 批量删除
        userGroupAssignRepository.batchDeleteByPrimaryKey(userGroupAssignList);
    }

    @Override
    public Set<Receiver> listOpenUserGroupAssign(List<UserGroupAssign> userGroupAssigns, String thirdPlatformType) {
        return userGroupAssignRepository.listOpenUserGroupAssign(userGroupAssigns, thirdPlatformType);
    }
}
