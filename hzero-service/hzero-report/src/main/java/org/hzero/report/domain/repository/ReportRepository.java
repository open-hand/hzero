package org.hzero.report.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.Report;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表信息资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface ReportRepository extends BaseRepository<Report> {

    /**
     * 获取报表定义列表
     *
     * @param pageRequest 分页
     * @param report      报表参数
     * @return 分页结果
     */
    Page<Report> selectReportDesigners(PageRequest pageRequest, Report report);

    /**
     * 获取报表信息（同时要判断当前租户是否有该报表的权限）
     *
     * @param reportId 报表ID
     * @param tenantId 租户Id
     * @return 分页结果
     */
    Report selectReportById(Long reportId, Long tenantId);

    /**
     * 获取报表定义列表(仅租户级调用，查询自己租户和平台分配了权限的数据)
     *
     * @param pageRequest    分页
     * @param reportCode     报表编码
     * @param reportName     报表名称
     * @param reportTypeCode 报表类型
     * @param tenantId       租户Id
     * @param nowDate        时间
     * @return 分页结果
     */
    Page<Report> selectTenantReportDesigners(PageRequest pageRequest, String reportCode, String reportName, String reportTypeCode, Long tenantId, LocalDate nowDate);

    /**
     * 获取报表定义明细
     *
     * @param datasetId 数据集Id
     * @param tenantId  租户Id
     * @return 报表明细
     */
    Report selectReportDesigner(Long datasetId, Long tenantId);

    /**
     * 获取报表预览汇总信息
     *
     * @param pageRequest 分页
     * @param report      报表参数
     * @param roleIds     角色列表
     * @param tenantId    租户Id
     * @return 分页结果
     */
    Page<Report> selectReports(PageRequest pageRequest, Report report, List<Long> roleIds, Long tenantId);

    /**
     * 获取报表详细
     *
     * @param reportKey 报表Key  uuid可以跨租户，使用编码则当前租户要与报表定义租户匹配
     * @param tenantId  租户Id
     * @return Report
     */
    Report selectReport(String reportKey, Long tenantId);

    /**
     * 获取报表详细
     *
     * @param tenantId  租户ID
     * @param reportKey 报表Key
     * @return Report
     */
    Report selectReportIgnorePermission(Long tenantId, String reportKey);

    /**
     * 获取报表元数据
     *
     * @param reportKey 报表key  uuid可以跨租户，使用编码则当前租户要与报表定义租户匹配
     * @param tenantId  租户Id
     * @return 元数据
     */
    Report selectReportMateData(String reportKey, Long tenantId);

    /**
     * 查询数据集关联的报表
     *
     * @param pageRequest 分页
     * @param datasetId   数据集Id
     * @param tenantId    租户Id
     * @return 报表
     */
    Page<Report> pageReportByDataSet(PageRequest pageRequest, Long datasetId, Long tenantId);

}
