package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.ReportPermission;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表权限资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-11-29 10:57:31
 */
public interface ReportPermissionRepository extends BaseRepository<ReportPermission> {

    /**
     * 分页查询报表权限
     *
     * @param reportId    报表Id
     * @param tenantId    租户Id
     * @param flag        是否包含未指定角色记录
     * @param pageRequest 分页请求
     * @return 分页数据
     */
    Page<ReportPermission> pageReportPermission(Long reportId, Long tenantId, boolean flag, PageRequest pageRequest);

    /**
     * 查询引用数据
     *
     * @param tenantId 租户Id
     * @param roleId   角色Id
     * @param reportId 报表Id
     * @return 数量
     */
    int selectCountByUnique(Long tenantId, Long roleId, Long reportId);

}
