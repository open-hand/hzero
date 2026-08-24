package org.hzero.iam.app.service;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDclLine;

/**
 * 安全组数据权限应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclService {

    /**
     * 查询安全组单据权限行
     *
     * @param secGrpId          安全组ID
     * @param pageRequest       分页信息
     * @return 角色单据权限行列表
     */
    SecGrpDclDTO querySecGrpDclAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询安全组单据权限行-
     *
     * @param secGrpId          安全组ID
     * @param pageRequest       分页信息
     * @return 角色单据权限行列表
     */
    SecGrpDclDTO querySecGrpDclAssignableAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询安全组单据权限行
     *
     * @param secGrpId          安全组ID
     * @param pageRequest       分页信息
     * @return 角色单据权限行列表
     */
    SecGrpDclDTO querySecGrpDclAssignedAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询角色安全组的数据权限
     */
    SecGrpDclDTO queryRoleSecGrpDclAuthority(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 查询用户安全组的数据权限
     */
    SecGrpDclDTO queryUserSecGrpDclAuthority(Long userId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest);

    /**
     * 创建安全组数据权限
     *
     * @param secGrpId          安全组ID
     * @param authorityTypeCode 权限类型
     * @param dclLines          数据权限行
     */
    void saveSecGrpDclAuthority(Long secGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines);

    /**
     * 删除安全组数据权限
     *
     * @param secGrpId          安全组ID
     * @param authorityTypeCode 权限类型
     * @param dclLines          数据权限行
     */
    void deleteSecGrpDclAuthority(Long secGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines);


}
