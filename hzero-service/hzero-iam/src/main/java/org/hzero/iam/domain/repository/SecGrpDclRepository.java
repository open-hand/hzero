package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 安全组数据权限资源库
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclRepository extends BaseRepository<SecGrpDcl> {

    /**
     * 查询公司、业务实体、库存信息
     *
     * @param secGrpId              安全组ID
     * @param selfDim 是否是自我管理的权限维度（1-是 0-不是）
     * @return 公司、业务实体、库存信息列表
     */
    List<CompanyOuInvorgNodeDTO> listSecGrpCompanyDcl(Long secGrpId, Integer selfDim, SecGrpDclQueryDTO queryDTO);

    /**
     * 查询分配给指定角色的安全组数据权限（公司实体组织）节点
     *
     * @param secGrpId 安全组ID
     * @return 权限集及权限集上层菜单列表
     */
    List<CompanyOuInvorgNodeDTO> listSecGrpAssignedCompanyDcl(Long secGrpId, SecGrpDclQueryDTO queryDTO);

    /**
     * 查询分配给指定角色的安全组数据权限（公司实体组织）节点，并标志屏蔽状态
     *
     * @param secGrpId 安全组ID
     * @return 权限集及权限集上层菜单列表
     */
    List<CompanyOuInvorgNodeDTO> listRoleSecGrpCompanyDcl(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO);

    /**
     * 通过安全组Id查询安全组所分配的数据权限维度
     *
     * @param secGrpId 安全组Id
     * @return 安全组的数据权限列表
     */
    List<SecGrpDcl> selectBySecGrpId(Long secGrpId);

    SecGrpDcl queryOne(Long secGrpId, String authorityTypeCode);
}
