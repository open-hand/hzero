package org.hzero.iam.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.service.authdata.vo.AuthDataVo;

/**
 * 权限数据Mapper
 *
 * @author bo.he02@hand-china.com 2020/06/05 11:59
 */
public interface AuthDataMapper {
    /**
     * 查询公司数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryComDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                      @Param("dataName") String dataName);

    /**
     * 查询业务实体数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryOuDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                     @Param("dataName") String dataName);

    /**
     * 查询库存组织数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryInvOrgDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                         @Param("dataName") String dataName);

    /**
     * 查询采购组织数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryPurOrgDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                         @Param("dataName") String dataName);

    /**
     * 查询采购员数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryPurAgentDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                           @Param("dataName") String dataName);

    /**
     * 查询采购品种数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryPurCatDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                         @Param("dataName") String dataName);

    /**
     * 查询数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                   @Param("dataName") String dataName);

    /**
     * 查询值集数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryLovDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                      @Param("dataName") String dataName);

    /**
     * 查询值集视图数据源信息
     *
     * @param tenantId 租户ID
     * @param dataCode 数据Code
     * @param dataName 数据名称
     * @return 查询结果数据
     */
    AuthDataVo queryLovViewDataSourceInfo(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode,
                                          @Param("dataName") String dataName);
}
