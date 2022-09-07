package org.hzero.admin.app.service;

import org.hzero.admin.api.dto.TraceReport;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/23 3:55 下午
 */
public interface TraceAnalysisService {

    void start();

    TraceReport end();

    boolean ifStarted();

}
