package org.hzero.boot.platform.data.permission.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.choerodon.core.oauth.DetailsHelper;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Table;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.data.permission.helper.PermissionDataHelper;
import org.hzero.boot.platform.data.permission.util.KeyUtils;
import org.hzero.boot.platform.data.permission.util.StringUtils;
import org.hzero.boot.platform.data.permission.vo.PermissionRangeExclVO;
import org.hzero.boot.platform.data.permission.vo.PermissionRangeVO;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 默认数据权限资源库实现
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 11:31
 */
public class DefaultPermissionSqlRepository implements PermissionSqlRepository {

    private final RedisHelper redisHelper;
    private final Cache<String, PermissionRangeVO> permissionCache;


    /**
     * 动态租户前缀标识
     */
    private static final String DYNAMIC_PREFIX = "#{tenantId}";

    @Value("${spring.application.name:hzero-platform}")
    private String serviceName;

    public DefaultPermissionSqlRepository(RedisHelper redisHelper, Duration getLocalCacheExpirationTime) {
        super();
        this.redisHelper = redisHelper;
        this.permissionCache = CacheBuilder.newBuilder()
                .expireAfterWrite(getLocalCacheExpirationTime.toMillis(), TimeUnit.MILLISECONDS)
                .softValues()
                .initialCapacity(128)
                .build();
    }

    @Override
    public Map<Table, PermissionRangeVO> getPermissionRangeVOMap(Collection<Table> tableCollection, String serviceName,
                                                                 String sqlId, Long tenantId) {
        if (CollectionUtils.isNotEmpty(tableCollection)) {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            Map<Table, PermissionRangeVO> tableListMap = new HashMap<>();
            tableCollection.forEach(table -> {
                PermissionRangeVO rangeVO = getPermissionRange(serviceName, table, sqlId, tenantId);
                if (rangeVO != null) {
                    tableListMap.put(table, rangeVO);
                }
            });
            redisHelper.clearCurrentDatabase();
            return tableListMap;
        }
        return null;
    }

    @Override
    public PermissionRangeVO getPermissionRange(String serviceName, Table table, String sqlId, Long tenantId) {
        String localCacheKey = serviceName + "&" + table.getName() + "&" + sqlId + "&" + tenantId;
        PermissionRangeVO cached = permissionCache.getIfPresent(localCacheKey);
        if (cached != null) {
            return cached;
        }

        String cacheKey = KeyUtils.generateCacheKey(table.getName());

        PermissionRangeVO permissionRangeVO = getPermissionRangeVO(tenantId, serviceName, StringUtils.handleCountSqlId(sqlId), cacheKey);
        // 处理自定义规则
        handlePermissionRangeVO(permissionRangeVO, table, sqlId);
        // 处理动态表前缀
        handleDynamicDbPrefix(permissionRangeVO);
        // 处理表别名
        handleTableAlias(table, permissionRangeVO);
        if (permissionRangeVO != null) {
            permissionCache.put(localCacheKey, permissionRangeVO);
        }
        return permissionRangeVO;
    }

    @Override
    public void resetCache() {
    }

    /**
     * 处理表别名
     *
     * @param table
     * @param permissionRangeVO
     */
    private void handleTableAlias(Table table, PermissionRangeVO permissionRangeVO) {
        if (permissionRangeVO != null && StringUtils.isNotEmpty(permissionRangeVO.getDbPrefix()) && CollectionUtils.isNotEmpty(permissionRangeVO.getSqlList()) && table.getAlias() == null) {
            Alias alias = new Alias(table.getName());
            table.setAlias(alias);
        }
    }

    /**
     * 处理表前缀，如果存在动态表前缀标记，则根据维护信息进行维护替换
     *
     * @param permissionRangeVO
     */
    private void handleDynamicDbPrefix(PermissionRangeVO permissionRangeVO) {
        if (permissionRangeVO == null) {
            return;
        }
        String value = null;
        if (DYNAMIC_PREFIX.equals(permissionRangeVO.getDbPrefix())) {
            String key = KeyUtils.generatePrefixCacheKey(DetailsHelper.getUserDetails().getTenantId(), serviceName);
            value = redisHelper.strGet(key);
        } else if (StringUtils.isNotEmpty(permissionRangeVO.getDbPrefix())) {
            List<String> dynamicDbPrefixList = StringUtils.getFieldList(permissionRangeVO.getDbPrefix());
            if (CollectionUtils.isNotEmpty(dynamicDbPrefixList)) {
                String key = KeyUtils.generatePrefixCacheKey(DetailsHelper.getUserDetails().getTenantId(), StringUtils.getField(dynamicDbPrefixList.get(0)));
                value = redisHelper.strGet(key);
            }
        }
        if (value != null) {
            permissionRangeVO.setDbPrefix(value);
        }
    }

    /**
     * 处理多个数据权限组合信息，返回应用层级最小的数据权限信息，判断应用层级大小的规则如下：
     * tenantId > tenantId + serviceName > tenantId + sqlI > tenantId + serviceName + sqlId
     *
     * @param tenantId    tenantId
     * @param serviceName serviceName
     * @param sqlId       sqlId
     * @param cacheKey    cacheKey
     * @return 数据权限信息
     */
    private PermissionRangeVO getPermissionRangeVO(Long tenantId, String serviceName, String sqlId,
                                                   String cacheKey) {

        List<String> keyList = new ArrayList<String>() {{
            add(KeyUtils.generateMapKey(tenantId, null, null));
            add(KeyUtils.generateMapKey(tenantId, serviceName, null));
            add(KeyUtils.generateMapKey(tenantId, null, sqlId));
            add(KeyUtils.generateMapKey(tenantId, serviceName, sqlId));
            if (!BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
                add(KeyUtils.generateMapKey(BaseConstants.DEFAULT_TENANT_ID, null, null));
                add(KeyUtils.generateMapKey(BaseConstants.DEFAULT_TENANT_ID, serviceName, null));
                add(KeyUtils.generateMapKey(BaseConstants.DEFAULT_TENANT_ID, null, sqlId));
                add(KeyUtils.generateMapKey(BaseConstants.DEFAULT_TENANT_ID, serviceName, sqlId));
            }
        }};

        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        List<String> rangeList = redisHelper.hshMultiGet(cacheKey, keyList);
        redisHelper.clearCurrentDatabase();

        PermissionRangeVO tenant = this.redisHelper.fromJson(getIfPresent(rangeList, 0), PermissionRangeVO.class);
        PermissionRangeVO tenantService = this.redisHelper.fromJson(getIfPresent(rangeList, 1), PermissionRangeVO.class);
        PermissionRangeVO tenantSqlId = this.redisHelper.fromJson(getIfPresent(rangeList, 2), PermissionRangeVO.class);
        PermissionRangeVO tenantServiceSqlId = this.redisHelper.fromJson(getIfPresent(rangeList, 3), PermissionRangeVO.class);
        PermissionRangeVO range = bestMatch(tenantId, serviceName, sqlId, tenantServiceSqlId, tenantSqlId, tenantService, tenant);
        if (range == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            tenant = this.redisHelper.fromJson(getIfPresent(rangeList, 4), PermissionRangeVO.class);
            tenantService = this.redisHelper.fromJson(getIfPresent(rangeList, 5), PermissionRangeVO.class);
            tenantSqlId = this.redisHelper.fromJson(getIfPresent(rangeList, 6), PermissionRangeVO.class);
            tenantServiceSqlId = this.redisHelper.fromJson(getIfPresent(rangeList, 7), PermissionRangeVO.class);
            range = bestMatch(BaseConstants.DEFAULT_TENANT_ID, serviceName, sqlId, tenantServiceSqlId, tenantSqlId, tenantService, tenant);
        }
        return range;
    }

    private String getIfPresent(List<String> permissionRangeList, int index) {
        if (permissionRangeList != null && permissionRangeList.size() > index) {
            return permissionRangeList.get(index);
        }
        return null;
    }

    /**
     * 判断处理数据屏蔽范围中的值，如果存在自定义规则标识，则选择用户自定义的sql，否则选择配置的sql
     *
     * @param rangeVO 范围vo
     * @param table   表
     */
    private void handlePermissionRangeVO(PermissionRangeVO rangeVO, Table table, String sqlId) {
        if (rangeVO == null) {
            return;
        }
        if (BaseConstants.Flag.YES.equals(rangeVO.getCustomRuleFlag())) {
            String sql = PermissionDataHelper.getTableSql(table.getName());
            // 如果不是分页的count语句则进行消除
            if (!StringUtils.isCountSql(sqlId)) {
                PermissionDataHelper.removeTableSql(table.getName());
            }
            rangeVO.setSqlList(new ArrayList<>());
            rangeVO.getSqlList().add(sql);
        }
    }

    /**
     * 处理分析多个数据权限信息
     *
     * @param ranges ranges
     * @return 最后一个权限信息
     */
    private PermissionRangeVO bestMatch(Long tenantId, String serviceName, String sqlId, PermissionRangeVO... ranges) {
        for (PermissionRangeVO range : ranges) {
            if (range != null && isNotEmpty(range)) {
                boolean exclude = false;
                if (!CollectionUtils.isEmpty(range.getRangeExclList())) {
                    for (PermissionRangeExclVO excl : range.getRangeExclList()) {
                        exclude |= (
                                (excl.getTenantId() != null && Objects.equals(excl.getTenantId(), tenantId))
                                        || (StringUtils.isNotEmpty(excl.getServiceName()) && Objects.equals(excl.getServiceName(), serviceName))
                                        || (StringUtils.isNotEmpty(excl.getSqlId()) && Objects.equals(excl.getSqlId(), sqlId))
                        );
                        if (exclude) {
                            break;
                        }
                    }
                }
                if (!exclude) {
                    return range;
                }
            }
        }
        return null;
    }

    private boolean isNotEmpty(PermissionRangeVO range) {
        return BaseConstants.Flag.YES.equals(range.getCustomRuleFlag())
                || StringUtils.isNotEmpty(range.getDbPrefix())
                || CollectionUtils.isNotEmpty(range.getSqlList())
                || CollectionUtils.isNotEmpty(range.getRangeExclList());
    }
}
