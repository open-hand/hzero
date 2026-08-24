package org.hzero.gateway.helper.filter;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseHeaders;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.ServerRequestUtils;
import org.hzero.core.util.SystemClock;
import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * Api防重放
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/19 20:54
 */
@Component
public class ApiReplayFilter implements HelperFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiReplayFilter.class);

    private static final long EFFECTIVE_TIME = 600L;
    private static final int REQUEST_ID_LENGTH = 45;

    private final AntPathMatcher matcher = new AntPathMatcher();

    private final RedisHelper redisHelper;
    private final GatewayHelperProperties gatewayHelperProperties;

    @Autowired
    public ApiReplayFilter(RedisHelper redisHelper,
                           GatewayHelperProperties gatewayHelperProperties) {
        this.redisHelper = redisHelper;
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    @Override
    public int filterOrder() {
        // 紧跟在GetUserDetailsFilter之后执行
        return 41;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        GatewayHelperProperties.Filter.ApiReplay apiReplay = gatewayHelperProperties.getFilter().getApiReplay();
        if (!apiReplay.isEnabled()) {
            return false;
        }
        if (apiReplay.getSkipPaths().stream().anyMatch(t -> matcher.match(t, context.request.uri))) {
            return false;
        }
        CustomUserDetails userDetails = context.getCustomUserDetails();
        if (userDetails == null) {
            // public接口拦截
            return false;
        }
        return BaseConstants.Flag.YES.equals(userDetails.getApiReplayFlag());
    }

    @Override
    public boolean run(RequestContext context) {
        String requestId = getRequestId(context);
        if (StringUtils.isBlank(requestId) || requestId.length() != REQUEST_ID_LENGTH) {
            context.response.setStatus(CheckState.REQUEST_REPEAT);
            context.response.setMessage("Repeat request, do not submit repeatedly");
            return false;
        }
        Long timestamp = getTimeStamp(requestId);
        // 校验timestamp
        if (!checkTimeStamp(timestamp)) {
            context.response.setStatus(CheckState.REQUEST_REPEAT);
            context.response.setMessage("Repeat request, do not submit repeatedly");
            return false;
        }
        String uuid = getNonce(requestId);
        // 校验nonce
        if (!checkNonce(uuid)) {
            context.response.setStatus(CheckState.REQUEST_REPEAT);
            context.response.setMessage("Repeat request, do not submit repeatedly");
            return false;
        }
        return true;
    }


    private String getRequestId(RequestContext context) {
        Object servletRequest = context.getServletRequest();
        String requestId = ServerRequestUtils.getHeaderValue(servletRequest, BaseHeaders.H_REQUEST_ID);
        if (StringUtils.isBlank(requestId)) {
            return null;
        }
        return requestId;
    }

    private boolean checkTimeStamp(Long timestamp) {
        long now = SystemClock.now();
        long num;
        if (now > timestamp) {
            num = now - timestamp;
        } else {
            num = timestamp - now;
        }
        boolean flag = num <= EFFECTIVE_TIME * 1000;
        if (!flag) {
            LOGGER.debug("Replay Request! Time difference {} milliseconds", num);
        }
        return flag;
    }

    /**
     * 判断随机数是否存在
     *
     * @param nonce 随机数
     * @return true不存在
     */
    private boolean checkNonce(String nonce) {
        String key = "gateway:api-replay:" + nonce;
        boolean flag = Boolean.TRUE.equals(redisHelper.strSetIfAbsent(key, "lock"));
        if (flag) {
            redisHelper.setExpire(key, EFFECTIVE_TIME, TimeUnit.SECONDS);
        }
        return flag;
    }

    @SuppressWarnings({"unused"})
    private String encrypt(Long timestamp, String uuid) {
        // 翻转时间戳
        String time = reverse(String.valueOf(timestamp));
        // 毫秒
        String time1 = time.substring(0, 3);
        String time2 = time.substring(3, 7);
        String time3 = time.substring(7);
        String uuid1 = uuid.substring(0, 12);
        String uuid2 = uuid.substring(12);
        return time1 + uuid1 + time2 + uuid2 + time3;
    }

    private Long getTimeStamp(String str) {
        String time1 = str.substring(0, 3);
        String time2 = str.substring(15, 19);
        String time3 = str.substring(39);
        return Long.valueOf(reverse(time1 + time2 + time3));
    }

    private String getNonce(String str) {
        String uuid1 = str.substring(3, 15);
        String uuid2 = str.substring(19, 39);
        return uuid1 + uuid2;
    }

    private String reverse(String str) {
        StringBuilder sb = new StringBuilder(new StringBuffer(str));
        return sb.reverse().toString();
    }
}