package org.hzero.iam.app.service;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * 安全组访问权限应用服务
 *
 * @author bojiangzhou 2020/02/17 代码优化
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpAclService {

    /**
     * 向安全组中新增权限
     *
     * @param tenantId      租户ID
     * @param secGrpId      安全组ID
     * @param permissionIds 权限ID
     */
    void createSecGrpAcl(@Nullable Long tenantId, @NotNull Long secGrpId, List<Long> permissionIds);

    /**
     * 向安全组中删除权限
     *
     * @param tenantId      租户ID
     * @param secGrpId      安全组ID
     * @param permissionIds 权限ID
     */
    void deleteSecGrpAcl(@Nullable Long tenantId, @NotNull Long secGrpId, List<Long> permissionIds);

}
