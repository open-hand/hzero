package org.hzero.platform.app.service.impl;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.app.service.HpfmGroupService;
import org.hzero.platform.domain.entity.Group;
import org.hzero.platform.domain.repository.HpfmGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
/**
 * 集团信息应用服务默认实现
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Service
public class HpfmGroupServiceImpl implements HpfmGroupService {
    
    @Autowired
    private HpfmGroupRepository groupRepository;
    @Autowired
    private CodeRuleService codeRuleService;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Group insertOrUpdate(Group group) {
        if(group == null) {
            return null;
        }
        Assert.notNull(group.getTenantId(), BaseConstants.ErrorCode.DATA_INVALID);
        if(group.getGroupId() == null) {
            // insert
            group = group.insertIntoDb(this.groupRepository, this.codeRuleService);
        }else {
            // update
            Group groupInDb = this.groupRepository.selectByPrimaryKey(group.getGroupId());
            Assert.notNull(groupInDb, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            Assert.isTrue(Objects.equals(group.getTenantId(), groupInDb.getTenantId()), BaseConstants.ErrorCode.DATA_INVALID);
            group.setGroupNum(groupInDb.getGroupNum());
            this.groupRepository.updateByPrimaryKey(group);
        }
        return group;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Group> batchInsertOrUpdate(List<Group> groups) {
        if(CollectionUtils.isEmpty(groups)) {
            return groups;
        }
        for(Group group : groups) {
            this.insertOrUpdate(group);
        }
        return groups;
    }

}
