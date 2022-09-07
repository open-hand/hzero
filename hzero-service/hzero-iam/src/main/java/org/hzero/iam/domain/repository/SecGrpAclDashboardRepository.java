package org.hzero.iam.domain.repository;

import java.util.List;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 安全组工作台配置资源库
 *
 * @author bojiangzhou 2020/02/17
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclDashboardRepository extends BaseRepository<SecGrpAclDashboard> {

    /**
     * 分页查询安全组工作台配置
     *
     * @param secGrpId    安全组ID
     * @param pageRequest 分页参数
     * @return 分页数据
     */
    Page<SecGrpAclDashboard> listSecGrpDashboard(@Nullable Long tenantId, @NotNull Long secGrpId, SecGrpAclDashboard queryDTO, PageRequest pageRequest);

    /**
     * 分页查询安全组工作台配置
     *
     * @param secGrpId    安全组ID
     * @param pageRequest 分页参数
     * @return 分页数据
     */
    Page<SecGrpAclDashboard> listSecGrpAssignableDashboard(@NotNull Long secGrpId, SecGrpAclDashboard queryDTO, PageRequest pageRequest);

    /**
     * 查询供工作台配置
     *
     * @param secGrpIds 安全组ID列表
     */
    List<SecGrpAclDashboard> listDashboardBySecGrpIds(List<Long> secGrpIds);

}
