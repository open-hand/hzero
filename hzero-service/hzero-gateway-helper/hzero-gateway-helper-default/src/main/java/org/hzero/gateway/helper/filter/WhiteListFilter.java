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
 * 白名单过滤器
 * </p>
 *
 * @author qingsheng.chen 2019/3/15 星期五 13:55
 */
@Component
public class WhiteListFilter implements HelperFilter {
    private static final String TRUE = "true";
    private static final String WHITE_LIST_ENABLE_KEY = "gateway.WHITE_LIST_ENABLE";
    private static final String WHITE_LIST_KEY = "gateway.WHITE_LIST";
    private ListFilterProperties filterProperties;
    private RedisHelper redisHelper;

    @Autowired
    public WhiteListFilter(ListFilterProperties filterProperties, RedisHelper redisHelper) {
        this.filterProperties = filterProperties;
        this.redisHelper = redisHelper;
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter(RequestContext requestContext) {
        String enableWhiteList = redisHelper.strGet(WHITE_LIST_ENABLE_KEY);
        if (TRUE.equalsIgnoreCase(enableWhiteList)) {
            return true;
        }
        return !StringUtils.hasText(enableWhiteList) && filterProperties.getWhiteList().isEnable();
    }

    @Override
    public boolean run(RequestContext requestContext) {
        if (IpUtils.match(CollectionUtils.merge(redisHelper.lstAll(WHITE_LIST_KEY), filterProperties.getWhiteList().getIp()),
                ServerRequestUtils.getRealIp(requestContext.getServletRequest()))) {
            return true;
        } else {
            requestContext.response.setStatus(CheckState.PERMISSION_WHITE_LIST_FORBIDDEN);
            return false;
        }
    }
}
