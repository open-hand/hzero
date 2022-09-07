package org.hzero.iam.domain.repository;

import java.util.List;

import javax.annotation.Nonnull;

import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author allen 2018/7/6
 */
public interface TenantRepository extends BaseRepository<Tenant> {

    /**
     * 查询当前用户可访问的租户列表，如果用户是超级用户，返回所有启用的租户列表
     */
    List<TenantDTO> selectSelfTenants(TenantDTO params);

    /**
     * 查询当前用户可访问的租户列表，如果用户是超级用户，返回所有启用的租户列表
     */
    Page<TenantDTO> selectSelfTenants(TenantDTO params, PageRequest pageRequest);

    /**
     * 分页查询租户信息
     *
     * @param tenant      租户查询条件
     * @param pageRequest 分页参数
     * @return 分页查询结果
     */
    Page<Tenant> pagingTenantsList(Tenant tenant, PageRequest pageRequest);

    /**
     * 查询租户详情信息
     *
     * @param tenantId 租户Id
     * @return 租户详情信息（携带tenantConfig信息）
     */
    Tenant selectTenantDetails(long tenantId);
    /**
     * 查询有租户客制化菜单的租户
     *
     * @param tenant      租户参数
     * @param pageRequest 分页参数
     * @return Tenant List
     */
    @Nonnull Page<Tenant> pagingHavingCustomMenuTenants(Tenant tenant, PageRequest pageRequest);

    /**
     * 根据用户ID查询租户
     *
     * @param userId
     * @return
     */
    Tenant selectTenantByUserId(Long userId);

    /**
     * 更新用户的租户信息
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return
     */
    int assignTenantToUser(Long tenantId, Long userId);

    /**
     * 检查租户名称或租户编码是否已存在
     *
     * @param tenant
     * @return 数据库中已存在的数量
     */
    int checkRepeatCount(Tenant tenant);
    /**
     * 根据租户编码查询
     *
     * @param tenantNum 租户编码
     * @return 租户
     */
    int countByTenantNum(@Nonnull String tenantNum);

}
