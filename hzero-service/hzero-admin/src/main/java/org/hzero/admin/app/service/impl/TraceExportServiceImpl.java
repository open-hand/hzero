package org.hzero.admin.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.admin.api.dto.TraceExport;
import org.hzero.admin.api.dto.TraceReport;
import org.hzero.admin.app.service.TraceExportService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/3/2 2:40 下午
 */
@Service
public class TraceExportServiceImpl implements TraceExportService {

    @Autowired
    private RedisHelper redisHelper;
    @Autowired(required = false)
    @Qualifier("tracerRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public List<TraceExport> export(String traceGroupId) {
        String listKey = TraceAnalysisServiceImpl.TRACE_GROUP_KEY_PREFIX + traceGroupId;
        List<String> logList = exportDistRedis(listKey);
        if (CollectionUtils.isEmpty(logList)) {
            throw new CommonException("hadm.error.export_empty_log");
        }
        TraceReport report = TraceAnalysisServiceImpl.analysis(traceGroupId, logList);
        return transfer(report);
    }

    private List<String> exportDistRedis(String listKey) {
        if (redisTemplate == null) {
            return redisHelper.lstAll(listKey);
        } else {
            return redisTemplate.opsForList().range(listKey, 0L, redisTemplate.opsForList().size(listKey));
        }
    }

    private List<TraceExport> transfer(TraceReport report) {
        List<TraceReport.Trace> traces = report.getTraceList();
        if (!CollectionUtils.isEmpty(traces)) {
            List<TraceExport> exports = new ArrayList<>();
            for (TraceReport.Trace trace : traces) {
                exports.add(transferTraceExport(trace));
            }
            return exports;
        }
        return null;
    }

    private TraceExport transferTraceExport(TraceReport.Trace trace) {
        TraceExport export = new TraceExport();
        BeanUtils.copyProperties(trace, export);
        return export;
    }
}
