package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.RoleSecGrpDTO;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAssign;


/**
 * 安全组Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpMapper extends BaseMapper<SecGrp> {

    /**
     * 查询安全组详细信息
     *
     * @param tenantId 租户ID
     * @param secGrpId 安全组ID
     * @return SecGrp
     */
    SecGrp selectDetail(@Param("tenantId") Long tenantId, @Param("secGrpId") Long secGrpId, @Param("currentRoleId") Long roleId);

    /**
     * 条件查询自己创建建安全组
     *
     * @param secGrp 查询参数
     * @return 安全组列表
     */
    List<SecGrp> selectRoleCreatedSecGrp(SecGrpQueryDTO secGrp);

    /**
     * 条件查询子角色创建安全组
     *
     * @param secGrp 查询参数
     * @return 安全组列表
     */
    List<SecGrp> selectChildCreatedSecGrp(SecGrpQueryDTO secGrp);

    /**
     * 查询角色已分配的安全组
     *
     * @param dto 安全组过滤条件
     */
    List<SecGrp> selectRoleAssignedSecGrp(SecGrpQueryDTO dto);

    /**
     * 查询可为指定角色分配的安全组列表
     *
     * @param roleId 待分配安全组的角色id
     * @param query  安全组过滤条件
     * @return 返回
     */
    List<SecGrp> selectRoleAssignableSecGrp(@Param("roleId") Long roleId,
                                            @Param("query") SecGrpQueryDTO query);

    /**
     * 查询分配了指定安全组的角色ID列表
     *
     * @param secGrpId 安全组ID
     * @return 角色ID列表
     */
    List<RoleSecGrpDTO> selectSecGrpAssignedRole(@Param("secGrpId") Long secGrpId,
                                                 @Param("query") RoleSecGrpDTO queryDTO);

    /**
     * 回收: 查询发起回收有效角色ID的所有子孙角色（包含自己）自建的安全组
     *
     * @param revokeSgChildRoleId 发起回收有效角色ID
     */
    List<SecGrp> selectBuildBySelfGrpInRoleTree(@Param("revokeSgChildRoleId") Long revokeSgChildRoleId);

    /**
     * 查询角色自建的和被分配的安全组列表
     *
     * @param secGrpIds 安全组ID列表
     * @param secGrp    过滤参数
     * @return 安全组列表
     */
    List<SecGrp> selectAuthorizedSecGrp(@Param("secGrpIds") List<Long> secGrpIds, @Param("query") SecGrpQueryDTO secGrp);

    /**
     * 查询子账户已分配的安全组列表
     *
     * @param userId 子账户Id
     */
    List<SecGrp> selectUserAssignedSecGrp(@Param("userId") Long userId, @Param("query") SecGrpQueryDTO secGrp);

    /**
     * 查询用户可以分配的安全组
     *
     * @param userId   用户Id
     * @param queryDTO 过滤条件
     */
    List<SecGrp> listUserAssignableSecGrp(@Param("userId") Long userId, @Param("query") SecGrpQueryDTO queryDTO);

    /**
     * 列出角色Id，通过安全组Id
     *
     * @param secGrpId 安全组Id
     * @return 角色Id
     */
    List<Long> listRoleIdAssignedSecGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 通过安全组Id,查询用户Id列表
     *
     * @param secGrpId 安全组Id
     * @return 用户Id列表
     */
    List<SecGrpAssign> listSecGrpAssign(@Param("secGrpId") Long secGrpId);
}
