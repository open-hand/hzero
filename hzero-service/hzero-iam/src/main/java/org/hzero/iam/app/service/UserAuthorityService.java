package org.hzero.iam.app.service;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.ResponseCompanyOuInvorgDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;
import org.hzero.iam.domain.entity.UserAuthority;
import org.hzero.iam.domain.entity.UserAuthorityLine;

/**
 * 用户权限管理表应用服务
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
public interface UserAuthorityService {
    /**
     * 查询公司、业务单元、库存组织代码
     *
     * @param tenantId
     * @param userId
     * @param dataCode
     * @param dataName
     * @return CompanyOuInvorgDTO
     */
    ResponseCompanyOuInvorgDTO listComanyOuInvorg(Long tenantId, Long userId, String dataCode,
                                                  String dataName);

    /**
     * 批量保存用户数据权限
     *
     * @param tenantId
     * @param userId
     * @param authorityTypeCode
     * @param userAuthorityDTO
     * @return UserAuthorityDTO
     */
    UserAuthorityDTO batchCreateUserAuthority(Long tenantId, Long userId, String authorityTypeCode,
                                              UserAuthorityDTO userAuthorityDTO);

    /**
     * 批量创建用户数据权限
     *
     * @param userAuthorityDtos
     * @return
     */
    List<UserAuthorityDTO> batchCreateUserAuthority(List<UserAuthorityDTO> userAuthorityDtos);


    void copyUserAuthority(Long tenantId, Long userId, List<Long> copyUserIdList);


    /**
     * 用户权限交换
     *
     * @param tenantId
     * @param userId
     * @param exchangeUserId
     * @return UserAuthority
     */
    List<UserAuthority> exchangeUserAuthority(Long tenantId, Long userId, Long exchangeUserId);

    /**
     * 批量查询用户数据权限
     *
     * @param tenantId
     * @param userId
     * @param authorityTypeCode
     * @param dataCode
     * @param dataName
     * @return UserAuthorityDTO
     */
    UserAuthorityDTO selectCreateUserAuthority(Long tenantId, Long userId, String authorityTypeCode, String dataCode,
                                               String dataName, PageRequest pageRequest);

    /**
     * 保存公司、业务单元、库存组织数据权限
     *
     * @param tenantId
     * @param userId
     * @param companyOuInvorgDTOList
     * @return
     */
    List<CompanyOuInvorgDTO> createTreeUserAuthority(Long tenantId, Long userId,
                                                     List<CompanyOuInvorgDTO> companyOuInvorgDTOList);

    /**
     * 查询公司、业务单元、库存组织代码
     *
     * @param tenantId
     * @param dataCode
     * @param dataName
     * @return CompanyOuInvorgDTO
     */
    ResponseCompanyOuInvorgDTO listComanyOuInvorgAll(Long tenantId, String dataCode,
                                                     String dataName);

    /**
     * 批量删除
     *
     * @param tenantId              租户ID
     * @param userId                用户ID
     * @param userAuthorityLineList 用户权限数据行
     */
    void batchDeleteUserAuthorityLines(Long tenantId, Long userId, List<UserAuthorityLine> userAuthorityLineList);

}
