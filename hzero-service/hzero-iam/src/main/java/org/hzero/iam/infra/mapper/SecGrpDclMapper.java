package org.hzero.iam.infra.mapper;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDcl;

/**
 * 安全组数据权限Mapper
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclMapper extends BaseMapper<SecGrpDcl> {
    /**
     * 查询公司、业务实体、库存信息
     *
     * @return 公司、业务实体、库存信息列表
     */
    List<CompanyOuInvorgNodeDTO> selectSecGrpCompanyDcl(SecGrpDclQueryDTO queryDTO);

    /**
     * 查询分配给指定角色的安全组数据权限（公司实体组织）节点，并标志屏蔽状态
     *
     * @return 权限集及权限集上层菜单列表
     */
    List<CompanyOuInvorgNodeDTO> selectRoleSecGrpCompanyDcl(SecGrpDclQueryDTO queryDTO);

    /**
     * 查询分配给指定角色的安全组数据权限（公司实体组织）节点
     *
     * @return 权限集及权限集上层菜单列表
     */
    List<CompanyOuInvorgNodeDTO> selectSecGrpAssignedCompanyDcl(SecGrpDclQueryDTO queryDTO);
}
