package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.api.dto.UserAuthorityDataDTO;
import org.hzero.iam.domain.entity.UserAuthorityLine;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户权限管理行表资源库
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
public interface UserAuthorityLineRepository extends BaseRepository<UserAuthorityLine> {

    /**
     * 分页查询用户权限数据
     *
     * @param authorityId
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @param pageRequest
     * @return UserAuthorityLine
     */
    Page<UserAuthorityLine> selectCreateUserAuthorityLines(Long authorityId, Long tenantId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 用户权限数据更新
     *
     * @param tenantId
     * @param authorityId
     * @param copyAuthorityId
     */
    void updateUserAuthorityLine(Long tenantId, Long authorityId, Long copyAuthorityId);

    /**
     * 分页查询租用户数据权限采购组织
     *
     * @param tenantId
     * @param userId
     * @param dataCode
     * @param dataName
     * @param pageRequest
     * @return
     */
    Page<UserAuthorityDataDTO> listPurOrg(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);


    /**
     * 分页查询租用户数据权限采购组织
     *
     * @param tenantId
     * @param userId
     * @param dataCode
     * @param dataName
     * @param pageRequest
     * @return
     */
    Page<UserAuthorityDataDTO> listPurAgent(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);


    /**
     * 分页查询租用户数据权限采购品类
     *
     * @param tenantId
     * @param userId
     * @param dataCode
     * @param dataName
     * @param pageRequest
     * @return
     */
    Page<UserAuthorityDataDTO> listPurCat(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询用户数据权限值集
     *
     * @param tenantId    租户ID
     * @param userId      用户ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 用户数据权限值集列表
     */
    Page<UserAuthorityDataDTO> pageLov(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询用户数据权限值集视图
     *
     * @param tenantId    租户ID
     * @param userId      用户ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 用户数据权限值集视图列表
     */
    Page<UserAuthorityDataDTO> pageLovView(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询用户数据权限数据源
     *
     * @param tenantId    租户ID
     * @param userId      用户ID
     * @param dataCode    数据编码
     * @param dataName    数据名称
     * @param pageRequest 分页信息
     * @return 用户数据权限值集视图列表
     */
    Page<UserAuthorityDataDTO> pageDatasource(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 分页查询数据组
     * @param tenantId 租户id
     * @param userId 用户id
     * @param groupCode 数据组代码
     * @param groupName 数据组名称
     * @param pageRequest 分页查询条件
     * @return
     */
    Page<UserAuthorityDataDTO> pageGroupData(Long tenantId, Long userId, String groupCode, String groupName, PageRequest pageRequest);

    /**
     * 分页查询租用户数据权限采购品类
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @param pageRequest
     * @return
     */
    Page<UserAuthorityDataDTO> listPurCatAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest);


    /**
     * 更新用户权限行的数据来源字段
     *
     * @param userAuthorityId 用户权限Id
     * @param dataSouce 数据来源值
     */
    void updateUserAuthorityLineDataSource(Long userAuthorityId,String dataSouce);


    /**
     * 查询子账户下指定公司分配的ou信息
     *
     * @param userId
     * @param tenantId
     * @param companyId
     * @return
     */
    List<Long> selectCompanyAssignOu(Long userId, Long tenantId, Long companyId);

    /**
     * 查询子账户下指定业务实体分配的库存组织信息
     *
     * @param userId
     * @param tenantId
     * @param ouIds
     * @return
     */
    List<Long> selectOuAssignInvOrg(Long userId, Long tenantId, List<Long> ouIds);
}
