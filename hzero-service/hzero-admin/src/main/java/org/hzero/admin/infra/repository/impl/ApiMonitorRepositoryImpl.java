package org.hzero.admin.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.admin.domain.repository.ApiMonitorRepository;
import org.hzero.admin.infra.mapper.ApiMonitorMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 3:26 下午
 */
@Repository
public class ApiMonitorRepositoryImpl extends BaseRepositoryImpl<ApiMonitor> implements ApiMonitorRepository {

    @Autowired
    private ApiMonitorMapper apiMonitorMapper;

    @Override
    public Page<ApiMonitor> pageAndSort(PageRequest pageRequest, Long monitorRuleId, String monitorUrl, String monitorKey) {
        return PageHelper.doPageAndSort(pageRequest, () -> select(new ApiMonitor()
                .setMonitorRuleId(monitorRuleId)
                .setMonitorUrl(monitorUrl)
                .setMonitorKey(monitorKey)));
    }

    @Override
    public void deleteAll() {
        apiMonitorMapper.deleteAll();
    }
}
