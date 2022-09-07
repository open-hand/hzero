package org.hzero.report.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.ReportRequest;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表请求Mapper
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
public interface ReportRequestMapper extends BaseMapper<ReportRequest> {

    /**
     * 查询报表请求列表
     * 文件生成的开始时间在权限有效期内都可以查询到
     *
     * @param roleIds       角色Id
     * @param tenantId      租户Id
     * @param reportRequest 报表请求实体类
     * @return Page<ReportRequest>
     */
    Page<ReportRequest> selectReportRequests(@Param("roleIds") List<Long> roleIds,
                                             @Param("tenantId") Long tenantId,
                                             @Param("reportRequest") ReportRequest reportRequest);

    /**
     * 查询报表请求明细
     *
     * @param requestId 报表请求ID
     * @return ReportRequest 报表请求实体类
     */
    ReportRequest selectReportRequest(@Param("requestId") Long requestId);

}
