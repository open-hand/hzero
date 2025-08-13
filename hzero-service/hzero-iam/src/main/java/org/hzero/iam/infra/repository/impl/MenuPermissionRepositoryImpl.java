package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.hzero.iam.domain.entity.MenuPermission;
import org.hzero.iam.domain.repository.MenuPermissionRepository;
import org.hzero.iam.infra.mapper.MenuPermissionMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * @author allen 2018/7/5
 */
@Repository
public class MenuPermissionRepositoryImpl extends BaseRepositoryImpl<MenuPermission> implements MenuPermissionRepository {

    @Autowired
    private MenuPermissionMapper menuPermissionMapper;

    @Override
    public int deleteByIdAndCodes(Long menuId, List<String> permissionCodes) {
        if (CollectionUtils.isEmpty(permissionCodes)) {
            return 0;
        }
        return menuPermissionMapper.deleteByIdAndCodes(menuId, permissionCodes);
    }

    @Override
    public void  deleteByMenuId(Long menuId) {
        MenuPermission query = new MenuPermission();
        query.setMenuId(menuId);
        List<MenuPermission> select = menuPermissionMapper.select(query);
        if(CollectionUtils.isNotEmpty(select)){
            select.forEach(item ->menuPermissionMapper.deleteByPrimaryKey(item.getId()));
        }
    }

    @Override
    public int deleteByPermissionCodes(String serviceName, List<String> permissionCodes) {
        if (CollectionUtils.isEmpty(permissionCodes)) {
            return 0;
        }
        return menuPermissionMapper.deleteByServiceNameAndCodes(serviceName, permissionCodes);
    }

    @Override
    public List<MenuPermission> selectAllMenuPermissions() {
        return menuPermissionMapper.selectAllMenuPermissions();
    }

    @Override
    public void deleteAllMenuPermissions() {
        menuPermissionMapper.deleteAllMenuPermissions();
    }

    @Override
    public void batchInsertBySql(List<MenuPermission> menuPermissionList) {
        if (CollectionUtils.isEmpty(menuPermissionList)) {
            return;
        }

        BatchSqlHelper.batchExecute(menuPermissionList, 5,
                (dataList) -> menuPermissionMapper.batchInsertBySql(dataList),
                "BatchInsertMenuPermission");
    }
}
