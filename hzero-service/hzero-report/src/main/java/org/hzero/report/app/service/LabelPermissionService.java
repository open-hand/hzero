package org.hzero.report.app.service;

import org.hzero.report.domain.entity.LabelPermission;

/**
 * 标签权限应用服务
 *
 * @author fanghan.liu@hand-china.com 2019-12-02 10:27:44
 */
public interface LabelPermissionService {

    /**
     * 创建标签权限
     *
     * @param labelPermission 标签权限
     */
    void createLabelPermission(LabelPermission labelPermission);

    /**
     * 修改标签权限
     *
     * @param labelPermission 标签权限
     * @return 标签权限
     */
    LabelPermission updateLabelPermission(LabelPermission labelPermission);

    /**
     * 删除标签权限
     *
     * @param permissionId 权限ID
     */
    void removeLabelPermission(Long permissionId);
}
