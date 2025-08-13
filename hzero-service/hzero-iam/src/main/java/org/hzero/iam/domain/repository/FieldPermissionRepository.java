package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 接口字段权限维护资源库
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldPermissionRepository extends BaseRepository<FieldPermission> {

    /**
     * 查询字段权限列表
     *
     * @param tenantId            租户ID
     * @param permissionId        接口ID
     * @param permissionDimension 权限维度
     * @param dimensionValue      维度值
     * @param fieldDescription    字段描述
     * @param permissionType      权限类型
     * @param pageRequest         分页参数
     * @return 字段权限列表
     */
    Page<FieldPermission> pagePermission(long tenantId, long permissionId, String permissionDimension, long dimensionValue, String fieldDescription, String permissionType, PageRequest pageRequest);

    /**
     * 分页查询所有字段权限列表
     *
     * @param pageRequest 分页信息
     * @return 字段权限列表
     */
    List<FieldPermission> pageAll(PageRequest pageRequest);

    /**
     * 存储字段权限到缓存
     *
     * @param fieldPermission 字段权限
     */
    void storePermission(FieldPermission fieldPermission);

    /**
     * 批量存储字段权限到缓存
     *
     * @param fieldPermissionList 字段权限列表
     */
    void storePermission(List<FieldPermission> fieldPermissionList);

    /**
     * 删除字段权限缓存
     *
     * @param fieldPermission 字段权限缓存
     */
    void removePermission(FieldPermission fieldPermission);

    /**
     * 批量删除字段权限缓存
     *
     * @param fieldPermissionList 字段权限缓存列表
     */
    void removePermission(List<FieldPermission> fieldPermissionList);

    /**
     * 删除安全组分配的权限
     *
     * @param tenantId  租户Id
     * @param userId    用户Id
     * @param fieldIds  字段权限Id
     * @param dimension 权限维度
     */
    void removeSecGrpPermission(Long tenantId, Long userId, List<Long> fieldIds, String dimension);

    /**
     * 查询安全组字段权限
     *
     * @param tenantId  租户Id
     * @param userId    用户Id
     * @param fieldIds  字段权限列表
     * @param dimension 用户权限
     * @return
     */
    List<FieldPermission> listSecGrpPermission(Long tenantId, Long userId, List<Long> fieldIds, String dimension);
}
