package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.UserAuthorityDataDTO;
import org.hzero.iam.domain.entity.UserAuthorityLine;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 用户权限管理行表Mapper
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
public interface UserAuthorityLineMapper extends BaseMapper<UserAuthorityLine> {

    /**
     * 分页查询用户权限数据
     *
     * @param authorityId
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return UserAuthorityLine
     */
    Page<UserAuthorityLine> selectCreateUserAuthorityLines(@Param("authorityId") Long authorityId, @Param("tenantId") Long tenantId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 用户权限数据更新
     *
     * @param userAuthorityLines 用户权限列表
     */
    void updateUserAuthorityLine(List<UserAuthorityLine> userAuthorityLines);

    /**
     * 查询用户数据权限采购组织
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return
     */
    List<UserAuthorityDataDTO> listPurOrg(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询用户数据权限采购员
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return
     */
    List<UserAuthorityDataDTO> listPurAgent(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询用户数据权限采购品类
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return
     */
    List<UserAuthorityDataDTO> listPurCat(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询用户数据权限值集
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 用户数据权限值集列表
     */
    List<UserAuthorityDataDTO> listLov(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询用户数据权限值集视图
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 用户数据权限值集视图列表
     */
    List<UserAuthorityDataDTO> listLovView(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询用户数据权限数据源
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 用户数据权限值集视图列表
     */
    List<UserAuthorityDataDTO> listDatasource(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询数据组列表
     *
     * @param tenantId  租户id
     * @param userId    用户id
     * @param groupCode 数据组代码
     * @param groupName 数据组名称
     * @return
     */
    List<UserAuthorityDataDTO> listDataGroup(@Param("tenantId") Long tenantId, @Param("userId") Long userId, @Param("groupCode") String groupCode, @Param("groupName") String groupName);

    /**
     * 查询用户数据权限采购品类
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return
     */
    List<UserAuthorityDataDTO> listPurCatAll(@Param("tenantId") Long tenantId, @Param("dataCode") String dataCode, @Param("dataName") String dataName);

    /**
     * 查询目标用户权限和来源用户权限的用户权限行的差异
     *
     * @param tenantId          租户Id
     * @param targetAuthorityId 目标用户权限Id
     * @param sourceAuthorityId 来源用户权限Id
     * @return
     */
    List<UserAuthorityLine> queryUserAuthLineDiff(@Param("tenantId") Long tenantId,
                                                  @Param("targetAuthorityId") Long targetAuthorityId,
                                                  @Param("sourceAuthorityId") Long sourceAuthorityId);

    /**
     * 查询子账户下指定公司分配的ou信息
     *
     * @param userId
     * @param tenantId
     * @param companyId
     * @return
     */
    List<Long> selectCompanyAssignOu(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("companyId") Long companyId);

    /**
     * 查询子账户下指定业务实体分配的库存组织信息
     *
     * @param userId
     * @param tenantId
     * @param ouIds
     * @return
     */
    List<Long> selectOuAssignInvOrg(@Param("userId") Long userId, @Param("tenantId") Long tenantId, @Param("ouIds") List<Long> ouIds);
}
