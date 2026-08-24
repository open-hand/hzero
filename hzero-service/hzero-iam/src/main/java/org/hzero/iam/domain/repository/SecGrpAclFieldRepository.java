package org.hzero.iam.domain.repository;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.SecGrpAclApiDTO;
import org.hzero.iam.api.dto.SecGrpAclFieldDTO;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 安全组字段权限资源库
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclFieldRepository extends BaseRepository<SecGrpAclField> {

    /**
     * 查询安全组下分配的访问权限绑定API信息
     *
     * @param tenantId    租户ID
     * @param secGrpId    安全组ID
     * @param queryDTO    安全组API查询DTO
     * @param pageRequest 分页参数
     * @return API信息
     */
    Page<Permission> listAssignableSecGrpApi(Long tenantId, Long secGrpId, SecGrpAclApiDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询安全组下已分配的访问权限绑定API信息
     *
     * @param tenantId    租户ID
     * @param secGrpId    安全组ID
     * @param queryDTO    安全组API查询DTO
     * @param pageRequest 分页参数
     * @return API信息
     */
    Page<Permission> listAssignedSecGrpApi(Long tenantId, Long secGrpId, SecGrpAclApiDTO queryDTO, PageRequest pageRequest);

    /**
     * 安全组API对应的字段
     *
     * @param secGrpId          安全组ID
     * @param permissionId      权限ID
     * @param secGrpAclFieldDTO 安全组字段查询DTO
     * @param pageRequest       分页参数
     */
    Page<SecGrpAclField> listSecGrpApiField(Long tenantId, Long secGrpId, Long permissionId, SecGrpAclFieldDTO secGrpAclFieldDTO, PageRequest pageRequest);

    /**
     * 查询分配给指定角色的安全组权限字段，并标志屏蔽状态
     *
     * @param roleId       角色ID
     * @param secGrpId     安全组ID
     * @param permissionId 权限ID
     * @param queryDTO     过滤参数
     * @param pageRequest  分页参数
     */
    Page<SecGrpAclField> listRoleAssignedApiField(Long roleId, Long secGrpId, Long permissionId, SecGrpAclFieldDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询角色被限制的安全组访问字段
     *
     * @param roleId 角色ID
     */
    List<SecGrpAclField> listRoleSecGrpField(Long roleId);

    /**
     * 查询安全组下能自我管理的，不受父级限制的安全组访问字段
     *
     * @param secGrpId 安全组ID
     * @return 访问字段列表
     */
    List<SecGrpAclField> selectSelfManagementFieldInGrp(Long secGrpId);

    /**
     * 查询指定角色及其子角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId   角色ID
     * @param fieldIds 字段ID列表
     * @return
     */
    List<SecGrpAclField> selectBuildAclFieldBindFieldIdInRoleAndSubRole(Long roleId, List<Long> fieldIds);

    /**
     * 查询指定角色及其子角色被分配的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId   角色ID
     * @param fieldIds 字段ID列表
     */
    List<SecGrpAclField> selectAssignedAclFieldBindFieldIdInRoleAndSubRole(Long roleId, List<Long> fieldIds);

    /**
     * 安全组快速创建-查询安全组列表下分配的访问字段权限列表
     *
     * @param secGrpIds 安全组ID列表
     * @return 访问字段列表
     */
    List<SecGrpAclField> listSecGrpFields(List<Long> secGrpIds, Set<Long> secGrpFieldIds);

    /**
     * 通过安全组Id和租户ID查询字段权限
     *
     * @param secGrpId 安全组ID
     * @param tenantId 租户ID
     * @return 安全组字段权限列表
     */
    List<SecGrpAclField> select(Long secGrpId, Long tenantId);

    /**
     * 查询角色分配的安全组中，排除此安全组后，其它安全组中不包含这些字段的字段
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param fields          字段权限
     * @return 排除此安全组的其它分配的安全组的字段权限
     */
    List<SecGrpAclField> listRoleNotIncludedFields(Long roleId, Long excludeSecGrpId, List<SecGrpAclField> fields);

    /**
     * 查询用户分配的安全组中，排除此安全组后，其它安全组中不包含这些字段的字段
     *
     * @param userId          用户ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param fields          字段权限
     * @return 排除此安全组的其它分配的安全组的字段权限
     */
    List<SecGrpAclField> listUserNotIncludedFields(Long userId, Long excludeSecGrpId, List<SecGrpAclField> fields);

    /**
     * 批量移除字段权限
     *
     * @param secGrpIds 安全组
     * @param fields    字段
     */
    void batchRemove(List<Long> secGrpIds, List<SecGrpAclField> fields, Integer autoAssignFlag);

    /**
     * 批量插入
     */
    void batchAdd(List<SecGrpAclField> fields);

    /**
     * 通过安全组查询安全组字段权限
     *
     * @param secGrpIds 安全组列表
     * @return 安全组字段权限列表
     */
    List<SecGrpAclField> listSecGrpAclField(List<Long> secGrpIds);

    /**
     * 查询指定安全组所有的字段访问权限
     *
     * @param secGrpId 安全组ID
     * @return 字段访问权限
     */
    List<SecGrpAclField> listSecGrpAclField(@Nonnull Long secGrpId);
}
