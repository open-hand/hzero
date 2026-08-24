package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.vo.DashboardLayoutVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 工作台配置Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 */
public interface DashboardLayoutMapper extends BaseMapper<DashboardLayout> {

    /**
     * 查询父级角色信息
     *
     * @param roleId 角色id
     * @return 父级角色Ids
     */
    List<Long> selectParentRole(@Param("roleId") Long roleId);

    /**
     * 校验是否是租户管理员
     *
     * @param roleIds 角色Ids
     * @return 模板角色Id
     */
    Long checkOrgAdminUser(@Param("roleIds") List<Long> roleIds);

    /**
     * 查询获取角色卡片信息
     * FIX 03-21 sql中判断是否继承模板角色从而获取模板角色分配的卡片
     *
     * @param roleIds 当前角色Id
     * @return 角色卡片信息集合
     */
    List<DashboardLayoutVO> selectRoleCardsByRoleId(@Param("roleIds") List<Long> roleIds, @Param("templateCodes") List<String> templateCodes);

    /**
     * 查询获取当前用户布局信息
     *
     * @param userId   用户Id
     * @param roleIds  角色合并Ids
     * @param tenantId 租户Id
     * @return 当前用户布局信息
     */
    List<DashboardLayoutVO> selectCurrentLayouts(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds, @Param("tenantId") Long tenantId);

    /**
     * 查询获取租户管理员角色卡片信息
     *
     * @param roleIds 当前角色ids
     * @return 角色卡片信息
     */
    List<DashboardLayoutVO> selectOrgAdminRoleCards(@Param("roleIds") List<Long> roleIds);
}
