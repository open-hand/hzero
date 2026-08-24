package org.hzero.admin.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.admin.api.dto.GatewayRateLimitDto;
import org.hzero.admin.app.service.GatewayRateLimitLineService;
import org.hzero.admin.app.service.GatewayRateLimitService;
import org.hzero.admin.app.service.ServiceConfigRefreshService;
import org.hzero.admin.app.service.ServiceRouteRefreshService;
import org.hzero.admin.domain.entity.GatewayRateLimit;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.GatewayRateLimitDimensionRepository;
import org.hzero.admin.domain.repository.GatewayRateLimitLineRepository;
import org.hzero.admin.domain.repository.GatewayRateLimitRepository;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 网关限流设置应用服务默认实现
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Service
public class GatewayRateLimitServiceImpl extends BaseAppService implements GatewayRateLimitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayRateLimitServiceImpl.class);

    private static final String KEY_RESOLVER_PACKAGE_PREFIX = "org.hzero.gateway.ratelimit.dimension.";
    private static final String PRINCIPLE_KEY_RESOLVER_BEAN_NAME = "principalNameKeyResolver";
    private static final String SEMICOLON_PLACEHOLDER = "\\\"";

    @Lazy
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;
    @Lazy
    @Autowired
    private GatewayRateLimitLineRepository gatewayRateLimitLineRepository;
    @Lazy
    @Autowired
    private GatewayRateLimitRepository gatewayRateLimitRepository;
    @Lazy
    @Autowired
    private GatewayRateLimitLineService gatewayRateLimitLineService;
    @Lazy
    @Autowired
    private GatewayRateLimitDimensionRepository gatewayRateLimitDimensionRepository;
    @Lazy
    @Autowired
    private ServiceRouteRefreshService routeRefreshService;
    @Lazy
    @Autowired
    private ServiceConfigRefreshService configRefreshService;

    private final ObjectMapper mapper = new ObjectMapper();

    @ProcessLovValue
    @Override
    public GatewayRateLimitDto queryDetail(Long limitId, PageRequest pageRequest) {
        //查头
        GatewayRateLimit gatewayRateLimit = new GatewayRateLimit();
        gatewayRateLimit.setRateLimitId(limitId);
        gatewayRateLimit = gatewayRateLimitRepository.selectByPrimaryKey(gatewayRateLimit);
        //查行
        GatewayRateLimitLine gatewayRateLimitLine = new GatewayRateLimitLine();
        gatewayRateLimitLine.setRateLimitId(gatewayRateLimit.getRateLimitId());
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, GatewayRateLimitLine.FIELD_RATE_LIMIT_LINE_ID));
        pageRequest.setSort(sort);
        List<GatewayRateLimitLine> limitLines = gatewayRateLimitLineRepository.pageByCondition(pageRequest, gatewayRateLimitLine);
        //构建返回结果
        GatewayRateLimitDto gatewayRateLimitDto = new GatewayRateLimitDto();
        BeanUtils.copyProperties(gatewayRateLimit, gatewayRateLimitDto);
        gatewayRateLimitDto.setGatewayRateLimitLineList(limitLines);
        return gatewayRateLimitDto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GatewayRateLimitDto saveDetail(GatewayRateLimitDto dto) {
        SecurityTokenHelper.validToken(dto, false);
        this.validObject(dto);

        List<GatewayRateLimitLine> updateList = new ArrayList<>();
        List<GatewayRateLimitLine> insertList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dto.getGatewayRateLimitLineList())) {
            for (GatewayRateLimitLine line : dto.getGatewayRateLimitLineList()) {
                if (line.getRateLimitLineId() != null) {
                    updateList.add(line);
                } else {
                    insertList.add(line);
                }
            }
        }
        //更新头
        GatewayRateLimit limit = new GatewayRateLimit();
        BeanUtils.copyProperties(dto, limit);
        if (limit.judgeUpdateFieldIsUniqueIndex(gatewayRateLimitRepository)) {
            limit.validUniqueIndex(gatewayRateLimitRepository);
        }
        if (BaseConstants.Flag.NO.equals(limit.getEnabledFlag())) {
            //禁用时，取消所有子限流
            insertList.forEach(insert -> insert.setEnabledFlag(BaseConstants.Flag.NO));
            updateList.forEach(update -> update.setEnabledFlag(BaseConstants.Flag.NO));
        }
        gatewayRateLimitRepository.updateByPrimaryKeySelective(limit);
        //更新行
        SecurityTokenHelper.validToken(updateList);
        List<GatewayRateLimitLine> insertResult = gatewayRateLimitLineRepository.batchInsert(insertList);
        List<GatewayRateLimitLine> updateResult = gatewayRateLimitLineService.batchUpdateByPrimaryKey(updateList);
        //如果子限流被禁用，则撤销该限流变更
        List<Long> rollbackRouteIds = new ArrayList<>();
        updateList.forEach(update -> {
            if (BaseConstants.Flag.NO.equals(update.getEnabledFlag())) {
                rollbackRouteIds.add(update.getServiceRouteId());
            }
        });
        rollbackRateLimitChange(rollbackRouteIds);
        //构建返回结果
        GatewayRateLimitDto result = new GatewayRateLimitDto();
        BeanUtils.copyProperties(limit, result);
        result.setGatewayRateLimitLineList(insertResult);
        result.getGatewayRateLimitLineList().addAll(updateResult);
        return result;
    }

    private void rollbackRateLimitChange(List<Long> rollbackRouteIds) {
        routeRefreshService.removeRouteExtendConfigAndNotifyGateway(RATE_LIMITER_FILTER_NAME, rollbackRouteIds);
    }

    @Override
    public Page<GatewayRateLimit> pageByCondition(GatewayRateLimit GatewayRateLimit, PageRequest pageRequest) {
        return gatewayRateLimitRepository.pageByCondition(GatewayRateLimit, pageRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<GatewayRateLimit> batchInsertOrUpdate(List<GatewayRateLimit> GatewayRateLimitList) {
        List<GatewayRateLimit> updateList = new ArrayList<>();
        List<GatewayRateLimit> insertList = new ArrayList<>();
        for (GatewayRateLimit limit : GatewayRateLimitList) {
            if (limit.getRateLimitId() != null) {
                if (limit.judgeUpdateFieldIsUniqueIndex(gatewayRateLimitRepository)) {
                    limit.validUniqueIndex(gatewayRateLimitRepository);
                }
                updateList.add(limit);
            } else {
                limit.validUniqueIndex(gatewayRateLimitRepository);
                insertList.add(limit);
            }
        }
        //校验更新的token
        SecurityTokenHelper.validToken(updateList);
        List<GatewayRateLimit> insertResult = gatewayRateLimitRepository.batchInsert(insertList);
        List<GatewayRateLimit> updateResult = gatewayRateLimitRepository.batchUpdateOptional(updateList);
        List<GatewayRateLimit> result = new ArrayList<>();
        result.addAll(insertResult);
        result.addAll(updateResult);
        return result;
    }

    @Override
    public GatewayRateLimit selectByPrimaryKey(Long rateLimitId) {
        return gatewayRateLimitRepository.selectByPrimaryKey(rateLimitId);
    }

    @Override
    public GatewayRateLimit insertSelective(GatewayRateLimit gatewayRateLimit) {
        this.validObject(gatewayRateLimit);
        gatewayRateLimit.validUniqueIndex(gatewayRateLimitRepository);
        gatewayRateLimitRepository.insert(gatewayRateLimit);
        return gatewayRateLimit;
    }

    @Override
    public GatewayRateLimit updateByPrimaryKeySelective(GatewayRateLimit gatewayRateLimit) {
        this.validObject(gatewayRateLimit);
        SecurityTokenHelper.validToken(gatewayRateLimit);
        gatewayRateLimitRepository.updateByPrimaryKeySelective(gatewayRateLimit);
        return gatewayRateLimit;
    }

    /**
     * @param rateLimitList
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void refresh(List<GatewayRateLimit> rateLimitList) {
        List<GatewayRateLimit> feignRecord = new ArrayList<>();
        for (GatewayRateLimit gatewayRateLimit : rateLimitList) {
            List<GatewayRateLimit> limitList = gatewayRateLimitRepository.select(gatewayRateLimit);
            for (GatewayRateLimit limitUpdate : limitList) {
                try {
                    if (BaseConstants.Flag.YES.equals(limitUpdate.getEnabledFlag())) {
                        GatewayRateLimitLine queryParam = new GatewayRateLimitLine();
                        queryParam.setRateLimitId(limitUpdate.getRateLimitId());
                        queryParam.setEnabledFlag(BaseConstants.Flag.YES);
                        List<GatewayRateLimitLine> lines = gatewayRateLimitLineRepository.select(queryParam);
                        String rateLimitType = limitUpdate.getRateLimitType();
                        //step1:匹配路由，将限流配置合并到路由的额外配置(extend_config_map)中
                        for (GatewayRateLimitLine line : lines) {
                            merge(line, rateLimitType);
                        }

                        //step2:通知网关服务
                        routeRefreshService.notifyGateway();

                        //设置刷新状态
                        limitUpdate.setRefreshStatus(1L);
                        limitUpdate.setRefreshMessage(null);
                    } else {
                        // 未启用
                        limitUpdate.setRefreshStatus(1L);
                        limitUpdate.setRefreshMessage(null);
                    }
                } catch (Exception e) {
                    //处理异常
                    limitUpdate.setRefreshStatus(0L);
                    limitUpdate.setRefreshMessage(ExceptionUtils.getMessage(e));
                } finally {
                    limitUpdate.setRefreshTime(new Date());
                    feignRecord.add(limitUpdate);
                }
            }
        }
        gatewayRateLimitRepository.batchUpdateByPrimaryKey(feignRecord);
    }

    private void merge(GatewayRateLimitLine line, String rateLimitType) {
        if (line == null) {
            return;
        }
        Long routeId = line.getServiceRouteId();
        ServiceRoute route = serviceRouteRepository.selectByPrimaryKey(routeId);
        String origin = route.getExtendConfigMap();
        List<GatewayRateLimitDimension> gatewayRateLimitDimensions = gatewayRateLimitDimensionRepository.select(GatewayRateLimitDimension.FIElD_RATE_LIMIT_LINE_ID, line.getRateLimitLineId());
        String extendConfigMap = buildExtendConfigJson(line, gatewayRateLimitDimensions, rateLimitType);
        String mergeValue = mergeExtendConfig(origin, extendConfigMap);
        route.setExtendConfigMap(mergeValue);

        //更新配置到路由的额外配置字段
        serviceRouteRepository.updateOptional(route, ServiceRoute.FIELD_EXTEND_CONFIG_MAP);

    }

    private String mergeExtendConfig(String origin, String extendConfigJson) {

        Map<String, Object> map = null;
        if (origin == null || origin.isEmpty()) {
            map = new HashMap<>();
            List<String> jsonList = new ArrayList<>();
            map.put("filters", jsonList);
            jsonList.add(extendConfigJson);
            try {
                return mapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                LOGGER.error("map convert json failed", e);
                return "{}";
            }
        }

        try {
            map = mapper.readValue(origin, Map.class);
            List<Object> filters = (List<Object>) map.get("filters");
            if (filters != null) {
                List<Object> removeList = new ArrayList<>();
                for (Object filter : filters) {
                    if (isAllowCover((String) filter, extendConfigJson)) {
                        removeList.add(filter);
                    }
                }
                filters.removeAll(removeList);
                filters.add(extendConfigJson);
            } else {
                filters = new ArrayList<>();
                filters.add(extendConfigJson);
            }
            map.put("filters", filters);
        } catch (IOException e) {
            LOGGER.error("json convert failed", e);
        }

        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            LOGGER.error("map convert json failed", e);
            return "{}";
        }

    }

    private boolean isAllowCover(String str, String extendConfigJson) {
        try {

            Map<String, String> map1 = mapper.readValue(str, Map.class);
            Map<String, String> map2 = mapper.readValue(extendConfigJson, Map.class);

            String name1 = map1.get("name");
            String name2 = map2.get("name");

            if (name1 != null && name1.equals(name2)) {
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            return true;
        }
    }

    private String buildExtendConfigJson(GatewayRateLimitLine line, List<GatewayRateLimitDimension> gatewayRateLimitDimensions, String rateLimitType) {
        Assert.notNull(rateLimitType, "rateLimitType is not allowed to be null");
        Assert.notNull(line.getReplenishRate(), "replenishRate is not allowed to be null");
        if (line.getReplenishRate() < 1) {
            throw new CommonException("hadm.error.prohibited_less_than", "replenishRate", 1);
        }
        if (line.getBurstCapacity() != null && line.getBurstCapacity() < 1) {
            throw new CommonException("hadm.error.prohibited_less_than", "burstCapacity", 1);
        }
        /*{
         "name":"RequestRateLimiter",
         "args":{
            "rate-limiter":"#{@redisRateLimiter}",
            "key-resolver":"#{@tenantKeyResolver}",
            "redis-rate-limiter.burstCapacity":"1",
            "redis-rate-limiter.replenishRate":"1"
            }
         }*/
        String argsRateLimiter = "#{@" + rateLimitType + "}";
        String rateLimitDimension = line.getRateLimitDimension();
        String argsKeyResolver = buildKeyResolver(rateLimitDimension);
        String argsReplenishRate = String.valueOf(line.getReplenishRate());
        String argsBurstCapacity = String.valueOf(line.getBurstCapacity());
        StringBuilder builder = new StringBuilder("{")
                .append("\"name\":\"").append(RATE_LIMITER_FILTER_NAME).append("\",")
                .append("\"args\":{")
                .append("\"rate-limiter\":\"").append(argsRateLimiter).append("\"");
        if (rateLimitDimension != null) {
            builder.append(",")
                    .append("\"key-resolver\":\"").append(argsKeyResolver).append("\"");
        }
        builder.append(",")
                .append("\"redis-rate-limiter.replenishRate\":\"").append(argsReplenishRate).append("\"");
        if (line.getBurstCapacity() != null) {
            builder.append(",")
                    .append("\"redis-rate-limiter.burstCapacity\":\"").append(argsBurstCapacity).append("\"");
        }
        builder.append(buildGatewayRateLimitDimensionItems(rateLimitDimension, gatewayRateLimitDimensions));
        builder.append("}")
                .append("}");
        return builder.toString();
    }

    private String buildGatewayRateLimitDimensionItems(String rateLimitDimension, List<GatewayRateLimitDimension> gatewayRateLimitDimensions) {
        if (gatewayRateLimitDimensions == null || gatewayRateLimitDimensions.isEmpty()) {
            return "";
        }
        StringBuilder returnVal = new StringBuilder();
        for (GatewayRateLimitDimension config : gatewayRateLimitDimensions) {
            validateRateLimitDimension(rateLimitDimension, config);
            String key = buildKey(config.getRateLimitDimension(), config.getDimensionKey());
            if (config.getReplenishRate() != null) {
                if (config.getReplenishRate() < 1) {
                    throw new CommonException("hadm.error.prohibited_less_than", "replenishRate", 1);
                }
                returnVal
                        .append(",")
                        .append("\"redis-rate-limiter.replenishRateMap.")
                        .append(SEMICOLON_PLACEHOLDER)
                        .append(key)
                        .append(SEMICOLON_PLACEHOLDER)
                        .append("\":\"")
                        .append(config.getReplenishRate())
                        .append("\"");
            }
            if (config.getBurstCapacity() != null) {
                if (config.getBurstCapacity() != null && config.getBurstCapacity() < 1) {
                    throw new CommonException("hadm.error.prohibited_less_than", "burstCapacity", 1);
                }
                returnVal
                        .append(",")
                        .append("\"redis-rate-limiter.burstCapacityMap.")
                        .append(SEMICOLON_PLACEHOLDER)
                        .append(key)
                        .append(SEMICOLON_PLACEHOLDER)
                        .append("\":\"")
                        .append(config.getReplenishRate())
                        .append("\"");
            }
        }
        return returnVal.toString();
    }

    private void validateRateLimitDimension(String rateLimitDimension, GatewayRateLimitDimension config) {
        if (!rateLimitDimension.equals(config.getRateLimitDimension())) {
            LOGGER.error("The gatewayRateLimitDimension[id={}] is invalid, it may be dirty data, please try to clear it and try again.", config.getRateLimitDimId());
            throw new CommonException("hadm.error.data_error");
        }
    }

    private String buildKey(String rateLimitDimension, String dimensionKey) {
        Assert.notNull(dimensionKey, "dimensionKey should not be null!");
        String[] dimensionParts = rateLimitDimension.split(",");
        String[] dimensionKeyParts = dimensionKey.split(",");
        if (dimensionParts.length != dimensionKeyParts.length) {
            throw new IllegalArgumentException("arguments do not correspond.[required " + dimensionParts.length + ", but " + dimensionKeyParts.length + "]");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimensionParts.length; i++) {
            String extractTemplate = GatewayRateLimitDimension.Dimension.extractTemplate(dimensionParts[i]);
            if (!StringUtils.isEmpty(extractTemplate)) {
                builder
                        .append(GatewayRateLimitDimension.Dimension.extractDimension(dimensionParts[i]))
                        .append(GatewayRateLimitDimension.Dimension.buildUrlKey(extractTemplate, dimensionKeyParts[i]));
            } else {
                builder
                        .append(dimensionParts[i])
                        .append(dimensionKeyParts[i]);
            }
        }
        String key = builder.toString();
        /**
         * 转义处理，由于?/&=会被直接转化为空串
         * 使用._代表?
         * 使用.代表/
         * 使用_代表&
         * 使用=代表-
         */
        return key.replaceAll("\\?", "._")
                .replaceAll("/", ".")
                .replaceAll("&", "_")
                .replaceAll("=", "-");
    }

    private String buildKeyResolver(String rateLimitDimension) {
        //old logic
        //return "#{@" + rateLimitDimension + "KeyResolver}";

        //new logic
        String config = generateGatewayRateLimitDimension(rateLimitDimension, "@" + PRINCIPLE_KEY_RESOLVER_BEAN_NAME);

        return "#{" + config + "}";
    }

    private String generateGatewayRateLimitDimension(String rateLimitDimension, String defaultValue) {
        if (rateLimitDimension == null || rateLimitDimension.isEmpty()) {
            return defaultValue;
        }
        String[] dimensions = rateLimitDimension.split(",");
        StringBuilder builder = new StringBuilder("new ")
                .append(KEY_RESOLVER_PACKAGE_PREFIX)
                .append(GatewayRateLimitDimension.Dimension.COMBINED_KEY_RESOLVER)
                .append("(");
        for (int i = 0; i < dimensions.length; i++) {
            builder
                    .append("new ")
                    .append(KEY_RESOLVER_PACKAGE_PREFIX)
                    .append(GatewayRateLimitDimension.Dimension.getClassName(dimensions[i]))
                    .append("(");
            String extractTemplate = GatewayRateLimitDimension.Dimension.extractTemplate(dimensions[i]);
            if (!StringUtils.isEmpty(extractTemplate)) {
                builder
                        .append(SEMICOLON_PLACEHOLDER)
                        .append(extractTemplate)
                        .append(SEMICOLON_PLACEHOLDER);
            }
            builder.append(")");
            if (i < dimensions.length - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

}
