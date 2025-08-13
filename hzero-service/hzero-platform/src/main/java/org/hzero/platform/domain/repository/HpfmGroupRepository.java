package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Group;

/**
 * 集团信息资源库
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface HpfmGroupRepository extends BaseRepository<Group> {
    
    /**
     * 检查编码重复数量
     * 
     * @param group
     * @return
     */
    int selectRepeatCount(Group group);
    
}
