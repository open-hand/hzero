package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.api.dto.SecGrpDclDimLineDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;

import java.util.List;
import java.util.Set;

/**
 * 安全组数据权限维度Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclDimMapper extends BaseMapper<SecGrpDclDim> {

    /**
     * 查询安全组数据权限维度信息
     *
     * @param queryDTO 查询参数 secGrpId 和tenantID是必填参数 docTypeName是模糊查询参数
     */
    List<SecGrpDclDimDTO> listSecGrpAssignableDim(SecGrpDclDimDTO queryDTO);

    /**
     * 查询安全组已分配的数据权限维度信息
     */
    List<SecGrpDclDimDTO> listSecGrpAssignedDim(SecGrpDclDimDTO queryDTO);

    /**
     * 查询安全组可分配的维度行
     *
     * @param secGrpId   安全组ID
     * @param docTypeIds docTypeIds
     * @return 安全组权限维度行数据
     */
    List<SecGrpDclDimLineDTO> listSecGrpAssignableDimLine(@Param("secGrpId") Long secGrpId,
                                                          @Param("docTypeIds") Set<Long> docTypeIds);

    /**
     * 查询安全组已分配的维度行
     *
     * @param secGrpId   安全组ID
     * @param docTypeIds docTypeIds
     * @return 安全组权限维度行数据
     */
    List<SecGrpDclDimLineDTO> listSecGrpAssignedDimLine(@Param("secGrpId") Long secGrpId,
                                                        @Param("docTypeIds") Set<Long> docTypeIds);

    /**
     * 查询角色单据类型定义维度分配列表
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据类型定义维度分配列表
     */
    Set<String> selectSecGrpAssignedAuthTypeCode(@Param("secGrpId") Long secGrpId);


    /**
     * 查询角色下的单据维度明细（被分配的）
     *
     * @param roleId 角色ID
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> selectRoleSecGrpDim(@Param("roleId") Long roleId);

    /**
     * 查询安全组下能自我管理的，不受父级限制的单据维度
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> selectSelfManagementDimDetailInGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 查询安全组下受父级限制的单据维度
     *
     * @param secGrpId 安全组ID
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> selectAssignedDimDetailInGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 查询自建安全组下所有的单据维度明细
     *
     * @param secGrpIds 安全组ID列表
     * @return 安全组单据维度
     */
    List<SecGrpDclDimDetailDTO> selectSecGrpDim(@Param("secGrpIds") List<Long> secGrpIds);

    /**
     * 查询分配了当前安全组的角色被分配的其他安全组中对指定单据类型的维度范围配置与当前安全组中不同的维度信息
     *
     * @param secGrpId      安全组ID
     * @param docTypeId     单据类型ID
     * @param authScopeCode 维度范围
     * @return
     */
    List<SecGrpDclDim> selectExtraDimInAssignedRole(@Param("secGrpId") Long secGrpId,
                                                    @Param("docTypeId") Long docTypeId,
                                                    @Param("authScopeCode") String authScopeCode);

    /**
     * 查询分配了指定安全组的角色的自建权限维度
     *
     * @param secGrpId  安全组ID
     * @param docTypeId 单据类型ID
     * @return
     */
    List<SecGrpDclDim> selectBuildDimInAssignedRoleAndSubRole(@Param("secGrpId") Long secGrpId,
                                                              @Param("docTypeId") Long docTypeId);

    /**
     * 查询指定角色被分配的不同于指定安全组中的数据权限维度（用于分配校验）
     *
     * @param secGrpId 安全组ID
     * @param roleId   角色
     * @return
     */
    List<SecGrpDclDim> selectDifferentAssignedDimInRoleFromSecGrp(@Param("secGrpId") Long secGrpId,
                                                                  @Param("roleId") Long roleId);

    /**
     * 通过维度范围唯一键查询安全组维度
     *
     * @param secGrpId              安全组ID
     * @param authDocTypeUniqueKeys 数据维度唯一键s
     * @return 查询结果
     */
    List<SecGrpDclDim> listSecGrpDimByUniqueKeys(@Param("secGrpId") Long secGrpId,
                                                 @Param("uniqueKeys") Set<String> authDocTypeUniqueKeys);

    /**
     * 查询角色其他分配的安全组是否包含指定维度范围的单据
     *
     * @param roleId          角色ID
     * @param excludeSecGrpId 排除的安全组ID
     * @param authDocTypeId   单据ID
     * @param authScopeCode   维度范围
     * @return 维度范围ID集合
     */
    Set<Long> selectRoleIncludedAuthDoc(@Param("roleId") Long roleId,
                                        @Param("excludeSecGrpId") Long excludeSecGrpId,
                                        @Param("authDocTypeId") Long authDocTypeId,
                                        @Param("authScopeCode") String authScopeCode);

    /**
     * 查询需处理维度范围的安全组
     *
     * @param roleId        角色ID
     * @param authDocTypeId 单据类型ID
     * @param authScopeCode 授权范围Code
     * @return 满足条件的安全组
     */
    List<SecGrp> listRoleIncludedSecGrp(@Param("roleId") Long roleId,
                                        @Param("authDocTypeId") Long authDocTypeId,
                                        @Param("authScopeCode") String authScopeCode);

    /**
     * 查询维度行为空的维度ids
     *
     * @param secGrpIds      安全组IDs
     * @param authDocTypeId  单据类型ID
     * @param authScopeCode  维度范围
     * @param assignTypeCode 分配类型编码
     * @return 维度IDs
     */
    Set<Long> selectDimId(@Param("secGrpIds") List<Long> secGrpIds,
                          @Param("authDocTypeId") Long authDocTypeId,
                          @Param("authScopeCode") String authScopeCode,
                          @Param("assignTypeCode") String assignTypeCode);

    /**
     * 批量删除指定安全组中的空维度
     *
     * @param dimIds 维度ID
     */
    void batchDeleteBySql(@Param("dimIds") Set<Long> dimIds);

    /**
     * 批量更新指定安全组中的空维度
     *
     * @param dimIds         维度ID
     * @param assignTypeCode 分配类型编码
     */
    void batchUpdateBySql(@Param("dimIds") Set<Long> dimIds,
                          @Param("assignTypeCode") String assignTypeCode);

    /**
     * 根据条件对属于自己创建的权限维度计数
     *
     * @param secGrpId          安全组
     * @param roleId            当前角色ID
     * @param authorityTypeCode 授权码
     * @return 存在就返回 1 不存在就返回 0
     */
    int countSelfManagementDim(@Param("secGrpId") Long secGrpId,
                               @Param("roleId") Long roleId,
                               @Param("authorityTypeCode") String authorityTypeCode);
}
