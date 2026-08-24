package org.hzero.iam.domain.service.secgrp.authority.dcl;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 安全组数据权限服务管理器
 *
 * @author bo.he02@hand-china.com 2020/04/03 10:44
 */
public interface DclServiceManager {
    /**
     * 查询安全组数据权限详细
     *
     * @param authorityTypeCode 权限类型码
     * @param queryDTO          查询条件
     * @param pageRequest       分页对象
     * @return 数据权限信息
     */
    SecGrpDclDTO querySecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                         @Nonnull SecGrpDclQueryDTO queryDTO,
                                         @Nonnull PageRequest pageRequest);

    /**
     * 查询安全组可分配的数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param queryDTO          查询条件
     * @param pageRequest       分页对象
     * @return 可分配的数据权限
     */
    SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull String authorityTypeCode,
                                                   @Nonnull SecGrpDclQueryDTO queryDTO,
                                                   @Nonnull PageRequest pageRequest);

    /**
     * 查询安全组已分配的数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param queryDTO          查询条件对象
     * @param pageRequest       分页对象
     * @return 已分配的数据权限
     */
    SecGrpDclDTO querySecGrpDclAssignedAuthority(@Nonnull String authorityTypeCode,
                                                 @Nonnull SecGrpDclQueryDTO queryDTO,
                                                 @Nonnull PageRequest pageRequest);

    /**
     * 查询角色安全组已分配的数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param roleId            角色ID
     * @param queryDTO          查询条件对象
     * @param pageRequest       分页对象
     * @return 角色安全组已分配的数据权限
     */
    SecGrpDclDTO queryRoleSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                             @Nonnull Long roleId,
                                             @Nonnull SecGrpDclQueryDTO queryDTO,
                                             @Nonnull PageRequest pageRequest);

    /**
     * 添加安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrp            安全组
     * @param dcl               数据权限头
     * @param dclLines          数据权限行
     */
    void addSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                               @Nonnull SecGrp secGrp,
                               @Nonnull SecGrpDcl dcl,
                               @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 移除安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrp            安全组
     * @param dcl               数据权限头
     * @param dclLines          数据权限行
     */
    void removeSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                  @Nonnull SecGrp secGrp,
                                  @Nonnull SecGrpDcl dcl,
                                  @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 启用安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrp            安全组
     * @param dcl               数据权限
     */
    void enableSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                  @Nonnull SecGrp secGrp,
                                  @Nonnull SecGrpDcl dcl);

    /**
     * 禁用安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrp            安全组
     * @param dcl               数据权限
     */
    void disabledSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                    @Nonnull SecGrp secGrp,
                                    @Nonnull SecGrpDcl dcl);

    /**
     * 创建安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrpId          安全组ID
     * @param dclLines          数据权限行
     */
    void saveSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                @Nonnull Long secGrpId,
                                @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 删除安全组数据权限
     *
     * @param authorityTypeCode 权限类型码
     * @param secGrpId          安全组ID
     * @param dclLines          数据权限行
     */
    void deleteSecGrpDclAuthority(@Nonnull String authorityTypeCode,
                                  @Nonnull Long secGrpId,
                                  @Nonnull List<SecGrpDclLine> dclLines);
}
