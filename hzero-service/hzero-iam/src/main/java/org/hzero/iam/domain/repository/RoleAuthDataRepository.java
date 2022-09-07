package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 角色单据权限管理资源库
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
public interface RoleAuthDataRepository extends BaseRepository<RoleAuthData> {

    /**
     * 查询公司、业务实体、库存信息
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 公司、业务实体、库存信息列表
     */
    List<CompanyOuInvorgNodeDTO> listCompanyUoInvorg(Long tenantId, Long roleId, String dataCode, String dataName);

    /**
     * 分页查询分配角色列表
     *
     * @param dataId       数据id
     * @param roleAuthData 其他条件
     * @param pageRequest  分页条件
     * @return
     */
    Page<RoleAuthDataDTO> pageRoleAuthDataAssignedRole(Long dataId,
                                                       RoleAuthData roleAuthData,
                                                       PageRequest pageRequest);

    /**
     * 查询获取目标角色对应的auth_data_id,若目标角色查询不到 authDataId 则报错处理
     *
     * @param tenantId          租户Id
     * @param roleId            角色Id
     * @param targetRoleId      目标角色Id
     * @param authorityTypeCode 单据类型
     * @return authDataId
     */
    Long selectRoleAuthDataId(Long tenantId, Long roleId, Long targetRoleId, String authorityTypeCode);

    /**
     * 查询角色数据权限
     *
     * @param tenantId          租户Id
     * @param roleId            角色Id
     * @param authorityTypeCode 权限类型编码
     * @return
     */
    RoleAuthData select(Long tenantId, Long roleId, String authorityTypeCode);
}
