package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.api.dto.SecGrpAclApiDTO;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.vo.Lov;
import org.hzero.iam.domain.vo.PermissionVO;

/**
 * @author wuguokai
 */
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 分页查询
     *
     * @param condition 编码或名称
     * @param level     层级
     */
    List<PermissionVO> selectPermissions(@Param("condition") String condition, @Param("level") String level);

    /**
     * 查询Lov
     */
    List<Lov> selectLovByCodes(@Param("codes") List<String> codes, @Param("tenantId") Long tenantId);

    /**
     * 根据服务名查
     */
    List<Permission> selectSimpleByService(String serviceName);

    /**
     * 通过编码查询接口权限，如果用户没有权限访问接口查询不到
     *
     * @param permissionCode 权限编码
     * @param level          接口层级
     * @return 接口信息
     */
    PermissionVO queryPermissionByCode(@Param("permissionCode") String permissionCode, @Param("level") String level);

    /**
     * 查询接口列表
     *
     * @param serviceName 服务名称
     * @param method      请求方式
     * @param path        请求路径
     * @param description 请求描述
     * @param includeAll  默认查询的的API是维护了字段的
     * @param roleId      角色ID，只筛选该角色能够查看的接口
     * @param userId      用户ID，只筛选该用户能够查看的接口
     * @return 接口列表
     */
    List<PermissionFieldResponse> listApi(@Param("serviceName") String serviceName,
                                          @Param("method") String method,
                                          @Param("path") String path,
                                          @Param("description") String description,
                                          @Param("includeAll") boolean includeAll,
                                          @Param("roleId") Long roleId,
                                          @Param("userId") Long userId);

    /**
     * 查询安全组中的接口列表
     *
     * @param serviceName 服务名称
     * @param method      请求方式
     * @param path        请求路径
     * @param includeAll  默认查询的的API是维护了字
     * @param description 请求描述段的
     * @param secGrpIds   安全组id列表
     * @return 接口列表
     */
    List<Permission> listApiInSecGrps(@Param("serviceName") String serviceName,
                                      @Param("method") String method,
                                      @Param("path") String path,
                                      @Param("description") String description,
                                      @Param("includeAll") boolean includeAll,
                                      @Param("secGrpIds") List<Long> secGrpIds);

    /**
     * 查询租户的api权限集列表
     *
     * @param permissionVO permission
     * @return 租户的api权限管理接口列表
     */
    List<PermissionVO> selectTenantApis(PermissionVO permissionVO);

    /**
     * 查询api权限列表
     *
     * @param permissionVO 参数
     * @return api权限管理接口列表
     */
    List<PermissionVO> selectApis(PermissionVO permissionVO);

    /**
     * 查询租户可以添加的权限
     *
     * @param permissionVO 参数
     * @return 可添加的权限
     */
    List<PermissionVO> selectTenantAssignableApis(PermissionVO permissionVO);

    /**
     * 安全组字段权限——查询可配置的API
     * @param dto 查询条件
     */
    List<Permission> listSecGrpAssignableApi(SecGrpAclApiDTO dto);

    /**
     * 安全组字段权限——查询可配置的API
     * @param dto 查询条件
     */
    List<Permission> listSecGrpAssignedApi(SecGrpAclApiDTO dto);
}
