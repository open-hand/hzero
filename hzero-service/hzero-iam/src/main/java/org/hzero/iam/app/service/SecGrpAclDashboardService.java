package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.SecGrpAclDashboard;

/**
 * 安全组工作台配置应用服务
 *
 * @author bojiangzhou 2020/02/17
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclDashboardService {

    /**
     * 新增安全组工作台配置
     *
     * @param secGrpId         安全组ID
     * @param secGrpDashboards 安全组工作台配置
     */
    void createSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards);

    /**
     * 更新安全组工作台配置
     *
     * @param secGrpId         安全组ID
     * @param secGrpDashboards 安全组工作台配置
     */
    void updateSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards);

    /**
     * 删除安全组中的工作台配置
     *
     * @param tenantId         租户ID
     * @param secGrpId         安全组ID
     * @param secGrpDashboards 待删除的工作台配置
     */
    void removeSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards);
}
