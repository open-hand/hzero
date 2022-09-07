package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Tenant;

import io.choerodon.mybatis.common.BaseMapper;

/**
 *
 * @author allen 2018/7/6
 */
public interface TenantMapper extends BaseMapper<Tenant> {

    /**
     * 查询用户的租户
     * 
     * @param params 参数
     * @return List<TenantDTO>
     */
    List<TenantDTO> selectUserTenant(TenantDTO params);

    /**
     * 查询超级用户可访问的租户
     */
    List<TenantDTO> selectRootTenant(TenantDTO params);

    /**
     * 条件查询租户信息列表
     *
     * @param tenant 租户信息
     * @return 租户信息列表
     */
    List<Tenant> selectTenantsList(Tenant tenant);

    /**
     * 查询租户详情信息
     *
     * @param tenantId 租户Id
     * @return 租户详情信息（携带tenantConfig信息）
     */
    Tenant selectTenantDetails(@Param("tenantId") long tenantId);

    /**
     * 查询有租户客制化菜单的租户
     */
    List<Tenant> selectHavingCustomMenuTenant(Tenant tenant);

    /**
     * 根据用户ID查询租户
     * @param userId 用户id
     * @return 返回租户值
     */
    Tenant selectTenantByUserId(Long userId);

    /**
     * 更新用户的租户信息
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @return 返回值
     */
    int assignTenantToUser(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 检查租户名称或租户编码是否已存在
     *
     * @param tenant 租户信息
     * @return 数据库中已存在的数量
     */
    int checkRepeatCount(Tenant tenant);
}
