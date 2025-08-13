package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ApiMonitor;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/17 8:24 下午
 */
public interface ApiMonitorService {

    /**
     * 分页条件查询
     * @param pageRequest
     * @param monitorRuleId
     * @param monitorUrl
     * @param monitorKey
     * @return
     */
    Page<ApiMonitor> pageAndSort(PageRequest pageRequest, Long monitorRuleId, String monitorUrl, String monitorKey);

    /**
     * 加入黑名单
     * @param id monitorRuleId
     * @param ip
     */
    void blacklist(Long id, String ip);
}
