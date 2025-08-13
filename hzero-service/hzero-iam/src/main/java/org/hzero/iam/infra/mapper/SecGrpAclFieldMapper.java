package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.SecGrpAclFieldDTO;
import org.hzero.iam.domain.entity.SecGrpAclField;

/**
 * 安全组字段权限Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclFieldMapper extends BaseMapper<SecGrpAclField> {
    /**
     * 安全组API对应的字段
     *
     * @param secGrpAclFieldDTO 安全组字段查询DTO
     */
    List<SecGrpAclField> selectSecGrpApiField(SecGrpAclFieldDTO secGrpAclFieldDTO);

    /**
     * 查询分配给指定角色的安全组权限字段，并标志屏蔽状态
     *
     * @param roleId            角色ID
     * @param secGrpAclFieldDTO 过滤参数
     */
    List<SecGrpAclField> selectRoleSecGrpAssignedApiField(@Param("roleId") Long roleId,
                                                          @Param("query") SecGrpAclFieldDTO secGrpAclFieldDTO);

    /**
     * 查询角色被被限制的安全组屏蔽字段
     *
     * @param roleId 角色ID
     * @return 安全组屏蔽字段
     */
    List<SecGrpAclField> selectRoleSecGrpField(@Param("roleId") Long roleId);

    /**
     * 查询安全组下能自我管理的，不受父级限制的安全组访问字段
     *
     * @param secGrpId 安全组ID
     * @return
     */
    List<SecGrpAclField> selectSelfManagementFieldInGrp(@Param("secGrpId") Long secGrpId);


    /**
     * 查询指定角色及其子角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId   角色ID
     * @param fieldIds 字段ID列表
     * @return
     */
    List<SecGrpAclField> selectBuildAclFieldBindFieldIdInRoleAndSubRole(@Param("roleId") Long roleId, @Param("fieldIds") List<Long> fieldIds);

    /**
     * 查询指定角色及其子角色被分配的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId   角色ID
     * @param fieldIds 字段ID列表
     * @return
     */
    List<SecGrpAclField> selectAssignedAclFieldBindFieldIdInRoleAndSubRole(@Param("roleId") Long roleId, @Param("fieldIds") List<Long> fieldIds);

    /**
     * 安全组快速创建-查询安全组列表下分配的访问字段权限列表
     *
     * @param secGrpIds 安全组ID列表
     * @return 访问字段列表
     */
    List<SecGrpAclField> selectSecGrpFields(@Param("secGrpIds") List<Long> secGrpIds,
                                            @Param("secGrpFieldIds") Set<Long> secGrpFieldIds);

    /**
     * 查询字段权限
     *
     * @param secGrpAclFieldDTO 过滤条件
     * @return
     */
    List<SecGrpAclField> selectAssignedSecGrpAclFieldForUserInGrp(@Param("secGrpAclFieldDTO") SecGrpAclFieldDTO secGrpAclFieldDTO);

    /**
     * 查询角色分配的安全组中，除了指定的安全组，其他安全组中包含的字段权限对象
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 需要排除的安全组
     * @param fieldIds        字段权限Ids
     * @return 满足条件的字段权限数据
     */
    List<SecGrpAclField> selectRoleSecGrpIncludedField(@Param("roleId") Long roleId,
                                                       @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                       @Param("fieldIds") Set<Long> fieldIds);

    /**
     * 查询用户分配的安全组中，除了指定的安全组，其他安全组中包含的字段权限对象
     *
     * @param userId          用户ID
     * @param excludeSecGrpId 需要排除的安全组
     * @param fieldIds        字段权限Ids
     * @return 满足条件的字段权限数据
     */
    List<SecGrpAclField> selectUserSecGrpIncludedField(@Param("userId") Long userId,
                                                       @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                       @Param("fieldIds") Set<Long> fieldIds);

    void batchDeleteBySql(@Param("secGrpIds") List<Long> secGrpIds,
                          @Param("fieldIds") Set<Long> fieldIds,
                          @Param("autoAssignFlag") Integer autoAssignFlag);

    void batchInsertBySql(List<SecGrpAclField> fields);

    /**
     * 通过安全组Id查询安全组字段权限列表
     *
     * @param secGrpIds 安全组Id
     * @return 安全组字段权限列表
     */
    List<SecGrpAclField> listSecGrpAclField(@Param("secGrpIds") List<Long> secGrpIds);
}
