package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.SecGrpDclDimLineDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDimLine;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;

import java.util.List;
import java.util.Set;

/**
 * 安全组数据权限维度行Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclDimLineMapper extends BaseMapper<SecGrpDclDimLine> {
    /**
     * 查询安全组下的数据权限维度行
     *
     * @param secGrpId   安全组ID
     * @param docTypeIds 单据权限类型ID
     * @return
     */
    List<SecGrpDclDimLineDTO> listSecGrpDclDimLine(@Param("secGrpId") Long secGrpId,
                                                   @Param("docTypeIds") List<Long> docTypeIds);


    /**
     * 查询指定角色及其子角色自建的且绑定了对应的权限类型的安全组数据权限维度明细
     *
     * @param roleId           角色ID
     * @param secGrpDclDimKeys 权限维度KEY
     * @return 安全组数据权限维度明细
     */
    List<SecGrpDclDimDetailDTO> selectBuildDclDimDetailBindAuthTypeCodeInRoleAndSubRole(@Param("roleId") Long roleId,
                                                                                        @Param("secGrpDclDimKeys") List<String> secGrpDclDimKeys);

    /**
     * 查询指定角色及其子角色被分配的的且绑定了对应的权限类型的安全组数据权限维度明细
     *
     * @param roleId           角色ID
     * @param secGrpDclDimKeys 权限维度KEY
     * @return 安全组数据权限维度明细
     */
    List<SecGrpDclDimDetailDTO> selectAssignedDclDimDetailBindAuthTypeCodeInRoleAndSubRole(@Param("roleId") Long roleId,
                                                                                           @Param("secGrpDclDimKeys") List<String> secGrpDclDimKeys);

    /**
     * 查询安全组数据权限维度明细
     *
     * @param secGrpDclDimLineId 维度行Id
     * @return 数据权限维度明细
     */
    SecGrpDclDimDetailDTO selectDclDimDetailById(@Param("secGrpDclDimLineId") Long secGrpDclDimLineId);

    /**
     * 根据主键批量删除
     *
     * @param secGrpDimIds DIM ID
     */
    void deleteDimLine(@Param("secGrpDimIds") List<Long> secGrpDimIds);


    /**
     * 查询角色分配的安全组中包含的维度值
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组
     * @param authDocTypeId   单据类型
     * @param authScopeCode   单据维度范围
     * @param authTypeCodes   单据维度
     * @return 满足条件的维度行
     */
    List<String> selectRoleSecGrpIncludedDimLine(@Param("roleId") Long roleId,
                                                 @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                 @Param("authDocTypeId") Long authDocTypeId,
                                                 @Param("authScopeCode") String authScopeCode,
                                                 @Param("authTypeCodes") Set<String> authTypeCodes);

    /**
     * 查询用户分配的安全组中包含的维度值
     *
     * @param userId          用户ID
     * @param excludeSecGrpId 排除的安全组
     * @param authDocTypeId   单据类型
     * @param authScopeCode   单据维度范围
     * @param authTypeCodes   单据维度
     * @return 满足条件的维度行
     */
    List<SecGrp> selectUserSecGrpIncludedDimLine(@Param("userId") Long userId,
                                                 @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                 @Param("authDocTypeId") Long authDocTypeId,
                                                 @Param("authScopeCode") String authScopeCode,
                                                 @Param("authTypeCodes") Set<String> authTypeCodes);

    /**
     * 查询角色分配的安全组中包含的维度值
     *
     * @param roleIds         角色IDs
     * @param excludeSecGrpId 排除的安全组
     * @param authDocTypeId   单据类型
     * @param authScopeCode   单据维度范围
     * @param authTypeCodes   单据维度
     * @return 满足条件的维度行
     */
    List<SecGrpDclDimLine> queryRoleIncludedDimLines(@Param("roleIds") Set<Long> roleIds,
                                                     @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                     @Param("authDocTypeId") Long authDocTypeId,
                                                     @Param("authScopeCode") String authScopeCode,
                                                     @Param("authTypeCodes") Set<String> authTypeCodes);

    /**
     * 查询角色创建的安全组中，需要处理回收维度行的安全组
     *
     * @param roleId        角色ID
     * @param authDocTypeId 单据类型
     * @param authScopeCode 维度范围
     * @param authTypeCodes 单据维度
     * @return 满足条件的查询结果
     */
    List<SecGrp> selectRoleIncludedDimLineSecGrp(@Param("roleId") Long roleId,
                                                 @Param("authDocTypeId") Long authDocTypeId,
                                                 @Param("authScopeCode") String authScopeCode,
                                                 @Param("authTypeCodes") Set<String> authTypeCodes);

    /**
     * 根据条件查询数据权限维度行的数据
     *
     * @param secGrpIds      安全组IDs
     * @param authDocTypeId  文档ID
     * @param authScopeCode  维度范围
     * @param authTypeCodes  权限类型编码
     * @param assignTypeCode 分配类型编码
     * @return 满足条件的数据权限维度行id集合
     */
    List<Long> selectDimLineId(@Param("secGrpIds") List<Long> secGrpIds,
                               @Param("authDocTypeId") Long authDocTypeId,
                               @Param("authScopeCode") String authScopeCode,
                               @Param("authTypeCodes") Set<String> authTypeCodes,
                               @Param("assignTypeCode") String assignTypeCode);

    /**
     * 根据条件查询数据权限维度行的授权类型码
     *
     * @param secGrpId       安全组ID
     * @param authDocTypeId  文档ID
     * @param authScopeCode  维度范围
     * @param authTypeCodes  权限类型编码
     * @param assignTypeCode 分配类型编码
     * @return 满足条件的数据权限维度行id集合
     */
    Set<String> selectAuthTypeCodes(@Param("secGrpId") Long secGrpId,
                                    @Param("authDocTypeId") Long authDocTypeId,
                                    @Param("authScopeCode") String authScopeCode,
                                    @Param("authTypeCodes") Set<String> authTypeCodes,
                                    @Param("assignTypeCode") String assignTypeCode);

    /**
     * 删除的安全组中的维度对应的行
     *
     * @param dimLineIds 待删除的数据id
     */
    void batchDeleteBySql(@Param("dimLineIds") List<Long> dimLineIds);

    /**
     * 更新安全组中的维度对应的行，将分配类型更新为指定的分配类型编码
     *
     * @param dimLineIds     待更新的数据id
     * @param assignTypeCode 指定的待更新的分配类型编码
     */
    void batchUpdateBySql(@Param("dimLineIds") List<Long> dimLineIds,
                          @Param("assignTypeCode") String assignTypeCode);
}
