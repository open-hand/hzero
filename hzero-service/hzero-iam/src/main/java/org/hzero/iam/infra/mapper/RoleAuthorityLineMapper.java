package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.RoleAuthorityLine;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 角色数据权限行定义Mapper
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:51:40
 */
public interface RoleAuthorityLineMapper extends BaseMapper<RoleAuthorityLine> {

    /**
     * 通过角色权限头获取角色权限行信息
     *
     * @param roleAuthId 角色权限头Id
     * @return List<RoleAuthorityLine>
     */
    List<RoleAuthorityLine> selectByRoleAuthId(@Param("roleAuthId") Long roleAuthId);

    /**
     * 查询获取有效的角色权限行信息
     *
     * @return List<RoleAuthorityLine>
     */
    List<RoleAuthorityLine> selectDocRoleAuthLine();

    /**
     * 通过权限维度编码查询角色权限行数据
     *
     * @param authTypeCode 权限维度编码
     * @return 角色权限行
     */
    List<RoleAuthorityLine> selectRoleAuthLineByAuthTypeCode(@Param("authTypeCode") String authTypeCode);
}
