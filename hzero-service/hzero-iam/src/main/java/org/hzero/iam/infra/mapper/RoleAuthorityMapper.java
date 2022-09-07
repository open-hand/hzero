package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 角色数据权限定义Mapper
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
public interface RoleAuthorityMapper extends BaseMapper<RoleAuthority> {

    /**
     * 批量查询单据权限下的维度
     *
     * @param roleId     角色ID
     * @param docTypeIds 单据权限ID
     * @return 单据权限下的维度列表
     */
    List<RoleAuthorityLine> listDocTypeDim(@Param("roleId") Long roleId,
                                           @Param("docTypeIds") Set<Long> docTypeIds);

    /**
     * 根据单据名分页查询该角色下的单据权限信息
     *
     * @param roleId      角色ID
     * @param tenantId    租户ID
     * @param docTypeName 单据类型名称
     * @return 单据权限信息
     */
    Page<RoleAuthorityDTO> listRoleAuthorityPage(@Param("roleId") Long roleId,
                                                 @Param("tenantId") Long tenantId,
                                                 @Param("docTypeName") String docTypeName);

    /**
     * 查询角色单据类型定义维度分配列表
     *
     * @param roleId 角色ID
     * @param tenantId 租户ID
     * @return 角色单据类型定义维度分配列表
     */
    Set<String> listRoleAssign(@Param("roleId") long roleId, @Param("tenantId") Long tenantId);

    /**
     * 查询用户单据类型定义维度分配列表
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 用户单据类型定义维度分配列表
     */
    Set<String> listUserAssign(@Param("userId") long userId, @Param("tenantId") Long tenantId);


    /**
     * 查询单据已分配角色列表
     *
     * @param roleAuthorityDTO 角色权限
     * @return
     */
    List<RoleAuthorityDTO> listDocTypeAssignedRole(@Param("roleAuthorityDTO") RoleAuthorityDTO roleAuthorityDTO);

    /**
     * 获取与源角色单据维度匹配的目标角色单据维度
     *
     * @param roleId     角色Id
     * @param copyRoleId 目标角色Id
     * @return List<DocTypeDimension>
     */
    List<String> selectCompareDimensions(@Param("roleId") Long roleId, @Param("copyRoleId") Long copyRoleId);

    /**
     *
     *
     * @param docIds
     * @return
     */
    List<RoleAuthority> selectByDocIds(@Param("docIds") List<Long> docIds);

    /**
     * 查询获取启用状态的RoleAuthority
     *
     * @return 查询结果
     */
    List<RoleAuthority> selectDocRoleAuth();

    /**
     * 通过主键批量删除角色权限头信息
     *
     * @param roleAuthIds 需删除的角色权限头Id信息
     */
    void batchDeleteByRoleAuthorityId(@Param("roleAuthIds") List<Long> roleAuthIds);
}
