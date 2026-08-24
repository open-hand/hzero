package org.hzero.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpAuthorityDTO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 安全组应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
public interface SecGrpService {

    /**
     * 创建安全组
     *
     * @param tenantId 租户ID
     * @param secGrp   安全组
     * @return 新建的安全组
     */
    SecGrp createSecGrp(@NotNull Long tenantId, @NotNull SecGrp secGrp);

    /**
     * 复制安全组
     *
     * @param tenantId       租户ID
     * @param sourceSecGrpId 来源安全组ID
     * @param secGrp         安全组
     * @return 新建的安全组
     */
    SecGrp copySecGrp(@NotNull Long tenantId, @NotNull Long sourceSecGrpId, @NotNull SecGrp secGrp);

    /**
     * 快速创建安全组，可选择多个安全组合并创建
     *
     * @param tenantId        租户ID
     * @param sourceSecGrpIds 来源安全组ID列表
     * @param secGrp          安全组
     * @param roleId          当前操作的角色ID
     * @return 新建的安全组
     */
    SecGrp quickCreateSecGrp(Long tenantId, List<Long> sourceSecGrpIds, SecGrp secGrp, Long roleId);

    /**
     * 更新安全组
     *
     * @param secGrp 安全组
     * @return 安全组
     */
    SecGrp updateSecGrp(Long tenantId, SecGrp secGrp);

    /**
     * 删除草稿状态的安全组
     *
     * @param tenantId 租户IDID
     * @param secGrpId 安全组ID
     */
    void deleteDraftSecGrp(@Nullable Long tenantId, @NotNull Long secGrpId);

    /**
     * 安全组编码重名校验 唯一性索引 (tenant_id, code, level)
     */
    void checkDuplicate(Long tenantId, String level, String secGrpCode);

    /**
     * 查询安全组中的所有权限
     *
     * @param secGrpId 安全组Id
     * @return 权限对象
     */
    SecGrpAuthorityDTO selectSecGrpAuthorityInSecGrp(Long secGrpId);

    /**
     * 查询子账户所分配的安全组列表
     *
     * @param userId         子账户Id
     * @param secGrpQueryDTO 查询条件
     * @param pageRequest    分页信息
     */
    Page<SecGrp> pageUserAssignedSecGrp(Long userId, SecGrpQueryDTO secGrpQueryDTO, PageRequest pageRequest);
}
