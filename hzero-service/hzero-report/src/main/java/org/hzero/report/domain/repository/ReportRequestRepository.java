package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.ReportRequest;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表请求资源库
 *
 * @author xianzhi.chen@hand-china.com 2019-01-25 14:21:02
 */
public interface ReportRequestRepository extends BaseRepository<ReportRequest> {

    /**
     * 分页查询报表请求
     *
     * @param pageRequest   分页请求对象
     * @param reportRequest 报表请求
     * @return Page<ReportRequest>
     */
    Page<ReportRequest> selectReportRequests(PageRequest pageRequest, ReportRequest reportRequest);

    /**
     * 查询报表请求明细
     *
     * @param requestId 报表ID
     * @return ReportRequest
     */
    ReportRequest selectReportRequest(Long requestId);

}
