package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.vo.PermissionVO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * API管理
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM 2019/11/27
 */
public interface PermissionService {

    /**
     * 更新API
     * 
     * @param permissions 权限
     */
    void updateApis(List<Permission> permissions);

    /**
     * 批量删除api
     *
     * @param permission 删除的权限
     */
    void deleteApis(List<Permission> permission);

    /**
     * 批量删除租户api权限
     *
     * @param permission 删除的权限
     */
    void deleteTenantApis(List<Permission> permission);

    /**
     * 分配权限给租户
     *
     * @param tenantId 租户ID
     * @param permissions 分配的权限
     */
    void assignTenantApis(Long tenantId, List<Permission> permissions);

    /**
     * 分配权限给所选的租户
     *
     * @param tenantIds 租户ID集合
     * @param permissions 分配的权限
     */
    void assignTenantApis(Long[] tenantIds, List<Permission> permissions);

    /**
     * 更新权限
     *
     * @param permission 更新值
     */
    void updateApi(Permission permission);

    /**
     * 分页查询API
     *
     * @param params
     * @param pageRequest
     * @return
     */
    Page<PermissionVO> pageApis(PermissionVO params, PageRequest pageRequest);

    /**
     * 分页查询租户API
     * @param params
     * @param pageRequest
     * @return
     */
    Page<PermissionVO> pageTenantApis(PermissionVO params, PageRequest pageRequest);
}
