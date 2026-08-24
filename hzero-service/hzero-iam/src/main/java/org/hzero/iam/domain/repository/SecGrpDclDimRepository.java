package org.hzero.iam.domain.repository;

import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;
import org.hzero.mybatis.base.BaseRepository;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 安全组数据权限维度资源库
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclDimRepository extends BaseRepository<SecGrpDclDim> {
    /**
     * 查询安全组数据权限维度信息，包含维度行
     *
     * @param tenantId    租户ID
     * @param secGrpId    安全组ID
     * @param pageRequest 分页参数
     */
    Page<SecGrpDclDimDTO> listSecGrpAssignableDim(Long tenantId, Long secGrpId, SecGrpDclDimDTO secGrpDclDim, PageRequest pageRequest);

    /**
     * 查询安全组已分配的数据权限维度信息，包含维度行
     *
     * @param tenantId    租户ID
     * @param secGrpId    安全组ID
     * @param pageRequest 分页参数
     */
    Page<SecGrpDclDimDTO> listSecGrpAssignedDim(Long tenantId, Long secGrpId, SecGrpDclDimDTO secGrpDclDim, PageRequest pageRequest);

    /**
     * 查询安全组下分配了权限的单据类型定义维度分配列表
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据类型定义维度分配列表
     */
    Set<String> listSecGrpAssignedAuthTypeCode(long secGrpId);

    /**
     * 查询角色下的单据维度明细（被分配的）
     *
     * @param roleId 角色ID
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> listRoleSecGrpDim(@NotNull Long roleId);

    /**
     * 通过维度范围唯一键查询安全组维度
     *
     * @param secGrpId              安全组ID
     * @param authDocTypeUniqueKeys 数据维度唯一键s
     * @return 查询结果
     */
    List<SecGrpDclDim> listSecGrpDimByUniqueKeys(Long secGrpId, Set<String> authDocTypeUniqueKeys);

    /**
     * 查询自建安全组下能自我管理的，不受父级限制的单据维度
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> selectSelfManagementDimDetailInGrp(Long secGrpId);

    /**
     * 查询自建安全组下所有的单据维度明细
     *
     * @param secGrpIds 安全组ID列表
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> listSecGrpAssignableDim(List<Long> secGrpIds);

    /**
     * 判断安全组下指定的权限类型是否是不受父级限制的单据维度
     *
     * @param secGrpId          安全组ID
     * @param roleId            当前角色ID
     * @param authorityTypeCode 权限类型编码
     * @return 是否是当前角色创建的Dim  true 是 false 否
     */
    Boolean isSelfManagementDim(Long secGrpId, Long roleId, String authorityTypeCode);

    /**
     * 查询指定角色被分配的不同于指定安全组中的数据权限维度（用于分配校验）
     *
     * @param secGrpId 安全组ID
     * @param roleId   角色
     * @return
     */
    List<SecGrpDclDim> selectDifferentAssignedDimInRoleFromSecGrp(Long secGrpId, Long roleId);

    /**
     * 查询安全组的数据权限维度
     *
     * @param secGrpId 安全组ID列表
     * @return 安全组数据权限维度
     */
    List<SecGrpDclDim> selectBySecGrpId(Long secGrpId);

    /**
     * 判断角色其他分配的安全组是否包含指定维度范围的单据
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param authDocTypeId   单据ID
     * @param authScopeCode   维度范围
     * @return 维度范围ID集合
     */
    boolean isRoleIncludedAuthDoc(Long roleId, Long excludeSecGrpId, Long authDocTypeId, String authScopeCode);

    /**
     * 查询需处理维度范围的安全组
     *
     * @param roleId        角色ID
     * @param authDocTypeId 单据类型ID
     * @param authScopeCode 授权范围Code
     * @return 满足条件的安全组
     */
    List<SecGrp> listRoleIncludedSecGrp(Long roleId, Long authDocTypeId, String authScopeCode);

    /**
     * 批量删除指定安全组中的维度
     *
     * @param secGrpIds     安全组ID
     * @param authDocTypeId 单据维度
     * @param authScopeCode 维度范围
     */
    void batchDeleteEmptySecGrpDim(List<Long> secGrpIds, Long authDocTypeId, String authScopeCode);

    /**
     * 批量更新指定安全组中的维度
     *
     * @param secGrpIds     安全组ID
     * @param authDocTypeId 单据维度
     * @param authScopeCode 维度范围
     */
    void batchUpdateEmptySecGrpDim(List<Long> secGrpIds, Long authDocTypeId, String authScopeCode);

    /**
     * 通过数据权限维度唯一键查询数据权限维度范围对象
     *
     * @param authDocTypeId 单据维度
     * @param authScopeCode 维度范围
     * @return 数据权限维度范围对象
     */
    SecGrpDclDim selectDimByUniqueKey(@Nonnull Long authDocTypeId, @Nonnull String authScopeCode);

    /**
     * 查询角色创建的安全组对应的dim对象
     *
     * @param roleCreatedSecGrpIds 角色创建的安全组IDs
     * @param authDocTypeId        单据维度
     * @param authScopeCode        维度范围
     * @return 角色安全组对应的dim key -> value === SecGrpId -> SecGrpDclDim
     */
    Map<Long, SecGrpDclDim> listRoleSecGrpIncludedDim(Set<Long> roleCreatedSecGrpIds, Long authDocTypeId, String authScopeCode);

    /**
     * 查询角色创建的安全组对应的dim对象
     *
     * @param roleCreatedSecGrps 角色创建的安全组s
     * @param authDocTypeId      单据维度
     * @param authScopeCode      维度范围
     * @return 角色安全组对应的dim key -> value === SecGrpId -> SecGrpDclDim
     */
    Map<Long, SecGrpDclDim> listRoleSecGrpIncludedDim(List<SecGrp> roleCreatedSecGrps, Long authDocTypeId, String authScopeCode);
}
