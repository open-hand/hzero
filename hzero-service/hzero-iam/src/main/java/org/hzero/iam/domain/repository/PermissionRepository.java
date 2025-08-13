package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 权限管理
 *
 * @author allen 2018/6/25
 */
public interface PermissionRepository extends BaseRepository<Permission> {

    /**
     * 分页查询
     */
    Page<PermissionVO> pagePermission(String condition, String level, PageRequest pageRequest);

    /**
     * 根据编码查询权限
     *
     * @param codes 权限编码
     * @return LIST
     */
    List<Permission> selectByCodes(String[] codes);

    /**
     * 根据权限ID查询
     *
     * @param ids 权限ID集合
     * @return 权限集合
     */
    List<Permission> selectByIds(List<Long> ids);

    /**
     * 根据编码查询Lov(Lov当成一种特殊的权限)
     *
     * @param codes 权限编码
     * @return LIST
     */
    List<Lov> selectLovByCodes(String[] codes, Long tenantId);

    /**
     * 缓存服务下的权限
     *
     * @param serviceName 服务名称
     * @param clearCache  是否清除缓存
     */
    void cacheServicePermissions(String serviceName, boolean clearCache);

    /**
     * 根据服务名查询权限
     *
     * @param serviceName 服务名
     */
    List<Permission> selectSimpleByService(String serviceName);

    /**
     * 通过编码查询接口权限，如果用户没有权限访问接口查询不到
     *
     * @param permissionCode 权限编码
     * @param level          接口层级
     * @return 接口信息
     */
    PermissionVO queryPermissionByCode(String permissionCode, String level);

    /**
     * 分页查询租户级api列表
     *
     * @param permissionVO 查询条件
     * @param pageRequest  分页
     * @return Page
     */
    Page<PermissionVO> pageTenantApis(PermissionVO permissionVO, PageRequest pageRequest);

    /**
     * 分页查询api列表
     *
     * @param permissionVO 查询条件
     * @param pageRequest  分页
     * @return Page
     */
    Page<PermissionVO> pageApis(PermissionVO permissionVO, PageRequest pageRequest);

    /**
     * 分页查询可以添加的
     *
     * @param permissionVO
     * @param pageRequest
     * @return
     */
    Page<PermissionVO> pageTenantAssignableApis(PermissionVO permissionVO, PageRequest pageRequest);

    /**
     * 通过服务名称查询权限数据
     *
     * @param serviceName 服务名称
     * @return 权限数据
     */
    List<Permission> listByServiceName(String serviceName);
}
