package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.RoleSecGrpDTO;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.mybatis.base.BaseRepository;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 安全组资源库
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpRepository extends BaseRepository<SecGrp> {

    /**
     * 分页查询安全组
     *
     * @param tenantId    租户ID，可为空(平台层可不传)
     * @param secGrp      查询参数
     * @param pageRequest 分页参数
     * @return 分页数据
     */
    Page<SecGrp> listSecGrp(@Nullable Long tenantId, SecGrpQueryDTO secGrp, PageRequest pageRequest);

    /**
     * 查询可快速创建的安全组
     *
     * @param tenantId 租户ID，可为空
     * @param secGrp   查询参数
     * @return 安全组列表
     */
    Page<SecGrp> listSecGrpForQuickCreate(@Nullable Long tenantId, @NotNull SecGrpQueryDTO secGrp, PageRequest pageRequest);

    /**
     * 查询安全组明细
     *
     * @param tenantId 租户ID
     * @param roleId   当前角色ID，可为空，默认取 self.roleId
     * @param secGrpId 安全组ID
     * @return 安全组明细信息，不为 null.
     * @throws io.choerodon.core.exception.CommonException 数据不存在将抛出此异常
     */
    @NotNull
    SecGrp querySecGrp(@Nullable Long tenantId, @Nullable Long roleId, @Nullable Long secGrpId);

    /**
     * 查询安全组明细
     *
     * @param tenantId 租户ID
     * @param secGrpId 安全组ID
     * @return 安全组明细信息，不为 null.
     * @throws io.choerodon.core.exception.CommonException 数据不存在将抛出此异常
     */
    @NotNull
    SecGrp querySecGrp(@Nullable Long tenantId, @Nullable Long secGrpId);

    /**
     * 查询安全组明细
     *
     * @param secGrpId 安全组ID
     * @return 安全组明细信息，不为 null.
     * @throws io.choerodon.core.exception.CommonException 数据不存在将抛出此异常
     */
    @NotNull
    SecGrp querySecGrp(@Nullable Long secGrpId);

    /**
     * 查询角色已经分配的安全组
     *
     * @param roleId      角色ID
     * @param dto         安全组过滤条件
     * @param pageRequest 分页条件
     */
    Page<SecGrp> listRoleAssignedSecGrp(Long roleId, SecGrpQueryDTO dto, PageRequest pageRequest);

    /**
     * 查询可为指定角色分配的安全组列表(排除已经分配的安全组)
     *
     * @param roleId      待分配安全组的角色ID
     * @param dto         安全组过滤条件
     * @param pageRequest 分页条件
     * @return 安全组列表
     */
    Page<SecGrp> listRoleAssignableSecGrp(Long roleId, SecGrpQueryDTO dto, PageRequest pageRequest);

    /**
     * 查询可为指定角色分配的安全组列表(排除已经分配的安全组)
     *
     * @param roleId    待分配安全组的角色ID
     * @param secGrpIds 可分配的安全组ID
     * @return 安全组列表
     */
    List<SecGrp> listRoleAssignableSecGrp(Long roleId, List<Long> secGrpIds);

    /**
     * 查询角色创建的安全组
     *
     * @param roleId 角色ID
     * @return 安全组列表
     */
    List<SecGrp> listRoleCreatedSecGrp(Long roleId);

    /**
     * 查询角色创建的安全组
     *
     * @param roleIds 角色IDs
     * @return 角色创建的安全组 key -> value === roleId -> SecGrpList
     */
    Map<Long, List<SecGrp>> queryRoleCreatedSecGrp(Set<Long> roleIds);

    /**
     * 查询安全组分配的角色
     *
     * @param secGrpId 安全组ID
     * @return 角色ID列表
     */
    Page<RoleSecGrpDTO> listSecGrpAssignedRole(Long secGrpId, RoleSecGrpDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询角色创建的安全组
     *
     * @param roles 角色s
     * @return 角色创建的安全组 key -> value === roleId -> SecGrpList
     */
    Map<Long, List<SecGrp>> queryRoleCreatedSecGrp(List<Role> roles);

    /**
     * 查询安全组分配的角色
     *
     * @param secGrpId 安全组ID
     * @return 角色ID列表
     */
    List<Role> listSecGrpAssignedRole(Long secGrpId);

    /**
     * 查询安全组可分配的角色分列表（排除已经分配的角色）
     *
     * @param secGrpId    分配的安全组ID
     * @param queryDTO    角色查询参数
     * @param pageRequest 分页参数
     * @return 角色列表
     */
    Page<RoleVO> listSecGrpAssignableRole(Long secGrpId, RoleSecGrpDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询子账户所有已经分配的安全组列表
     *
     * @param userId      用户ID
     * @param queryDTO    查询条件
     * @param pageRequest 分页信息
     */
    Page<SecGrp> listUserAssignedSecGrp(Long userId, SecGrpQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询用户可以分配的安全组
     *
     * @param userId      用户信息
     * @param queryDTO    查询条件
     * @param pageRequest 分页信息
     */
    Page<SecGrp> listUserAssignableSecGrp(Long userId, SecGrpQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询用户可以分配的安全组
     *
     * @param userId    用户信息
     * @param secGrpIds 安全组ID集合
     */
    List<SecGrp> listUserAssignableSecGrp(Long userId, List<Long> secGrpIds);

    /**
     * 查询直接分配了指定安全组的角色列表
     *
     * @param secGrpId 安全组ID
     */
    List<Role> selectDirectAssignedRolesOfSecGrp(Long secGrpId);

    /**
     * 查询发起回收有效角色ID的所有子孙角色（包含自己）自建的安全组
     *
     * @param revokeSgChildRoleId 发起回收有效角色ID
     */
    List<SecGrp> selectBuildBySelfGrpInRoleTree(Long revokeSgChildRoleId);

    /**
     * 查询角色有权限访问的安全组，包含角色自建的和被分配的安全组列表
     *
     * @param secGrpIds 安全组ID列表
     * @param roleId    上下文角色ID
     * @return 安全组列表
     */
    List<SecGrp> selectRoleAuthorizedSecGrp(List<Long> secGrpIds, Long roleId);


    /**
     * 通过安全组，查询角色Id列表
     *
     * @param secGrpId 安全组Id
     * @return 角色Id列表
     */
    List<Long> listRoleIdAssignedSecGrp(Long secGrpId);

    /**
     * 通过安全组,查询用户Id列表
     *
     * @param secGrpId 安全组Id
     * @return 用户Id列表
     */
    List<SecGrpAssign> listSecGrpAssign(Long secGrpId);
}
