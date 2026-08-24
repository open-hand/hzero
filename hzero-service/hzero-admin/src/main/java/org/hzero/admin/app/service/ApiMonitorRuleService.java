package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.ApiMonitorRule;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 5:05 下午
 */
public interface ApiMonitorRuleService {

    String BLACK_MODE = "BLACK";
    String WHITE_MODE = "WHITE";

    /**
     * 分页条件查询
     * @param monitorRuleId
     * @param urlPattern
     * @param pageRequest
     * @return
     */
    Page<ApiMonitorRule> pageAndSort(Long monitorRuleId, String urlPattern, PageRequest pageRequest);

    /**
     * 校验pattern并创建
     * @param apiMonitorRule
     * @return
     */
    ApiMonitorRule validateAndCreate(ApiMonitorRule apiMonitorRule);

    /**
     * 根据id列表批量删除ApiMonitorRule，并清除关联配置及监控信息
     * @param monitorRuleIds
     * @return
     */
    int batchDelete(List<Long> monitorRuleIds);

    /**
     * 应用配置
     * @param monitorRuleIds
     */
    void apply(List<Long> monitorRuleIds);

    /**
     * 通知网关重新加载redis配置
     */
    void notifyGateway();

}
