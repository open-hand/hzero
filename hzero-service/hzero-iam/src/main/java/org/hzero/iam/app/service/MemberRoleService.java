package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.api.dto.MemberRoleAssignDTO;
import org.hzero.iam.domain.entity.MemberRole;

/**
 * 成员角色分配
 * 
 * @author allen
 */
public interface MemberRoleService {

    /**
     * 批量新增或更新成员角色<br/>
     * 成员角色列表分配方式, 此时MemberRole里的memberId必须指定<br/>
     */
    List<MemberRole> batchAssignMemberRole(List<MemberRole> memberRoleList);

    /**
     * 批量新增或更新成员角色<br/>
     * 内部调用，不检查角色是否属于当前用户
     */
    List<MemberRole> batchAssignMemberRoleInternal(List<MemberRole> memberRoleList);

    /**
     * 批量删除成员角色<br/>
     */
    void batchDeleteMemberRole(Long organizationId, List<MemberRole> memberRoleList);

    /**
     * 批量删除成员角色<br/>
     */
    void batchDeleteMemberRoleInternal(Long organizationId, List<MemberRole> memberRoleList);

    /**
     * 批量分配成员角色至租户, 分配层级为租户层, 分配层级值为待分配用户租户<br/>
     */
    List<MemberRole> batchAssignMemberRoleOnTenant(List<MemberRoleAssignDTO> memberRoleAssignDTOList);

}
