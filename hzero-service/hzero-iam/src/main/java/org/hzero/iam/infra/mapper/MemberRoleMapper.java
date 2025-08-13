package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.MemberRole;

/**
 * @author allen.liu
 * @date 2018/6/19
 */
public interface MemberRoleMapper extends BaseMapper<MemberRole> {

    /**
     * 查询角色成员关系
     * @param params 参数
     * @return MemberRole
     */
    MemberRole selectMemberRole(MemberRole params);

}
