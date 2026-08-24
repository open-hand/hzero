package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 角色数据权限行定义资源库
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:51:40
 */
public interface RoleAuthorityLineRepository extends BaseRepository<RoleAuthorityLine> {

    /**
     * 角色权限头Id查询角色权限行
     *
     * @param roleAuthId 角色权限头Id
     * @return 查询结果
     */
    List<RoleAuthorityLine> selectByRoleAuthId(Long roleAuthId);

    /**
     * 查询获取有效的角色权限行信息
     *
     * @return List<RoleAuthorityLine>
     */
    List<RoleAuthorityLine> selectDocRoleAuthLine();

    /**
     * 通过权限维度编码查询角色权限行数据
     *
     * @param dimensionCode 权限维度编码
     * @return 角色权限行
     */
    List<RoleAuthorityLine> selectRoleAuthLineByAuthTypeCode(String dimensionCode);
}
