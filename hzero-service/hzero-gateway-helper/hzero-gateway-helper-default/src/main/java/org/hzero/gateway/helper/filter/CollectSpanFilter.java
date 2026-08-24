package org.hzero.gateway.helper.filter;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayHelperProperties;
import org.hzero.gateway.helper.domain.TranceSpan;
import org.hzero.gateway.helper.entity.CommonRoute;
import org.hzero.gateway.helper.entity.PermissionDO;
import org.hzero.gateway.helper.entity.RequestContext;


/**
 * @author bojiangzhou Mark: 缓存统计信息的 key 增加一级目录，避免直接在顶层出现大量日期key
 * @author superlee
 */
@Component
public class CollectSpanFilter implements HelperFilter {

    private boolean collectSpanEnabled;
    private StringRedisTemplate stringRedisTemplate;

    public CollectSpanFilter(StringRedisTemplate stringRedisTemplate, GatewayHelperProperties properties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.collectSpanEnabled = properties.getFilter().getCollectSpan().isEnabled();
    }

    @Override
    public int filterOrder() {
        return 25;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return collectSpanEnabled;
    }

    @Override
    public boolean run(RequestContext context) {
        CommonRoute zuulRoute = context.getRoute();
        PermissionDO permissionDO = context.getPermission();
        String serviceId = zuulRoute.getServiceId();
        String method = context.request.method;
        TranceSpan tranceSpan = new TranceSpan(permissionDO.getPath(), serviceId, method, LocalDate.now());
        Observable
                .just(tranceSpan)
                .subscribeOn(Schedulers.io())
                .subscribe(this::tranceSpanSubscriber);
        return true;
    }

    private void tranceSpanSubscriber(final TranceSpan tranceSpan) {
        // 服务访问次数计数
        this.staticInvokeCount(tranceSpan.getServiceInvokeKey(), tranceSpan.getServiceInvokeValue());
        // api访问次数计数
        this.staticInvokeCount(tranceSpan.getApiInvokeKey(), tranceSpan.getApiInvokeValue());
    }

    private void staticInvokeCount(String key, String value) {
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.opsForZSet().incrementScore(key, value, 1);
        } else {
            stringRedisTemplate.opsForZSet().add(key, value, 1);
            stringRedisTemplate.expire(key, 31, TimeUnit.DAYS);
        }
    }
}
