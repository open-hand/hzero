package org.hzero.gateway.helper.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ServerRequestUtils;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.ListFilterProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.util.CollectionUtils;
import org.hzero.gateway.helper.util.IpUtils;

/**
 * <p>
 * 黑名单过滤
 * </p>
 *
 * @author qingsheng.chen 2019/3/15 星期五 13:48
 */
@Component
public class BlackListFilter implements HelperFilter {
    private static final String TRUE = "true";
    private static final String BLACK_LIST_ENABLE_KEY = "gateway.BLACK_LIST_ENABLE";
    private static final String BLACK_LIST_KEY = "gateway.BLACK_LIST";
    private ListFilterProperties filterProperties;
    private RedisHelper redisHelper;

    @Autowired
    public BlackListFilter(ListFilterProperties filterProperties, RedisHelper redisHelper) {
        this.filterProperties = filterProperties;
        this.redisHelper = redisHelper;
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter(RequestContext requestContext) {
        String enableBlackList = redisHelper.strGet(BLACK_LIST_ENABLE_KEY);
        if (TRUE.equalsIgnoreCase(enableBlackList)) {
            return true;
        }
        return !StringUtils.hasText(enableBlackList) && filterProperties.getBlackList().isEnable();
    }

    @Override
    public boolean run(RequestContext requestContext) {
        if (!IpUtils.match(CollectionUtils.merge(redisHelper.lstAll(BLACK_LIST_KEY), filterProperties.getBlackList().getIp()),
                ServerRequestUtils.getRealIp(requestContext.getServletRequest()))) {
            return true;
        } else {
            requestContext.response.setStatus(CheckState.PERMISSION_BLACK_LIST_FORBIDDEN);
            return false;
        }
    }
}
