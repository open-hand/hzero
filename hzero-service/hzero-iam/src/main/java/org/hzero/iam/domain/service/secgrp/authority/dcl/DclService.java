package org.hzero.iam.domain.service.secgrp.authority.dcl;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 安全组数据权限服务
 *
 * @author bergturing 2020/04/03 10:29
 */
public interface DclService {
    /**
     * 判断当前Service是否可用于处理对应的数据权限类型
     *
     * @param authorityTypeCode 权限类型码
     * @param docTypeDimension  单据类型维度对象
     * @return 是否是支持的类型
     */
    boolean support(@Nonnull String authorityTypeCode, @Nullable DocTypeDimension docTypeDimension);

    /**
     * 查询安全组数据权限详细
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页对象
     * @return 数据权限信息
     */
    SecGrpDclDTO querySecGrpDclAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest);

    /**
     * 查询安全组可分配的数据权限
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页对象
     * @return 可分配的数据权限
     */
    SecGrpDclDTO querySecGrpDclAssignableAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest);

    /**
     * 查询安全组已分配的数据权限
     *
     * @param queryDTO    查询条件对象
     * @param pageRequest 分页对象
     * @return 已分配的数据权限
     */
    SecGrpDclDTO querySecGrpDclAssignedAuthority(@Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest);

    /**
     * 查询角色安全组已分配的数据权限
     *
     * @param roleId      角色ID
     * @param queryDTO    查询条件对象
     * @param pageRequest 分页对象
     * @return 角色安全组已分配的数据权限
     */
    SecGrpDclDTO queryRoleSecGrpDclAuthority(@Nonnull Long roleId, @Nonnull SecGrpDclQueryDTO queryDTO, @Nonnull PageRequest pageRequest);

    /**
     * 添加安全组数据权限
     *
     * @param secGrp   安全组
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void addSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 移除安全组数据权限
     *
     * @param secGrp   安全组
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void removeSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 启用安全组数据权限
     *
     * @param secGrp 安全组
     * @param dcl    数据权限
     */
    void enableSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl);

    /**
     * 禁用安全组数据权限
     *
     * @param secGrp 安全组
     * @param dcl    数据权限
     */
    void disabledSecGrpDclAuthority(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl);

    /**
     * 创建安全组数据权限
     *
     * @param secGrpId 安全组ID
     * @param dclLines 数据权限行
     */
    void saveSecGrpDclAuthority(@Nonnull Long secGrpId, @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 删除安全组数据权限
     *
     * @param secGrpId 安全组ID
     * @param dclLines 数据权限行
     */
    void deleteSecGrpDclAuthority(@Nonnull Long secGrpId, @Nonnull List<SecGrpDclLine> dclLines);

    /**
     * 清理数据，主要用于清理服务使用过程中的一些额外资源，比如线程变量
     */
    void clear();
}
