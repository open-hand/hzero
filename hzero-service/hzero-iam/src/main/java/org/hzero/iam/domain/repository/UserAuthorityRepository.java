package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户权限管理表资源库
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
public interface UserAuthorityRepository extends BaseRepository<UserAuthority> {

    /**
     * 查询公司、业务实体、库存信息
     *
     * @param tenantId
     * @param userId
     * @param dataCode
     * @param dataName
     * @return CompanyOuInvorgNodeDTO
     */
    List<CompanyOuInvorgNodeDTO> listComanyUoInvorg(Long tenantId, Long userId, String dataCode, String dataName);

    /**
     * 根据租户ID和用户ID查询权限头信息
     *
     * @param tenantId
     * @param userId
     * @return typeCodeList
     */
    List<String> listUserAuthorityTypeCode(Long tenantId, Long userId);

    /**
     * 分页查询数据已分配角色列表
     *
     * @param dataId            数据id
     * @param authorityTypeCode 授权码
     * @param userAuthority     其他条件
     * @param pageRequest       分页条件
     * @return
     */
    Page<UserAuthorityDTO> pageDataAssignedUser(Long dataId,
                                                String authorityTypeCode,
                                                UserAuthority userAuthority,
                                                PageRequest pageRequest);

    /**
     * 查询公司、业务实体、库存信息
     *
     * @param tenantId 租户id
     * @param dataCode 权限代码
     * @param dataName 权限名称
     * @return CompanyOuInvorgNodeDTO
     */
    List<CompanyOuInvorgNodeDTO> listComanyUoInvorgAll(Long tenantId, String dataCode, String dataName);

    /**
     * 查询某个用户下已分配安全组的用户权限
     *
     * @param tenantId 租户Id
     * @param userId   用户Id
     * @param secGrpId 安全组Id
     * @return
     */
    List<UserAuthority> listUserAuthorityAssignSecGrp(Long tenantId, Long userId, Long secGrpId);

    /**
     * 通过用户Id和租户Id 查询默认用户权限记录列表
     *
     * @param userId   用户Id
     * @param tenantId 租户Id
     * @return
     */
    List<UserAuthority> listByUserIdAndTenantId(Long userId, Long tenantId);

    /**
     * 处理用户权限数据缓存
     *
     * @param tenantId     租户Id
     * @param userId       用户Id
     * @param authorityTypeCode 权限类型编码
     */
    void processUserAuthorityCache(Long tenantId, Long userId, String authorityTypeCode);

    /**
     * 查询获取用户权限数据
     *
     * @return List<UserAuthority>
     */
    List<UserAuthority> selectDocUserAuth();
}
