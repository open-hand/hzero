package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.ApiMonitorRuleService;
import org.hzero.admin.domain.entity.ApiLimit;
import org.hzero.admin.domain.entity.ApiMonitor;
import org.hzero.admin.domain.entity.ApiMonitorRule;
import org.hzero.admin.domain.repository.ApiLimitRepository;
import org.hzero.admin.domain.repository.ApiMonitorRepository;
import org.hzero.admin.domain.repository.ApiMonitorRuleRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.endpoint.HttpTransporter;
import org.hzero.core.endpoint.client.BaseHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StaticEndpointHttpRequest;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/9 5:11 下午
 */
@Service
public class ApiMonitorRuleServiceImpl implements ApiMonitorRuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiMonitorRuleServiceImpl.class);

    private static final String BLACKLIST_VALUE_KEY = "hadm:blacklist:value";
    private static final String WHITELIST_VALUE_KEY = "hadm:whitelist:value";
    private static final String ID_PLACEHOLDER = "{id}";
    private static final String BLACKLIST_ID_VALUE_KEY = "hadm:blacklist:{id}:value";
    private static final String WHITELIST_ID_VALUE_KEY = "hadm:whitelist:{id}:value";
    private static final String IDS_KEY = "hadm:api:monitor:ids";
    private static final String PATTERNS_KEY = "hadm:api:monitor:patterns";
    private static final String WINDOWS_KEY = "hadm:api:monitor:windows";
    private static final String BLACKLIST_THRESHOLD_KEY = "hadm:api:monitor:blacklist-threshold";
    private static final Pattern PATTERN = Pattern.compile("^/([A-Za-z0-9_?*/-]*)");

    private String getBlacklistKey(Long id){
        return BLACKLIST_ID_VALUE_KEY.replace(ID_PLACEHOLDER, String.valueOf(id));
    }

    private String getWhitelistKey(Long id){
        return WHITELIST_ID_VALUE_KEY.replace(ID_PLACEHOLDER, String.valueOf(id));
    }

    @Lazy
    @Autowired
    private ApiMonitorRuleRepository apiMonitorRuleRepository;
    @Lazy
    @Autowired
    private ApiLimitRepository apiLimitRepository;
    @Lazy
    @Autowired
    private ApiMonitorRepository apiMonitorRepository;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private DiscoveryClient discoveryClient;
    private HttpTransporter<Long[]> httpTransporter = new BaseHttpTransporter<>();

    @Override
    public Page<ApiMonitorRule> pageAndSort(Long monitorRuleId, String urlPattern, PageRequest pageRequest) {
        return apiMonitorRuleRepository.pageAndSort(pageRequest, new ApiMonitorRule().setMonitorRuleId(monitorRuleId).setUrlPattern(urlPattern));
    }

    @Override
    public ApiMonitorRule validateAndCreate(ApiMonitorRule apiMonitorRule) {
        String urlPattern = apiMonitorRule.getUrlPattern();
        if (!validateUrl(urlPattern)) {
            throw new CommonException("hadm.error.wrong_pattern");
        }
        if (!validateUnique(urlPattern)) {
            throw new CommonException("hadm.error.repeat_pattern");
        }
        apiMonitorRuleRepository.insertSelective(apiMonitorRule);
        return apiMonitorRule;
    }

    private boolean validateUnique(String urlPattern) {
        ApiMonitorRule queryParam = new ApiMonitorRule();
        queryParam.setUrlPattern(urlPattern);
        List<ApiMonitorRule> query = apiMonitorRuleRepository.select(queryParam);
        return CollectionUtils.isEmpty(query);
    }

    private boolean validateUrl(CharSequence input){
        Matcher matcher = PATTERN.matcher(input);
        return matcher.matches();
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public int batchDelete(List<Long> monitorRuleIds) {

        int ruleCount = apiMonitorRuleRepository.batchDeleteByPrimaryKey(monitorRuleIds.stream()
                .map(id -> new ApiMonitorRule().setMonitorRuleId(id))
                .collect(Collectors.toList()));

        int limitCount = apiLimitRepository.batchDelete(monitorRuleIds.stream()
                .map(id -> new ApiLimit().setMonitorRuleId(id))
                .collect(Collectors.toList()));

        int monitorCount = apiMonitorRepository.batchDelete(monitorRuleIds.stream()
                .map(id -> new ApiMonitor().setMonitorRuleId(id))
                .collect(Collectors.toList()));

        refreshCache();

        return ruleCount + limitCount + monitorCount;
    }

    @Override
    public void apply(List<Long> monitorRuleIds) {
        initConfigInRedis();
        initValueListInRedis(monitorRuleIds);
        for (Long monitorRuleId : monitorRuleIds) {
            doApply(monitorRuleId);
        }
    }

    private void doApply(Long monitorRuleId) {
        ApiMonitorRule rule = apiMonitorRuleRepository.selectByPrimaryKey(monitorRuleId);
        ApiLimit apiLimit = apiLimitRepository.selectOne(new ApiLimit().setMonitorRuleId(monitorRuleId).setEnabledFlag(true));
        if (apiLimit == null) {
            return;
        }
        Long id = rule.getMonitorRuleId();
        String pattern = rule.getUrlPattern();
        int timeWindowSize = rule.getTimeWindowSize();
        redisHelper.lstRightPush(IDS_KEY, String.valueOf(id));
        redisHelper.lstRightPush(PATTERNS_KEY, pattern);
        redisHelper.lstRightPush(WINDOWS_KEY, String.valueOf(timeWindowSize));
        Integer blacklistThreshold = Optional.ofNullable(apiLimit.getBlacklistThreshold()).orElse(Integer.MAX_VALUE);
        redisHelper.hshPut(BLACKLIST_THRESHOLD_KEY, String.valueOf(monitorRuleId), String.valueOf(blacklistThreshold));
        String mode = apiLimit.getListMode();
        String list = apiLimit.getValueList();
        if (!StringUtils.isEmpty(list)) {
            String[] ips = list.split(",");
            if (BLACK_MODE.equals(mode)) {
                redisHelper.setAdd(getBlacklistKey(id), ips);
            } else if (WHITE_MODE.equals(mode)) {
                redisHelper.setAdd(getWhitelistKey(id), ips);
            }
        }
        //通知网关读取redis配置
        notifyGateway();

    }

    @Override
    public void notifyGateway() {
        List<ServiceInstance> instances = discoveryClient.getInstances(HZeroService.getRealName(HZeroService.Gateway.NAME));

        boolean success = true;
        for (ServiceInstance instance : instances){
            try {
                httpTransporter.transport(new StaticEndpointHttpRequest<>(instance, StaticEndpoint.GATEWAY_REFRESH_METRIC_RULE, Long[].class));
            }catch (RestClientException e){
                LOGGER.error("gateway [host={}] notify failed.", instance.getHost());
                success = false;
            }
        }

        if (!success) {
            throw new CommonException("hadm.error.apply_config_failed");
        }

    }

    private void refreshCache() {
        List<String> monitorRuleIds = redisHelper.lstAll(IDS_KEY);
        if (!CollectionUtils.isEmpty(monitorRuleIds)) {
            apply(monitorRuleIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        }
    }

    private void initConfigInRedis() {
        redisHelper.delKey(IDS_KEY);
        redisHelper.delKey(PATTERNS_KEY);
        redisHelper.delKey(WINDOWS_KEY);
        redisHelper.delKey(BLACKLIST_VALUE_KEY);
        redisHelper.delKey(WHITELIST_VALUE_KEY);
    }

    private void initValueListInRedis(List<Long> monitorRuleIds) {
        for (Long monitorRuleId : monitorRuleIds) {
            redisHelper.delKey(getBlacklistKey(monitorRuleId));
            redisHelper.delKey(getWhitelistKey(monitorRuleId));
        }
    }
}
