package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDclLine;

/**
 * 安全组数据权限行Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclLineMapper extends BaseMapper<SecGrpDclLine> {
    /**
     * 模糊查询安全组数据指定权限的权限行
     *
     * @return 安全组数据权限行
     */
    List<SecGrpDclLine> listSecGrpDclLine(SecGrpDclQueryDTO query);

    /**
     * 查询分配给指定角色的安全组单据权限行，并标志屏蔽状态
     */
    List<SecGrpDclLine> listRoleSecGrpDcl(SecGrpDclQueryDTO query);

    /**
     * 查询可为当前安全组分配的单据权限行列表（会排除已经拥有的）
     *
     * @return 安全组数据权限行
     */
    List<SecGrpDclLine> selectAssignableSecGrpDclLine(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的数据组权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 数据源列表
     */
    List<SecGrpDclLine> selectGlobalAssignableDataGroup(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的数据源权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 数据源列表
     */
    List<SecGrpDclLine> selectGlobalAssignableDatasource(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的值集视图权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 角色单据权限管理行表
     */
    List<SecGrpDclLine> selectGlobalAssignableLovView(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的值集权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 角色单据权限管理行表
     */
    List<SecGrpDclLine> selectGlobalAssignableLov(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的采购员权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 角色单据权限管理行表
     */
    List<SecGrpDclLine> selectGlobalAssignablePurAgent(SecGrpDclQueryDTO query);

    /**
     * 查询可被分配的采购组织权限（限制为当前租户和平台租户），并过滤已经分配
     *
     * @return 角色单据权限管理行表
     */
    List<SecGrpDclLine> selectGlobalAssignablePurOrg(SecGrpDclQueryDTO query);

    /**
     * 查询自建安全组下有效的数据权限列表
     *
     * @param secGrpId 安全组ID
     * @return 安全组数据权限行列表
     */
    List<SecGrpDclLine> selectSecGrpDclInGrp(@Param("secGrpId") Long secGrpId);

    /**
     * 查询角色所有可使用的安全组数据权限
     *
     * @param roleId 角色ID
     * @return 安全组数据权限行列表
     */
    List<SecGrpDclLine> selectSecGrpDclInRole(@Param("roleId") Long roleId);


    /**
     * 查询角色分配的安全组中可访问的数据权限
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID
     */
    List<SecGrpDclLine> selectRoleSecGrpDcl(@Param("roleId") Long roleId, @Param("secGrpIds") List<Long> secGrpIds);

    /**
     * 查询指定角色及其子角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId        发起回收有效角色ID
     * @param authorityType 数据权限类型
     * @param dataIds       受影响的数据D列表
     * @return
     */
    List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRoleAndSubRole(@Param("roleId") Long roleId,
                                                                        @Param("authorityType") String authorityType,
                                                                        @Param("dataIds") List<Long> dataIds,
                                                                        @Param("includeRevokeFlag") Integer includeRevokeFlag);

    /**
     * 查询指定角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId            发起回收有效角色ID
     * @param authorityTypeCode 数据权限类型
     * @param dataIds           受影响的数据D列表
     */
    List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRole(@Param("roleId") Long roleId,
                                                              @Param("authorityTypeCode") String authorityTypeCode,
                                                              @Param("dataIds") List<Long> dataIds,
                                                              @Param("includeRevokeFlag") Integer includeRevokeFlag);

    /**
     * 查询数据权限行详情
     *
     * @param secGrpDclLineId 安全组数据权限行ID
     * @return
     */
    SecGrpDclLine selectSecGrpDclLineDetailById(@Param("secGrpDclLineId") Long secGrpDclLineId);

    /**
     * 查询角色分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     *
     * @param secGrpId          需要排除的安全组ID
     * @param roleId            角色ID
     * @param authorityTypeCode 权限类型码
     * @param dataIds           权限ID集合
     * @return 角色分配的安全组中，排除此安全组后，其它安全组中包含这些数据权限行的权限行
     */
    List<SecGrpDclLine> selectRoleNotIncludeSecGrpDclLine(@Param("secGrpId") Long secGrpId,
                                                          @Param("roleId") Long roleId,
                                                          @Param("authorityTypeCode") String authorityTypeCode,
                                                          @Param("dataIds") Set<Long> dataIds);

    /**
     * 查询用户分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     *
     * @param secGrpId          需要排除的安全组ID
     * @param userId            用户ID
     * @param authorityTypeCode 权限类型码
     * @param dataIds           权限ID集合
     * @return 角色分配的安全组中，排除此安全组后，其它安全组中包含这些数据权限行的权限行
     */
    List<SecGrpDclLine> selectUserNotIncludeSecGrpDclLine(@Param("secGrpId") Long secGrpId,
                                                          @Param("userId") Long userId,
                                                          @Param("authorityTypeCode") String authorityTypeCode,
                                                          @Param("dataIds") Set<Long> dataIds);

    List<SecGrpDclLine> selectRoleIncludedDclLine(@Param("roleId") Long roleId,
                                                  @Param("excludeSecGrpId") Long excludeSecGrpId,
                                                  @Param("authorityTypeCode") String authorityTypeCode,
                                                  @Param("dataIds") Set<Long> dataIds);

    void batchInsertBySql(List<SecGrpDclLine> dclLines);

    Set<Long> selectDeletedDclLineId(@Param("secGrpId") Long secGrpId,
                                     @Param("authorityTypeCode") String authorityTypeCode,
                                     @Param("dataIds") Set<Long> dataIds);

    void batchDeleteBySql(@Param("dclLineIds") Set<Long> dclLineIds);
}
