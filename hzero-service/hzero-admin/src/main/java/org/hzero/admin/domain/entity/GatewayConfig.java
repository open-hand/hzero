package org.hzero.admin.domain.entity;

/**
 * @author XCXCXCXCX
 * @date 2019/10/18
 * @project hzero-admin
 */
public class GatewayConfig {

    public static final String ROOT_KEY = "spring.cloud.gateway";

    public static final String FILTERS_INDEX_PLACEHOLDER = "${f0-9}";
    public static final String PREDICATES_INDEX_PLACEHOLDER = "${p0-9}";

    public static final String FILTERS_KEY = "filters[" + FILTERS_INDEX_PLACEHOLDER + "]";
    public static final String PREDICATES_KEY = "predicates[" + PREDICATES_INDEX_PLACEHOLDER + "]";

    public static final String FILTER_NAME = "name";

    public static final String FILTER_NAME_KEY = FILTERS_KEY + "." + FILTER_NAME;

    public static final String FILTER_ARGS = "args";

    public static final String FILTER_ARGS_KEY = FILTERS_KEY + "." + FILTER_ARGS;

    public static final String ARGS_MAP_REPLENISH_RATE_KEY = "redis-rate-limiter.replenishRate";
    public static final String ARGS_MAP_BURST_CAPACITY_KEY = "redis-rate-limiter.burstCapacity";
    public static final String ARGS_MAP_RATE_LIMITER_KEY = "rate-limiter";
    public static final String ARGS_MAP_KEY_RESOLVER_KEY = "key-resolver";

    public static final String REPLENISH_RATE_KEY = FILTER_ARGS_KEY + "." + ARGS_MAP_REPLENISH_RATE_KEY;
    public static final String BURST_CAPACITY_KEY = FILTER_ARGS_KEY + "." + ARGS_MAP_BURST_CAPACITY_KEY;
    public static final String RATE_LIMITER_KEY = FILTER_ARGS_KEY + "." + ARGS_MAP_RATE_LIMITER_KEY;
    public static final String KEY_RESOLVER_KEY = FILTER_ARGS_KEY + "." + ARGS_MAP_KEY_RESOLVER_KEY;

}
