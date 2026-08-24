package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.FieldPermission;

import java.util.List;

/**
 * 接口字段权限维护Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldPermissionMapper extends BaseMapper<FieldPermission> {

    /**
     * 查询字段权限列表
     *
     * @param tenantId            租户ID
     * @param permissionId        接口ID
     * @param permissionDimension 权限维度
     * @param dimensionValue      维度值
     * @param fieldDescription    字段描述
     * @param permissionType      权限类型
     * @return 字段权限列表
     */
    List<FieldPermission> listPermission(long tenantId, long permissionId, String permissionDimension, long dimensionValue, String fieldDescription, String permissionType);

    /**
     * 查询所有字段权限
     *
     * @return 字段权廯列表
     */
    List<FieldPermission> listAll();

    /**
     * 删除安全组的字段权限
     *
     * @param tenantId  租户Id
     * @param userId    用户Id
     * @param fieldIds  字段Id
     * @param dimension 权限维度
     * @return
     */
    Integer deleteSecGrpPermission(@Param("tenantId") Long tenantId, @Param("userId") Long userId,
                                   @Param("dataSource") String dataSource,
                                   @Param("fieldIds") List<Long> fieldIds,
                                   @Param("dimension") String dimension);

    /**
     *  更新安全组的字段权限
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param dataSource 数据来源,过滤条件
     * @param dataSourceValue 数据来源,更新的数值
     * @param fieldIds 字段权限Id
     * @param dimension 权限维度
     * @return
     */
    Integer updateSecGrpPermission(@Param("tenantId") Long tenantId, @Param("userId") Long userId,
                                   @Param("dataSource") String dataSource,
                                   @Param("dataSourceValue") String dataSourceValue,
                                   @Param("fieldIds") List<Long> fieldIds,
                                   @Param("dimension") String dimension);

    /**
     * 查询安全组的字段权限
     *
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param fieldIds 字段权限Id
     * @param dimension 权限维度
     * @return
     */
    List<FieldPermission> listSecGrpPermission(@Param("tenantId") Long tenantId,
                                               @Param("userId") Long userId,
                                               @Param("fieldIds") List<Long> fieldIds,
                                               @Param("dimension") String dimension,
                                               @Param("dataSources") List<String> dataSources);
}
