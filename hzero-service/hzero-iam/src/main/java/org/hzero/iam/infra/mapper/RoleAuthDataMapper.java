package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthority;

import java.util.List;

/**
 * 角色单据权限管理Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-06-14 11:49:29
 */
public interface RoleAuthDataMapper extends BaseMapper<RoleAuthData> {

    /**
     * 查询公司、业务实体、库存信息
     *
     * @param tenantId 租户ID
     * @param roleId   角色ID
     * @param dataCode 数据编码
     * @param dataName 数据名称
     * @return 公司、业务实体、库存信息列表
     */
    List<CompanyOuInvorgNodeDTO> listCompanyUoInvorg(@Param("tenantId") Long tenantId,
                                                     @Param("roleId") Long roleId,
                                                     @Param("dataCode") String dataCode,
                                                     @Param("dataName") String dataName);

    /**
     * 查询权限列表
     *
     * @param dataId            数据id
     * @param authorityTypeCode 权限代码
     * @param roleAuthData      其他查询条件
     * @return
     */
    List<RoleAuthData> listAuthDataAssignedRole(@Param("dataId") Long dataId,
                                                @Param("authorityTypeCode") String authorityTypeCode,
                                                @Param("roleAuthData") RoleAuthData roleAuthData);

    /**
     * 查询角色数据权限
     *
     * @param tenantId          租户Id
     * @param roleId            角色Id
     * @param authorityTypeCode 权限类型编码
     * @return
     */
    RoleAuthData selectByUniqueKey(@Param("tenantId") Long tenantId, @Param("roleId") Long roleId, @Param("authorityTypeCode") String authorityTypeCode);
}
