package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.app.service.ApiMonitorRuleService;
import org.hzero.admin.app.service.ApiMonitorService;
import org.hzero.admin.domain.entity.ApiLimit;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.admin.domain.repository.ApiLimitRepository;
import org.hzero.admin.domain.repository.ApiMonitorRepository;
import org.hzero.core.redis.DynamicRedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/17 8:25 下午
 */
@Service
public class ApiMonitorServiceImpl implements ApiMonitorService {

    private static final int BLACKLIST_DB = 1;
    private static final String BLACK_MODE = "BLACK";
    private static final String WHITE_MODE = "WHITE";

    @Lazy
    @Autowired
    private ApiMonitorRepository apiMonitorRepository;
    @Lazy
    @Autowired
    private ApiLimitRepository apiLimitRepository;
    @Lazy
    @Autowired
    private ApiMonitorRuleService apiMonitorRuleService;
    @Autowired
    private DynamicRedisHelper redisHelper;

    @Override
    public Page<ApiMonitor> pageAndSort(PageRequest pageRequest, Long monitorRuleId, String monitorUrl, String monitorKey) {
        Page<ApiMonitor> page = apiMonitorRepository.pageAndSort(pageRequest, monitorRuleId, monitorUrl, monitorKey);
        addFlagIfInBlacklist(page);
        return page;
    }

    @Override
    public void blacklist(Long id, String ip) {
        ApiLimit apiLimit = apiLimitRepository.selectOne(new ApiLimit().setMonitorRuleId(id));
        if (apiLimit == null) {
            apiLimit = new ApiLimit();
            apiLimit.setListMode(BLACK_MODE);
            apiLimit.setValueList(ip);
            apiLimitRepository.insertSelective(apiLimit);
        } else {
            if (BLACK_MODE.equals(apiLimit.getListMode())){
                String blacklist = apiLimit.getValueList();
                if (StringUtils.isNotEmpty(blacklist)){
                    String[] list = blacklist.split(",");
                    boolean exist = false;
                    for (String ipValue : list) {
                        if (ipValue.equals(ip)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        blacklist = blacklist + "," + ip;
                    }
                } else {
                    blacklist = ip;
                }
                apiLimit.setValueList(blacklist);
            }
            apiLimitRepository.updateByPrimaryKey(apiLimit);
        }

        try {
            redisHelper.setCurrentDatabase(BLACKLIST_DB);
            redisHelper.setAdd("hadm:blacklist:" + id + ":value", new String[]{ip});
        }finally {
            redisHelper.clearCurrentDatabase();
        }
        apiMonitorRuleService.notifyGateway();
    }

    private void addFlagIfInBlacklist(List<ApiMonitor> apiMonitors) {
        try {
            redisHelper.setCurrentDatabase(BLACKLIST_DB);
            for (ApiMonitor apiMonitor : apiMonitors){
                Long id = apiMonitor.getMonitorRuleId();
                String ip = apiMonitor.getMonitorKey();
                if (redisHelper.setIsmember("hadm:blacklist:" + id + ":value", ip)){
                    apiMonitor.setInBlacklist(true);
                } else {
                    apiMonitor.setInBlacklist(false);
                }
            }
        }finally {
            redisHelper.clearCurrentDatabase();
        }
    }

}
