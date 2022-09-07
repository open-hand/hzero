package org.hzero.platform.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.Group;
import org.hzero.platform.domain.repository.HpfmGroupRepository;
import org.hzero.platform.infra.mapper.HpfmGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 集团信息 资源库实现
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@Component
public class HpfmGroupRepositoryImpl extends BaseRepositoryImpl<Group> implements HpfmGroupRepository {
    
    @Autowired
    private HpfmGroupMapper groupMapper;

    @Override
    public int selectRepeatCount(Group group) {
        return this.groupMapper.selectRepeatCount(group);
    }
  
}
