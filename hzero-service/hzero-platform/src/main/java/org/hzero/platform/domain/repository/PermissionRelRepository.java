package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.PermissionRelDTO;
import org.hzero.platform.domain.entity.PermissionRel;

/**
 * 屏蔽范围规则关系资源库
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-29 15:19:45
 */
public interface PermissionRelRepository extends BaseRepository<PermissionRel> {

    /**
     * 查询数据权限规则
     *
     * @param rangeId 范围id
     * @return 数据权限规则
     */
    List<PermissionRelDTO> selectPermissionRuleByRangeId(Long rangeId);

    /**
     * 新增一个屏蔽范围规则关系，并同步redis
     *
     * @param permissionRel 屏蔽范围规则关系
     * @return 屏蔽范围规则关系
     */
    PermissionRel insertPermissionRel(PermissionRel permissionRel);

    /**
     * 更新一个屏蔽范围规则关系，并同步redis
     *
     * @param permissionRel 屏蔽范围规则关系
     * @return 屏蔽范围规则关系
     */
    PermissionRel updatePermissionRel(PermissionRel permissionRel);

    /**
     * 删除，并同步redis
     *
     * @param permissionRelId 关系id
     */
    default void deletePermissionRel(Long permissionRelId) {
        deletePermissionRel(permissionRelId, true);
    }

    /**
     * 删除，并同步redis
     *
     * @param permissionRelId 关系id
     */
    void deletePermissionRel(Long permissionRelId, boolean validEditable);
}
