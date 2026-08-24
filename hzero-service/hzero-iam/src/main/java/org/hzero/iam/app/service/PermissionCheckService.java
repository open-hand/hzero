package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.api.dto.MenuPermissionSetDTO;
import org.hzero.iam.infra.constant.PermissionType;

/**
 * 缺失权限应用服务
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
public interface PermissionCheckService {

    /**
     * 清理缺失权限
     *
     * @param clearType 清理类型
     */
    void clearPermissionCheck(String clearType, String checkState);

    /**
     * 添加权限到权限集
     *
     * @param menuIds 权限集ID
     * @param permissionType 权限类型
     * @param permissionCode 权限编码
     * @param checkState 检查状态
     */
    void addPermissionSet(List<Long> menuIds, PermissionType permissionType, String[] permissionCode,
                    String checkState);

    /**
     * 分配未授权的API
     */
    void assignNotPassApi(MenuPermissionSetDTO menuPermissionSetDTO);

    /**
     * 刷新服务权限
     */
    void refreshMismatchApi(String[] serviceCodes);

    /**
     * 
     * 分配权限到菜单默认权限集
     * 
     * @param menuPermissionSetDTO
     */
    void assignMenuPermissionApi(MenuPermissionSetDTO menuPermissionSetDTO);
}
