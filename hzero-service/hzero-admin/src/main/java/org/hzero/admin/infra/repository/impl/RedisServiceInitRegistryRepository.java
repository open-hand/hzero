package org.hzero.admin.infra.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.admin.domain.repository.ServiceInitRegistryRepository;
import org.hzero.admin.domain.vo.Service;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:45 上午
 */
@Repository
public class RedisServiceInitRegistryRepository implements ServiceInitRegistryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServiceInitRegistryRepository.class);

    private static final String SERVICES_TO_INIT_KEY = HZeroService.Admin.CODE + ".services-to-init";

    private static final String SPLIT = ":";

    @Autowired
    private RedisHelper redisHelper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private <T> T selectDbAndExecute(Supplier<T> function) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
            return function.get();
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    private String buildHashKey(Service service) {
        return service.getServiceName() + SPLIT + service.getVersion();
    }

    @Override
    public void add(Service service) {
        selectDbAndExecute(() -> {
            try {
                redisHelper.hshPut(SERVICES_TO_INIT_KEY,
                        buildHashKey(service),
                        objectMapper.writeValueAsString(service));
            } catch (JsonProcessingException e) {
                LOGGER.error("json parse failed", e);
            }
            return null;
        });
    }

    @Override
    public void remove(Service service) {
        selectDbAndExecute(() -> {
            redisHelper.hshDelete(SERVICES_TO_INIT_KEY, buildHashKey(service));
            return null;
        });
    }

    @Override
    public Set<Service> get() {
        return selectDbAndExecute(() -> {
            Set<Service> serviceSet = new HashSet<>();
            Set<String> services = redisHelper.hshKeys(SERVICES_TO_INIT_KEY);
            List<String> serviceList = new ArrayList<>(services);
            List<String> serviceJsons = redisHelper.hshVals(SERVICES_TO_INIT_KEY, serviceList);
            for (String json : serviceJsons) {
                try {
                    Service service = objectMapper.readValue(json, Service.class);
                    serviceSet.add(service);
                } catch (IOException e) {
                    LOGGER.error("json parse failed", e);
                }
            }
            return serviceSet;
        });
    }

    @Override
    public void setInitialized(Service service) {
        add(service);
    }

}
