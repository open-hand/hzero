package org.hzero.gateway.helper.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.gateway.helper.domain.PermissionCheckDTO;
import org.hzero.gateway.helper.domain.vo.RoleVO;
import org.hzero.gateway.helper.entity.PermissionDO;

/**
 * @author qingsheng.chen 2018/12/26 星期三 10:46
 */

public interface PermissionPlusMapper extends BaseMapper<PermissionDO> {


    List<Long> selectSourceIdsByMemberAndRole(PermissionCheckDTO query);

    List<PermissionDO> selectPermissionByMethodAndService(@Param("method") String method, @Param("service") String service);

    int countMenuPermission(@Param("menuId") Long menuId, @Param("permissionCode") String permissionCode);

    /**
     * 查询系统超级管理员
     * {@link org.hzero.common.HZeroConstant.RoleCode#SITE} &
     * {@link org.hzero.common.HZeroConstant.RoleCode#TENANT}
     */
    List<RoleVO> selectSuperAdminRole();

    int countAvailableRole(@Param("memberId") Long memberId, @Param("memberType") String memberType, @Param("roleId") Long roleId);
}
