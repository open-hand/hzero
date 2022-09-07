package org.hzero.iam.app.service;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.ResponseCompanyOuInvorgDTO;
import org.hzero.iam.api.dto.RoleAuthDataDTO;

import java.util.List;

/**
 * 角色单据权限管理应用服务
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
public interface RoleAuthDataService {

    /**
     * 分页查询角色单据权限行
     *
     * @param tenantId          租户ID
     * @param roleId            角色ID
     * @param authorityTypeCode 权限类型编码
     * @param dataCode          数据编码
     * @param dataName          数据名称
     * @param pageRequest       分页信息
     * @return 角色单据权限行列表
     */
    RoleAuthDataDTO pageRoleAuthDataLine(Long tenantId, Long roleId, String authorityTypeCode, String dataCode, String dataName, PageRequest pageRequest);

    /**
     * 新建角色单据权限行
     *
     * @param roleAuthDataDTO 角色单据权限
     * @return 角色单据权限
     */
    RoleAuthDataDTO createRoleAuthDataLine(RoleAuthDataDTO roleAuthDataDTO);

    /**
     * 角色树形查询公司/业务实体/库存组织权限
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 树形公司/业务实体/库存组织权限
     */
    ResponseCompanyOuInvorgDTO treeRoleAuthority(Long tenantId, Long roleId, String dataCode, String dataName);

    /**
     * 角色树形保存公司/业务实体/库存组织权限
     *
     * @param tenantId               租户ID
     * @param roleId                 角色ID
     * @param companyOuInvorgDTOList 公司/业务实体/库存组织
     * @return 树形保存公司/业务实体/库存组织权限
     */
    List<CompanyOuInvorgDTO> createRoleAuthority(Long tenantId, Long roleId, List<CompanyOuInvorgDTO> companyOuInvorgDTOList);

    /**
     * 角色树形保存公司/业务实体/库存组织权限
     * @param tenantId 租户id
     * @param companyOuInvorgDTOList 公司列表
     * @return
     */
    List<CompanyOuInvorgDTO> createAuthorityForRoles(Long tenantId, List<CompanyOuInvorgDTO> companyOuInvorgDTOList);

    /**
     * 复制角色权限
     *
     * @param organizationId 租户Id
     * @param roleId 角色Id
     * @param copyRoleIdList 复制的角色Ids
     */
    void copyRoleAuthority(Long organizationId, Long roleId, List<Long> copyRoleIdList);
}
