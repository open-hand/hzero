package org.hzero.iam.infra.repository.impl;

import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.MemberRoleMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 用户角色关系资源库实现
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/20 15:29
 */
@Component
public class MemberRoleRepositoryImpl extends BaseRepositoryImpl<MemberRole> implements MemberRoleRepository {

    private final MemberRoleMapper memberRoleMapper;

    public MemberRoleRepositoryImpl(MemberRoleMapper memberRoleMapper) {
        this.memberRoleMapper = memberRoleMapper;
    }

    @Override
    public MemberRole getByRoleIdAndMemberId(Long roleId, Long memberId, HiamMemberType memberType) {
        MemberRole memberRole = new MemberRole();
        memberRole.setRoleId(roleId);
        memberRole.setMemberId(memberId);
        memberRole.setMemberType(memberType.value());
        return memberRoleMapper.selectMemberRole(memberRole);
    }
}
