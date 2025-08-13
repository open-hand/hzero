package org.hzero.gateway.helper.service.impl;

import static org.hzero.gateway.helper.filter.GetRequestRouteFilter.REQUEST_KEY_SEPARATOR;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.infra.mapper.PermissionPlusMapper;
import org.hzero.gateway.helper.service.PermissionService;


/**
 *
 * @author bojiangzhou Mark: 增加从缓存中获取权限，获取不到再从数据库获取
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionPlusMapper permissionPlusMapper;

    private RedisHelper redisHelper;

    private ObjectMapper mapper = BaseConstants.MAPPER;

    public PermissionServiceImpl( PermissionPlusMapper permissionPlusMapper, RedisHelper redisHelper) {
        this.permissionPlusMapper = permissionPlusMapper;
        this.redisHelper = redisHelper;
    }

    /**
     * Cacheable 设置使用二级缓存
     * 先通过method和service从数据库中查询权限；
     * 如果匹配到多条权限，则排序计算出匹配度最高的权限
     */
    @Override
    @Cacheable(value = "permission", key = "'choerodon:permission:'+#requestKey", unless = "#result == null")
    public PermissionDO selectPermissionByRequest(String requestKey) {
        String[] request = requestKey.split(REQUEST_KEY_SEPARATOR);
        String uri = request[0];
        String method = request[1];
        String serviceName = request[2];

        List<PermissionDO> permissionDOS = selectPermissions(serviceName, method);

        List<PermissionDO> matchPermissions = permissionDOS.stream().filter(t -> matcher.match(t.getPath(), uri))
                .sorted((PermissionDO o1, PermissionDO o2) -> {
                    Comparator<String> patternComparator = matcher.getPatternComparator(uri);
                    return patternComparator.compare(o1.getPath(), o2.getPath());
                }).collect(Collectors.toList());
        int matchSize = matchPermissions.size();
        if (matchSize < 1) {
            return null;
        } else {
            PermissionDO bestMatchPermission = matchPermissions.get(0);
            if (matchSize > 1) {
                LOGGER.info("Request: {} match multiply permission: {}, the best match is: {}",
                        uri, matchPermissions, bestMatchPermission.getPath());
            }
            return bestMatchPermission;
        }
    }

    private List<PermissionDO> selectPermissions(String serviceName, String method) {
        serviceName = StringUtils.lowerCase(serviceName);
        method = StringUtils.lowerCase(method);

        List<PermissionDO> permissions;

        String permissionKey = PermissionDO.generateKey(serviceName, method);
        // 先从缓存获取
        Map<String, String> map = redisHelper.hshGetAll(permissionKey);
        if (MapUtils.isNotEmpty(map)) {
            permissions = map.values().parallelStream().map(s -> redisHelper.fromJson(s, PermissionDO.class)).collect(Collectors.toList());
        }
        // 查询数据库
        else {
            permissions = permissionPlusMapper.selectPermissionByMethodAndService(method, serviceName);
            LOGGER.info("cache service permissions, serviceName={}, method={}, permissionsSize={}", serviceName, method, permissions.size());

            Map<String, String> idMap = permissions.parallelStream().collect(Collectors.toMap(p -> String.valueOf(p.getId()), p -> redisHelper.toJson(p)));
            redisHelper.hshPutAll(PermissionDO.generateKey(serviceName, method), idMap);
        }

        return permissions;
    }

}
