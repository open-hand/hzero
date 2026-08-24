package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.Group;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 集团信息Mapper
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
public interface HpfmGroupMapper extends BaseMapper<Group> {
    
    /**
     * 检查编码重复数量
     * 
     * @param group
     * @return
     */
    int selectRepeatCount(Group group);

}
