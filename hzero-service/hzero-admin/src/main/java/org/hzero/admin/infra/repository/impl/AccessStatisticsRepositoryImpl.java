package org.hzero.admin.infra.repository.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.api.dto.ApiAccessStatisticsDTO;
import org.hzero.admin.api.dto.ServiceAccessStatisticsDTO;
import org.hzero.admin.domain.repository.AccessStatisticsRepository;
import org.hzero.admin.domain.repository.SwaggerRepository;
import org.hzero.admin.infra.factory.AccessStatisticsFactory;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.DynamicRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 访问统计资源库实现
 *
 * @author bergturing on 2020-5-7.
 */
@Repository
public class AccessStatisticsRepositoryImpl implements AccessStatisticsRepository {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessStatisticsRepositoryImpl.class);

    /**
     * 服务调用KEY模板: gateway:span:{{date}}
     */
    private static final String SERVICE_INVOKE_KEY_TEMPLATE = "gateway:span:%s";
    /**
     * API调用KEY模板: gateway:span:{{date}}:{{service}}
     */
    private static final String API_INVOKE_KEY_TEMPLATE = SERVICE_INVOKE_KEY_TEMPLATE + ":%s";

    /**
     * 字段：paths
     */
    private static final String FIELD_PATHS = "paths";
    /**
     * 字段：services
     */
    private static final String FIELD_SERVICES = "services";
    /**
     * 字段：apiCounts
     */
    private static final String FIELD_API_COUNTS = "apiCounts";

    /**
     * 对象映射对象
     */
    private final ObjectMapper objectMapper;
    /**
     * 动态RedisHelper
     */
    private final DynamicRedisHelper dynamicRedisHelper;
    /**
     * swagger资源库对象
     */
    private final SwaggerRepository swaggerRepository;
    /**
     * 访问统计工厂对象
     */
    private final AccessStatisticsFactory accessStatisticsFactory;

    @Autowired
    public AccessStatisticsRepositoryImpl(ObjectMapper objectMapper,
                                          DynamicRedisHelper dynamicRedisHelper,
                                          SwaggerRepository swaggerRepository,
                                          AccessStatisticsFactory accessStatisticsFactory) {
        this.objectMapper = objectMapper;
        this.dynamicRedisHelper = dynamicRedisHelper;
        this.swaggerRepository = swaggerRepository;
        this.accessStatisticsFactory = accessStatisticsFactory;
    }

    @Override
    public Map<String, Object> queryInstanceApiCount() {
        Map<String, Object> apiCountMap = new HashMap<>(4);
        // 服务列表
        List<String> services = new ArrayList<>();
        // 服务下api个数列表
        List<Integer> apiCounts = new ArrayList<>();

        // 获取服务列表
        MultiKeyMap<String, Set<String>> serviceMetaDataMap = Optional.ofNullable(this.swaggerRepository.getServiceMetaDataMap())
                .orElse(new MultiKeyMap<>());

        // 遍历服务列表
        serviceMetaDataMap.forEach(((multiKey, versions) -> {
            // 服务对象
            String service = multiKey.getKey(1);
            // api个数
            int count = 0;
            //目前只有一个版本，所以取第一个，如果后续支持多版本，此处遍历版本即可
            if (CollectionUtils.isEmpty(versions)) {
                return;
            }
            // 版本号
            String version = versions.iterator().next();

            boolean done = false;
            if (Objects.nonNull(version)) {
                try {
                    // 获取服务数据
                    String json = this.swaggerRepository.fetchSwaggerJsonByService(service, version);
                    if (StringUtils.isBlank(json)) {
                        LOGGER.warn("the swagger json of service {} version {} is empty, skip", service, version);
                    } else {
                        JsonNode node = this.objectMapper.readTree(json);
                        JsonNode pathNode = node.get(FIELD_PATHS);
                        Iterator<String> urlIterator = pathNode.fieldNames();
                        while (urlIterator.hasNext()) {
                            String url = urlIterator.next();
                            JsonNode methodNode = pathNode.get(url);
                            count = count + methodNode.size();
                        }
                        done = true;
                    }
                } catch (IOException e) {
                    LOGGER.error("object mapper read tree error, service: {}, version: {}", service, version);
                } catch (RemoteAccessException e) {
                    LOGGER.error(e.getMessage());
                }
            }

            if (done) {
                services.add(service);
                apiCounts.add(count);
            }
        }));

        // 放入统计结果
        apiCountMap.put(FIELD_SERVICES, services);
        apiCountMap.put(FIELD_API_COUNTS, apiCounts);
        return apiCountMap;
    }

    @Override
    public ApiAccessStatisticsDTO queryApiInvokeCount(LocalDate beginDate, LocalDate endDate, String service) {
        // 使用动态redis处理数据，并返回结果
        return this.dynamicRedisProcess(() -> {
            // 1. 获取原始数据
            Map<String, Map<String, Integer>> originData = this.dateRange(beginDate, endDate, (localDate) -> {
                // 获取zSet的值
                return this.getKeyValues(String.format(API_INVOKE_KEY_TEMPLATE, localDate, service));
            });

            // 2. 调用工厂方法构建结果对象，并返回结果
            return this.accessStatisticsFactory.createApiAccessStatisticsDTO(originData);
        });
    }

    @Override
    public ServiceAccessStatisticsDTO queryServiceInvokeCount(LocalDate beginDate, LocalDate endDate) {
        // 使用动态redis处理数据，并返回结果
        return this.dynamicRedisProcess(() -> {
            // 1. 获取原始数据
            Map<String, Map<String, Integer>> originData = this.dateRange(beginDate, endDate, (localDate) -> {
                // 获取zSet的值
                return this.getKeyValues(String.format(SERVICE_INVOKE_KEY_TEMPLATE, localDate));
            });

            // 2. 调用工厂方法构建结果对象，并返回结果
            return this.accessStatisticsFactory.createServiceAccessStatisticsDTO(originData);
        });
    }

    /**
     * 动态Redis执行逻辑
     *
     * @param supplier 具体的业务逻辑
     * @param <T>      直接结果对象泛型
     * @return 执行结果
     */
    private <T> T dynamicRedisProcess(Supplier<T> supplier) {
        try {
            // 设置redis数据库序号
            this.dynamicRedisHelper.setCurrentDatabase(HZeroService.Gateway.REDIS_DB);

            // 执行业务逻辑
            return supplier.get();
        } finally {
            // 清理当前redis数据库序号设置
            this.dynamicRedisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 根据指定的日期范围，按每天进行遍历数据，交由指定的逻辑处理
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @param transfer  处理逻辑实体  Function<LocalDate, Data>
     * @param <D>       处理结果对象泛型
     * @return 处理结果    key ---> value === 'yyyy-MM-dd' ---> Data
     */
    private <D> Map<String, D> dateRange(LocalDate beginDate, LocalDate endDate, Function<LocalDate, D> transfer) {
        if (Objects.isNull(beginDate) || Objects.isNull(endDate) || beginDate.isAfter(endDate)) {
            return Collections.emptyMap();
        }
        // 结果对象: 使用LinkedHashMap使获取的数据有序
        Map<String, D> result = new LinkedHashMap<>((int) (Period.between(beginDate, endDate).getDays() / 0.75) + 1);

        // 日期迭代器
        LocalDate iterator = beginDate;
        while (!iterator.isAfter(endDate)) {
            // 处理数据
            result.put(iterator.format(DateTimeFormatter.ISO_LOCAL_DATE), transfer.apply(iterator));
            iterator = iterator.plusDays(1);
        }

        // 返回结果
        return result;
    }

    /**
     * 获取zSet的所有值
     *
     * @param key zSet的key
     * @return zSet的所有值    key ---> value === zSetValueKey ---> score
     */
    private Map<String, Integer> getKeyValues(String key) {
        // 获取所有的值
        Set<ZSetOperations.TypedTuple<String>> zSetValues = this.dynamicRedisHelper.getRedisTemplate()
                .opsForZSet().rangeWithScores(key, 0L, -1L);
        if (CollectionUtils.isEmpty(zSetValues)) {
            return Collections.emptyMap();
        }

        // 结果对象
        Map<String, Integer> zSet = new HashMap<>(zSetValues.size());
        for (ZSetOperations.TypedTuple<String> zSetValue : zSetValues) {
            // 获取score
            zSet.put(zSetValue.getValue(), Optional.ofNullable(zSetValue.getScore()).orElse(0.0).intValue());
        }

        return zSet;
    }
}
