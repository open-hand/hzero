package org.hzero.iam.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.FieldPermission;

/**
 * 接口字段权限维护应用服务
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldPermissionService {

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
     * 通过字段ID查询所有字段权限
     *
     * @param fieldId 字段ID
     * @return 字段权限
     */
    List<FieldPermission> listFieldPermission(Long fieldId);

    /**
     * 新增字段权限
     *
     * @param fieldPermission 字段权限
     * @return 字段权限
     */
    FieldPermission createPermission(FieldPermission fieldPermission);

    /**
     * 批量新增字段权限
     *
     * @param fieldPermissionList 字段权限列表
     * @return 字段权限列表
     */
    List<FieldPermission> createPermission(List<FieldPermission> fieldPermissionList);

    /**
     * 更新字段权限
     *
     * @param fieldPermission 字段权限
     * @return 字段权限
     */
    FieldPermission updatePermission(FieldPermission fieldPermission);

    /**
     * 批量更新字段权限
     *
     * @param fieldPermissionList 字段权限列表
     * @return 字段权限列表
     */
    List<FieldPermission> updatePermission(List<FieldPermission> fieldPermissionList);

    /**
     * 删除字段权限
     *
     * @param fieldPermission 字段权限列表
     */
    void deletePermission(FieldPermission fieldPermission);

    /**
     * 批量删除字段权限
     *
     * @param fieldPermissionList 字段权限列表
     */
    void deletePermission(List<FieldPermission> fieldPermissionList);

    /**
     * 重置所有缓存
     */
    void restorePermission();

}
