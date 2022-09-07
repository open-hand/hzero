package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.TraceExport;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/3/2 2:39 下午
 */
public interface TraceExportService {

    List<TraceExport> export(String traceGroupId);

}
