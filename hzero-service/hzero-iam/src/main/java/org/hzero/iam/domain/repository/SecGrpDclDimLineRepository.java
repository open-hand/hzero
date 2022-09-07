package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDimLine;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;
import org.hzero.mybatis.base.BaseRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 安全组数据权限维度行资源库
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclDimLineRepository extends BaseRepository<SecGrpDclDimLine> {

    /**
     * 查询指定角色及其子角色自建的且绑定了对应的权限类型的安全组数据权限维度明细
     *
     * @param roleId           角色ID
     * @param secGrpDclDimKeys 权限维度KEY
     * @return 安全组数据权限维度明细
     */
    List<SecGrpDclDimDetailDTO> selectBuildDclDimDetailBindAuthTypeCodeInRoleAndSubRole(Long roleId, List<String> secGrpDclDimKeys);

    /**
     * 查询指定角色及其子角色被分配的的且绑定了对应的权限类型的安全组数据权限维度明细
     *
     * @param roleId           角色ID
     * @param secGrpDclDimKeys 权限维度KEY
     * @return 安全组数据权限维度明细
     */
    List<SecGrpDclDimDetailDTO> selectAssignedDclDimDetailBindAuthTypeCodeInRoleAndSubRole(Long roleId, List<String> secGrpDclDimKeys);

    /**
     * 查询安全组数据权限维度明细
     *
     * @param secGrpDclDimLineId 维度行Id
     * @return 数据权限维度明细
     */
    SecGrpDclDimDetailDTO selectDclDimDetailById(Long secGrpDclDimLineId);

    /**
     * 通过安全组ID和安全组数据权限ID查询
     *
     * @param secGrpId       安全组ID
     * @param secGrpDclDimId 安全组数据权限ID
     * @return 安全组数据权限行ID
     */
    List<SecGrpDclDimLine> select(Long secGrpId, Long secGrpDclDimId);

    /**
     * 查询维度行
     *
     * @param secGrpId       安全组ID
     * @param secGrpDclDimId 维度头ID
     */
    List<SecGrpDclDimLine> listSecGrpDimLine(Long secGrpId, Long secGrpDclDimId);

    /**
     * 筛选出角色分配的安全中，在提供的维度值中没有的维度值
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param authDocTypeId   单据维度
     * @param authScopeCode   单据维度范围
     * @param authTypeCodes   单据类型
     * @return 该角色需回收的维度行
     */
    Set<String> listRoleNotIncludedAuthType(Long roleId, Long excludeSecGrpId, Long authDocTypeId,
                                            String authScopeCode, Set<String> authTypeCodes);

    /**
     * 筛选出用户分配的安全中，在提供的维度值中没有的维度值
     *
     * @param userId          用户ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param authDocTypeId   单据维度
     * @param authScopeCode   单据维度范围
     * @param authTypeCodes   单据类型
     * @return 该角色需回收的维度行
     */
    Set<String> listUserNotIncludedAuthType(Long userId, Long excludeSecGrpId, Long authDocTypeId,
                                            String authScopeCode, Set<String> authTypeCodes);

    /**
     * 查询角色分配的安全中，在提供的维度值中包含的维度值
     *
     * @param roleIds             角色IDs
     * @param excludeSecGrpId     排除的安全组ID
     * @param authDocTypeId       单据维度
     * @param authScopeCode       单据维度范围
     * @param assignAuthTypeCodes 分配的权限类型码
     * @return 该角色需回收的维度行 key -> value === roleId -> authTypeCodes
     */
    Map<Long, Set<String>> queryRoleIncludedAuthType(Set<Long> roleIds, Long excludeSecGrpId, Long authDocTypeId,
                                                     String authScopeCode, Set<String> assignAuthTypeCodes);

    /**
     * 筛选出角色分配的安全中，在提供的维度值中包含的维度值
     *
     * @param roles               角色s
     * @param excludeSecGrpId     排除的安全组ID
     * @param authDocTypeId       单据维度
     * @param authScopeCode       单据维度范围
     * @param assignAuthTypeCodes 分配的权限类型码
     * @return 该角色需回收的维度行 key -> value === roleId -> authTypeCodes
     */
    Map<Long, Set<String>> queryRoleIncludedAuthType(List<Role> roles, Long excludeSecGrpId, Long authDocTypeId,
                                                     String authScopeCode, Set<String> assignAuthTypeCodes);

    /**
     * 查询角色创建的安全组中，在提供的维度值中没有的安全组
     *
     * @param roleId        角色ID
     * @param authDocTypeId 单据维度
     * @param authScopeCode 单据维度范围
     * @param authTypeCodes 单据类型
     * @return 满足条件的安全组
     */
    List<SecGrp> listRoleIncludedAuthTypeSecGrp(Long roleId, Long authDocTypeId,
                                                String authScopeCode, Set<String> authTypeCodes);

    /**
     * 根据条件查询已被回收的数据权限维度行的授权类型码
     *
     * @param secGrpId      安全组ID
     * @param authDocTypeId 文档ID
     * @param authScopeCode 维度范围
     * @param authTypeCodes 权限类型编码
     * @return 满足条件的数据权限维度行id集合
     */
    Set<String> selectRecycleAuthTypeCodes(Long secGrpId, Long authDocTypeId,
                                           String authScopeCode, Set<String> authTypeCodes);

    /**
     * 删除这些安全组中的维度行
     *
     * @param secGrpIds     安全组ID
     * @param authDocTypeId 单据维度
     * @param authScopeCode 维度范围
     * @param authTypeCodes 单据类型
     */
    void batchRemoveSecGrpDimLine(List<Long> secGrpIds, Long authDocTypeId,
                                  String authScopeCode, Set<String> authTypeCodes);

    /**
     * 更新这些安全组中的维度行
     *
     * @param secGrpIds     安全组ID
     * @param authDocTypeId 单据维度
     * @param authScopeCode 维度范围
     * @param authTypeCodes 单据类型
     */
    void batchUpdateSecGrpDimLine(List<Long> secGrpIds, Long authDocTypeId,
                                  String authScopeCode, Set<String> authTypeCodes);

    /**
     * 查询Dim中已包含的指定授权码的DimLine
     *
     * @param secGrpDclDimId       dimId
     * @param notIncludedAuthTypes 指定的授权码
     * @return Dim中已包含的DimLine key -> value === AuthTypeCode -> DimLine
     */
    Map<String, SecGrpDclDimLine> listDimIncludedDimLine(@Nonnull Long secGrpDclDimId,
                                                         @Nonnull Set<String> notIncludedAuthTypes);

    /**
     * 根据安全组DimId和分配类型码对安全组DimLine计数
     *
     * @param secGrpDclDimId 安全组DimId
     * @param assignTypeCode 分配类型Code
     * @return 计数结果
     */
    int countDimLineByDimIdAndAssignTypeCode(@Nonnull Long secGrpDclDimId, @Nonnull String assignTypeCode);
}
