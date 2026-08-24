package org.hzero.iam.domain.service;

import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.entity.UserAuthImport;

/**
 * 用户导入相关业务
 *
 * @author bojiangzhou 2019/05/13
 */
public interface UserRoleImportService {

    /**
     * 检查 MemberRole
     */
    void checkAndInitMemberRole(MemberRole memberRole);

    /**
     * 给用户分配角色
     */
    void assignRole(User user);

    /**
     * MemberRole 是否不存在
     */
    boolean memberRoleNotExists(MemberRole memberRole);

    /**
     * 检查权限
     */
    void checkAndInitUserAuthority(UserAuthImport userAuthImport);
}
