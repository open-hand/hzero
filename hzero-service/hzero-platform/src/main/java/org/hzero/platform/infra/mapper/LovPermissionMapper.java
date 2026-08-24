package org.hzero.platform.infra.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.vo.LovUrlPermissionVO;

/**
 * 值集访问权限 Mapper。
 *
 * @author 25287
 */
public interface LovPermissionMapper {

    /**
     * 查询当前成员可访问的权限编码。
     *
     * @param memberId             成员 ID
     * @param memberType           成员类型
     * @param roleIds              当前或合并角色 ID
     * @param permissionCodes      权限编码
     * @param includeSecurityGroup 是否查询安全组授权
     * @param checkCurrentRole     是否仅校验当前或合并角色
     * @return 可访问的权限编码
     */
    List<String> selectAccessiblePermissionCodes(@Param("memberId") Long memberId,
                                                 @Param("memberType") String memberType,
                                                 @Param("roleIds") Collection<Long> roleIds,
                                                 @Param("permissionCodes") Collection<String> permissionCodes,
                                                 @Param("includeSecurityGroup") boolean includeSecurityGroup,
                                                 @Param("checkCurrentRole") boolean checkCurrentRole);

    /**
     * 查询 URL 值集对应的接口权限。
     *
     * @param urlLovs URL 值集的接口定位信息
     * @return 接口权限信息
     */
    List<LovUrlPermissionVO> selectUrlPermissions(@Param("urlLovs") Collection<LovUrlPermissionVO> urlLovs);

    /**
     * 按编码查询平台层角色 ID。
     *
     * @param code 角色编码
     * @return 角色 ID
     */
    List<Long> selectRoleIdsByCode(@Param("code") String code);
}
