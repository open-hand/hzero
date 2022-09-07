package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.mybatis.base.BaseRepository;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 3:24 下午
 */
public interface ApiMonitorRepository extends BaseRepository<ApiMonitor> {

    /**
     * 分页查询
     * @param pageRequest
     * @param monitorRuleId
     * @param monitorUrl
     * @param monitorKey
     * @return
     */
    Page<ApiMonitor> pageAndSort(PageRequest pageRequest, Long monitorRuleId, String monitorUrl, String monitorKey);

    /**
     * 清理
     */
    void deleteAll();

}
