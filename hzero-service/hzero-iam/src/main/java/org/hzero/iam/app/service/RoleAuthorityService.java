package org.hzero.iam.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.RoleAuthorityDTO;

/**
 * 角色数据权限定义应用服务
 *
 * @author mingke.yan@hand-china.com 2018-08-07 16:30:26
 */
public interface RoleAuthorityService {

    /**
     * 批量新增或保存数据权限
     *
     * @param roleId
     * @param roleAuthorityDtos
     */
    void batchCreateOrUpdateRoleAuthority(Long roleId, List<RoleAuthorityDTO> roleAuthorityDtos);

    /**
     * 分页查询角色单据权限
     *
     * @param pageRequest 分页请求
     * @param roleId      角色ID
     * @param docTypeName 单据类型名称
     * @param docTypeCode 单据类型编码
     * @param tenantId    租户ID
     * @return 单据权限
     */
    Page<RoleAuthorityDTO> pageList(PageRequest pageRequest, Long roleId, Long tenantId, String docTypeName, String docTypeCode);

    /**
     * 删除角色数据权限单据
     *
     * @param roleAuthId
     */
    void deleteRoleAuthority(Long roleAuthId);

    /**
     * 分页查询单据权限分给了的角色
     *
     * @param tenantId    租户id
     * @param docTypeId   单据权限ID
     * @param pageRequest 分页查询
     * @param roleCode    角色代码
     * @return
     */
    Page<RoleAuthorityDTO> pageDocTypeAssignRoles(Long tenantId,
                                                  Long docTypeId,
                                                  String roleCode,
                                                  PageRequest pageRequest);


    /**
     * 批量保存权限
     *
     * @param roleAuthorityDtos 权限信息
     */
    void batchCreateOrUpdateRoleAuthority(List<RoleAuthorityDTO> roleAuthorityDtos);



}
