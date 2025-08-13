package org.hzero.message.infra.mapper;

import org.hzero.message.domain.entity.UserGroup;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 用户组Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-14 13:44:56
 */
public interface NoticeUserGroupMapper extends BaseMapper<UserGroup> {

    /**
     * 分页查询用户组 - 租户级
     *
     * @param userGroup
     * @return
     */
	List<UserGroup> listUserGroups(long receiverTypeId,String groupName,String groupCode);
}
