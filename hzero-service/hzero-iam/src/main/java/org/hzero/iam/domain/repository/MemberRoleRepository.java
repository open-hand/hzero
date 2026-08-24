package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 成员角色分配资源库
 * 
 * @author allen
 * @date 2018/6/19
 */
public interface MemberRoleRepository extends BaseRepository<MemberRole> {

    /**
     * 根据角色ID和用户ID查询用户角色
     *
     * @param roleId 角色ID
     * @param memberId 用户ID
     * @return MemberRole
     */
    MemberRole getByRoleIdAndMemberId(Long roleId, Long memberId, HiamMemberType memberType);

}
