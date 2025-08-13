package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.RoleAuthDataLine;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 角色单据权限管理行Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
public interface RoleAuthDataLineMapper extends BaseMapper<RoleAuthDataLine> {

    /**
     * 查询角色数据权限行
     *
     * @param authDataId 数据权限行
     * @param tenantId   租户ID
     * @param dataCode   数据编码
     * @param dataName   数据名称
     * @return 角色数据权限行
     */
    List<RoleAuthDataLine> listRoleAuthDataLine(@Param("authDataId") Long authDataId,
                                                @Param("tenantId") Long tenantId,
                                                @Param("dataCode") String dataCode,
                                                @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLinePurOrg(@Param("tenantId") Long tenantId,
                                                      @Param("roleId") Long roleId,
                                                      @Param("dataCode") String dataCode,
                                                      @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLinePurAgent(@Param("tenantId") Long tenantId,
                                                        @Param("roleId") Long roleId,
                                                        @Param("dataCode") String dataCode,
                                                        @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineLov(@Param("tenantId") Long tenantId,
                                                   @Param("roleId") Long roleId,
                                                   @Param("dataCode") String dataCode,
                                                   @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineLovView(@Param("tenantId") Long tenantId,
                                                       @Param("roleId") Long roleId,
                                                       @Param("dataCode") String dataCode,
                                                       @Param("dataName") String dataName);

    /**
     * 查询角色数据源单据权限
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 数据源列表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineDatasource(@Param("tenantId") Long tenantId,
                                                          @Param("roleId") Long roleId,
                                                          @Param("dataCode") String dataCode,
                                                          @Param("dataName") String dataName);

    /**
     * 查询角色数据源单据权限
     *
     * @param tenantId  租户ID
     * @param roleId    角色ID
     * @param groupCode 数据编码
     * @param groupName 数据名称
     * @return 数据源列表
     */
    List<RoleAuthDataLine> listRoleAuthGroupData(@Param("tenantId") Long tenantId,
                                                 @Param("roleId") Long roleId,
                                                 @Param("groupCode") String groupCode,
                                                 @Param("groupName") String groupName);


    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLinePurOrgAll(@Param("tenantId") Long tenantId,
                                                      @Param("dataCode") String dataCode,
                                                      @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLinePurAgentAll(@Param("tenantId") Long tenantId,
                                                        @Param("dataCode") String dataCode,
                                                        @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineLovAll(@Param("tenantId") Long tenantId,
                                                   @Param("dataCode") String dataCode,
                                                   @Param("dataName") String dataName);

    /**
     * 查询角色单据权限管理行
     *
     * @param tenantId 租户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 角色单据权限管理行表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineLovViewAll(@Param("tenantId") Long tenantId,
                                                       @Param("dataCode") String dataCode,
                                                       @Param("dataName") String dataName);

    /**
     * 查询角色数据源单据权限
     *
     * @param tenantId 租户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 数据源列表
     */
    List<RoleAuthDataLine> listRoleAuthDataLineDatasourceAll(@Param("tenantId") Long tenantId,
                                                          @Param("dataCode") String dataCode,
                                                          @Param("dataName") String dataName);

    /**
     * 查询角色数据源单据权限
     *
     * @param tenantId  租户ID
     * @param groupCode 数据编码
     * @param groupName 数据名称
     * @return 数据源列表
     */
    List<RoleAuthDataLine> listRoleAuthGroupDataAll(@Param("tenantId") Long tenantId,
                                                 @Param("groupCode") String groupCode,
                                                 @Param("groupName") String groupName);

    /**
     * 查询排除目标角色已经存在的权限数据
     *
     * @param organizationId 租户id
     * @param roleId 源角色Id
     * @param copyRoleId 目标角色Id
     * @param compDocType 权限类型
     * @return List<RoleAuthDataLine>
     */
    List<RoleAuthDataLine> selectCompliantRoleAuthDatas(@Param("organizationId") Long organizationId,
                                                        @Param("roleId") Long roleId,
                                                        @Param("copyRoleId")Long copyRoleId,
                                                        @Param("compDocType")String compDocType);

    /**
     * 查询角色权限指定公司下的业务实体Id信息
     *
     * @param roleId    角色Id
     * @param tenantId  租户Id
     * @param companyId 公司Id
     * @return 业务实体Ids
     */
    List<Long> selectCompanyAssignOu(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId, @Param("companyId") Long companyId);

    /**
     * 查询角色权限指定业务实体下的库存组织Id信息
     *
     * @param roleId    角色Id
     * @param tenantId  租户Id
     * @param ouIds     业务实体Ids
     * @return 库存组织Ids
     */
    List<Long> selectOuAssignInvOrg(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId, @Param("ouIds") List<Long> ouIds);
}
