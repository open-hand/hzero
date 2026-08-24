package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;

import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 角色数据权限定义资源库
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
public interface RoleAuthorityRepository extends BaseRepository<RoleAuthority> {

    /**
     * 分页查询角色单据权限
     *
     * @param pageRequest 分页请求
     * @param roleId      角色ID
     * @param docTypeName 单据类型名称
     * @param docTypeCode 单据类型编码
     * @param tenantId    租户ID
     * @return 单据权限
     */
    Page<RoleAuthorityDTO> listRoleAuthorityDTO(PageRequest pageRequest, Long roleId, Long tenantId, String docTypeName, String docTypeCode);

    /**
     * 查询角色单据类型定义维度分配列表
     *
     * @param roleId   角色ID
     * @return 角色单据类型定义维度分配列表
     */
    Set<String> listRoleAssign(long roleId);

    /**
     * 查询用户单据类型定义维度分配列表
     *
     * @param userId   用户ID
     * @return 用户单据类型定义维度分配列表
     */
    Set<String> listUserAssign(long userId);

    /**
     * 分页查询单据已分配角色列表
     * @param roleAuthorityDTO 擦汗寻条件
     * @param pageRequest  分页条件
     * @return
     */
    Page<RoleAuthorityDTO> pageDocTypeAssignedRole(RoleAuthorityDTO roleAuthorityDTO,
                                                   PageRequest pageRequest);

    /**
     * 获取与源角色单据维度匹配的目标角色单据维度
     *
     * @param roleId 角色Id
     * @param copyRoleId 目标角色Id
     * @return List<DocTypeDimension>
     */
    List<String> selectCompareDimensions(Long roleId, Long copyRoleId);

    /**
     * 通过单据Ids获取角色权限头信息，用于修复数据删除用
     *
     * @param docIds 单据Ids
     * @return 查询结果
     */
    List<RoleAuthority> selectByDocIds(List<Long> docIds);

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
    void batchDeleteByRoleAuthorityId(List<Long> roleAuthIds);
}
