package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.MenuPermission;

/**
 *
 * @author allen 2018/7/5
 */
public interface MenuPermissionMapper extends BaseMapper<MenuPermission> {

    int deleteByIdAndCodes(@Param("permissionSetId") Long permissionSetId, @Param("codes") List<String> codes);

    int deleteByServiceNameAndCodes(@Param("serviceName") String serviceName, @Param("permissionCodes") List<String> permissionCodes);

    /**
     * 查询所有菜单-权限关联
     */
    List<MenuPermission> selectAllMenuPermissions();

    /**
     * 删除菜单-权限关联数据
     */
    void deleteAllMenuPermissions();

    void batchInsertBySql(List<MenuPermission> menuPermissions);
}
