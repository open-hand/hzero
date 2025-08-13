package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.mybatis.base.BaseRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * 安全组数据权限行资源库
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclLineRepository extends BaseRepository<SecGrpDclLine> {

    /**
     * 查询安全组的数据权限，需排除被回收的权限
     */
    Page<SecGrpDclLine> listSecGrpDclLine(SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询安全组可分配的数据权限，需排除被回收的权限
     */
    Page<SecGrpDclLine> listSecGrpAssignableDclLine(SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);


    List<SecGrpDclLine> listRoleNotIncludedDclLine(Long roleId, Long secGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines);

    /**
     * 查询分配给指定角色的安全组单据权限行，并标志屏蔽状态
     *
     * @param roleId      角色ID
     * @param secGrpId    当前安全组ID
     * @param queryDTO    查询参数
     * @param pageRequest 分页参数
     * @return 安全组数据权限行
     */
    Page<SecGrpDclLine> listRoleSecGrpDcl(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询自建安全组下有效的数据权限列表
     *
     * @param secGrpId 安全组ID
     * @return 安全组数据权限行列表
     */
    List<SecGrpDclLine> selectSecGrpDclInGrp(Long secGrpId);

    /**
     * 查询角色所有可使用的安全组数据权限
     *
     * @param roleId 角色ID
     * @return 安全组数据权限行列表
     */
    List<SecGrpDclLine> selectSecGrpDclInRole(Long roleId);

    /**
     * 查询角色分配的安全组中可访问的数据权限
     *
     * @param roleId    角色ID
     * @param secGrpIds 安全组ID列表
     */
    List<SecGrpDclLine> listRoleSecGrpDcl(Long roleId, List<Long> secGrpIds);

    /**
     * 查询指定角色及其子角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId            直接分配角色ID
     * @param authorityType     数据权限类型
     * @param dataIds           受影响的数据D列表
     * @param includeRevokeFlag 是否包含被回收的
     */
    List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRoleAndSubRole(Long roleId, String authorityType,
                                                                        List<Long> dataIds, Integer includeRevokeFlag);

    /**
     * 查询指定角色自建的且绑定了指定数据权限的安全组数据权限
     *
     * @param roleId            直接分配角色ID
     * @param authorityType     数据权限类型
     * @param dataIds           受影响的数据D列表
     * @param includeRevokeFlag 是否包含被回收的
     */
    List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRole(Long roleId, String authorityType,
                                                              List<Long> dataIds, Integer includeRevokeFlag);

    /**
     * 查询数据权限行详情
     *
     * @param secGrpDclLineId 安全组数据权限行ID
     * @return
     */
    SecGrpDclLine selectSecGrpDclLineDetailById(Long secGrpDclLineId);

    /**
     * 查询安全组下所有的数据权限
     *
     * @param secGrpId 安全组
     * @return 数据权限列表
     */
    List<SecGrpDclLine> selectBySecGrpId(Long secGrpId);

    /**
     * 查询角色分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     *
     * @param secGrpId      安全组ID
     * @param roleId        角色ID
     * @param authorityType 数据权限类型
     * @param dataIds       数据ID
     * @return 角色分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     */
    List<SecGrpDclLine> listRoleNotIncludeSecGrpDclLine(Long secGrpId, Long roleId, String authorityType, Set<Long> dataIds);

    /**
     * 查询用户分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     *
     * @param secGrpId      安全组ID
     * @param userId        用户ID
     * @param authorityType 数据权限类型
     * @param dataIds       数据IDs
     * @return 角色分配的安全组中，排除此安全组后，其它安全组中不包含这些数据权限行的权限行
     */
    List<SecGrpDclLine> listUserNotIncludeSecGrpDclLine(Long secGrpId, Long userId, String authorityType, Set<Long> dataIds);

    void batchAdd(List<SecGrpDclLine> addList);

    void batchRemove(Long secGrpId, String authorityType, Set<Long> dataIds);

    /**
     * 通过ID删除数据
     *
     * @param secGrpId   安全组ID
     * @param dclLineIds 待删除的ID
     */
    void batchDeleteBySql(Long secGrpId, Set<Long> dclLineIds);

    /**
     * 通过数据权限的id查询数据权限行数据
     *
     * @param dclId 数据权限ID
     * @return 查询到的数据权限行数据
     */
    List<SecGrpDclLine> selectDclLineByDclId(@Nonnull Long dclId);
}
