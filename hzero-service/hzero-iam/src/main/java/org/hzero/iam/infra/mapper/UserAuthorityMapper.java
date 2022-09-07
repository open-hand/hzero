package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.vo.CompanyVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 用户权限管理表Mapper
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
public interface UserAuthorityMapper extends BaseMapper<UserAuthority> {

    /**
     * 查询公司、业务实体、库存组织
     *
     * @param tenantId
     * @param userId
     * @return CompanyOuInvorgDTO
     */
    List<CompanyOuInvorgNodeDTO> listComanyUoInvorg(@Param("tenantId") Long tenantId, @Param("userId") Long userId,
                                                    @Param("dataCode") String dataCode, @Param("dataName") String dataName);


    /**
     * 根据租户ID和用户ID查询权限头信息
     *
     * @param tenantId
     * @param userId
     * @return typeCodeList
     */
    List<String> listUserAuthorityTypeCode(@Param("tenantId") Long tenantId, @Param("userId") Long userId);

    /**
     * 查询公司数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryComDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                @Param("dataName") String dataName);

    /**
     * 查询业务实体数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryOUDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                               @Param("dataName") String dataName);

    /**
     * 查询库存组织数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryInvDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                @Param("dataName") String dataName);

    /**
     * 查询采购组织数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryPOrgDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                 @Param("dataName") String dataName);

    /**
     * 查询采购员数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryPAgentDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                   @Param("dataName") String dataName);

    /**
     * 查询采购品种数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryPCatDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                 @Param("dataName") String dataName);

    /**
     * 查询数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                             @Param("dataName") String dataName);

    /**
     * 查询值集数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryLovDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                @Param("dataName") String dataName);

    /**
     * 查询值集视图数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据ID
     */
    Long queryLovViewDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                    @Param("dataName") String dataName);

    /**
     * 返回唯一确定的公司
     *
     * @param companyNum 公司编码
     * @param tenantId   该公司对应的租户id，注意此时的公司确保是改公司的租户id
     * @return 返回唯一的公司
     */
    CompanyVO selectCompanyInfo(@Param("companyNum") String companyNum, @Param("tenantId") Long tenantId);

    /**
     * 查询数据已分配的用户列表
     *
     * @param dataId
     * @param authorityTypeCode
     * @param userAuthority
     * @return
     */
    List<UserAuthorityDTO> listDataAssignedUser(@Param("dataId") Long dataId,
                                                @Param("authorityTypeCode") String authorityTypeCode,
                                                @Param("userAuthority") UserAuthority userAuthority);

    /**
     * 查询公司、业务实体、库存组织
     *
     * @param tenantId 租户id
     * @param dataCode 代码
     * @param dataName 名称
     * @return CompanyOuInvorgDTO
     */
    List<CompanyOuInvorgNodeDTO> listComanyUoInvorgAll(@Param("tenantId") Long tenantId,
                                                       @Param("dataCode") String dataCode,
                                                       @Param("dataName") String dataName);

    /**
     * 通过维度代码查视图代码
     *
     * @param dimensionCode 维度代码
     * @return 视图代码
     */
    String selectDimensionLovCode(@Param("dimensionCode") String dimensionCode);

    /**
     * 查询用户下已分配安全组的用户权限
     *
     * @param tenantId 租户Id
     * @param userId   用户Id
     * @param secGrpId 安全组Id
     * @return
     */
    List<UserAuthority> listUserAuthorityAssignSecGrp(@Param("tenantId") Long tenantId,
                                                      @Param("userId") Long userId,
                                                      @Param("secGrpId") Long secGrpId);

    /**
     * 通过用户Id和租户Id 查询用户权限列表
     *
     * @param userId   用户Id
     * @param tenantId 租户Id
     * @return
     */
    List<UserAuthority> listByUserIdAndTenantId(@Param("userId") Long userId,
                                                @Param("tenantId") Long tenantId);

    /**
     * 查询获取用户权限数据
     *
     * @return List<UserAuthority>
     */
    List<UserAuthority> selectDocUserAuth();
}
