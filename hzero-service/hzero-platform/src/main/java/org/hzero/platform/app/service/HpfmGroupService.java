package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.domain.entity.Group;

/**
 * 集团信息应用服务
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface HpfmGroupService {
    
    /**
     * 新增或更新集团信息
     * @param group
     * @return
     */
    Group insertOrUpdate(Group group);

    /**
     * 批量新增或更新集团信息<br/>
     * 新增时请提前生成好集团编码
     * @param groups
     * @return
     */
    List<Group> batchInsertOrUpdate(List<Group> groups);
    
}
