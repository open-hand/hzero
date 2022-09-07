package org.hzero.report.infra.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.Report;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表信息Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-10-22 16:35:10
 */
public interface ReportMapper extends BaseMapper<Report> {

    /**
     * 获取报表定义列表
     *
     * @param report 报表
     * @return 查询结果
     */
    List<Report> selectReportDesigners(Report report);

    /**
     * 获取报表信息（同时要判断当前租户是否有该报表的权限）
     *
     * @param reportId 报表ID
     * @param tenantId 租户Id
     * @param nowDate  时间
     * @return 分页结果
     */
    Report selectReportById(@Param("reportId") Long reportId,
                            @Param("tenantId") Long tenantId,
                            @Param("nowDate") LocalDate nowDate);

    /**
     * 获取报表定义列表(仅租户级调用，查询自己租户和平台分配了权限的数据)
     *
     * @param reportCode     报表编码
     * @param reportName     报表名称
     * @param reportTypeCode 报表类型
     * @param tenantId       租户Id
     * @param nowDate        时间
     * @return 分页结果
     */
    List<Report> selectTenantReportDesigners(@Param("reportCode") String reportCode,
                                             @Param("reportName") String reportName,
                                             @Param("reportTypeCode") String reportTypeCode,
                                             @Param("tenantId") Long tenantId,
                                             @Param("nowDate") LocalDate nowDate);

    /**
     * 获取报表定义明细
     *
     * @param reportId 报表Id
     * @param tenantId 租户Id
     * @return 明细
     */
    Report selectReportDesigner(@Param("reportId") Long reportId,
                                @Param("tenantId") Long tenantId);

    /**
     * 获取报表预览信息
     *
     * @param roleIds  角色Id
     * @param tenantId 租户Id
     * @param report   报表
     * @param nowDate  现在的时间
     * @return 分页
     */
    Page<Report> selectReports(@Param("roleIds") List<Long> roleIds,
                               @Param("tenantId") Long tenantId,
                               @Param("report") Report report,
                               @Param("nowDate") LocalDate nowDate);

    /**
     * 获取报表详细
     *
     * @param reportUuid     uuid
     * @param reportCode     报表编码
     * @param tenantId       租户Id
     * @param reportTenantId 报表租户
     * @param roleIds        角色Id
     * @param nowDate        现在的时间
     * @return 报表
     */
    Report selectReport(@Param("reportUuid") String reportUuid,
                        @Param("reportCode") String reportCode,
                        @Param("tenantId") Long tenantId,
                        @Param("reportTenantId") Long reportTenantId,
                        @Param("roleIds") List<Long> roleIds,
                        @Param("nowDate") LocalDate nowDate);

    /**
     * 获取报表详细
     *
     * @param reportUuid uuid
     * @param reportCode 报表编码
     * @param tenantId   租户Id
     * @return 报表
     */
    Report selectReportIgnorePermission(@Param("reportUuid") String reportUuid,
                                        @Param("reportCode") String reportCode,
                                        @Param("tenantId") Long tenantId);

    /**
     * 获取报表元数据
     *
     * @param reportUuid     uuid
     * @param reportCode     报表编码
     * @param tenantId       租户Id
     * @param reportTenantId 报表租户
     * @param roleIds        角色Id
     * @param nowDate        现在的时间
     * @return 元数据
     */
    Report selectReportMateData(@Param("reportUuid") String reportUuid,
                                @Param("reportCode") String reportCode,
                                @Param("tenantId") Long tenantId,
                                @Param("reportTenantId") Long reportTenantId,
                                @Param("roleIds") List<Long> roleIds,
                                @Param("nowDate") LocalDate nowDate);

    /**
     * 查询数据集关联的报表
     *
     * @param datasetId 数据集Id
     * @param tenantId  租户Id
     * @return 报表
     */
    List<Report> listReportByDataSet(@Param("datasetId") Long datasetId,
                                     @Param("tenantId") Long tenantId);

}
