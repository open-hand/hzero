package org.hzero.boot.platform.event.autoconfigure;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.OgnlContext;
import ognl.TypeConverter;
import org.hzero.boot.platform.event.EventScheduler;
import org.hzero.boot.platform.event.handler.EventHandlerHolder;
import org.hzero.boot.platform.event.helper.AsyncScheduleHelper;
import org.hzero.boot.platform.event.helper.RequestHelper;
import org.hzero.boot.platform.event.helper.RuleMatcher;
import org.hzero.boot.platform.event.helper.impl.DefaultAsyncScheduleHelper;
import org.hzero.boot.platform.event.helper.impl.DefaultRequestHelper;
import org.hzero.boot.platform.event.helper.impl.DefaultRuleMatcher;
import org.hzero.boot.platform.event.helper.impl.RequestTokenInterceptor;
import org.hzero.boot.platform.event.ognl.CustomMemberAccess;
import org.hzero.boot.platform.event.provider.DefaultScheduleProviderManager;
import org.hzero.boot.platform.event.provider.ScheduleProvider;
import org.hzero.boot.platform.event.provider.ScheduleProviderManager;
import org.hzero.boot.platform.event.repository.DefaultEventRuleRepository;
import org.hzero.boot.platform.event.repository.EventRuleRepository;
import org.hzero.core.properties.CoreProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * 事件调度器自动化配置
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 17:50
 */
@Configuration
@ComponentScan("org.hzero.boot.platform.event")
public class EventAutoConfiguration {

    /**
     * 创建时间处理类容器
     *
     * @return {@link EventHandlerHolder}
     */
    @Bean
    public EventHandlerHolder eventHandlerHolder() {
        return new EventHandlerHolder();
    }

    /**
     * 创建默认的规则匹配器
     *
     * @return {@link OgnlContext}
     * @see OgnlContext#OgnlContext(MemberAccess, ClassResolver, TypeConverter, Map)
     * @see CustomMemberAccess
     */
    @Bean
    @ConditionalOnMissingBean(RuleMatcher.class)
    public RuleMatcher ruleMatcher() {
        return new DefaultRuleMatcher();
    }

    /**
     * 创建默认的事件规则资源库
     *
     * @return {@link EventRuleRepository}
     * @see DefaultEventRuleRepository
     */
    @Bean
    @ConditionalOnMissingBean(EventRuleRepository.class)
    public EventRuleRepository eventRuleRepository() {
        return new DefaultEventRuleRepository();
    }

    /**
     * 创建默认的异步线程调度器
     *
     * @return {@link AsyncScheduleHelper}
     * @see DefaultAsyncScheduleHelper
     */
    @Bean
    @ConditionalOnMissingBean(AsyncScheduleHelper.class)
    public AsyncScheduleHelper asyncScheduleHelper() {
        return new DefaultAsyncScheduleHelper();
    }

    /**
     * 创建 RequestTokenInterceptor
     *
     * @param properties properties
     * @return {@link RequestTokenInterceptor}
     */
    @Bean(name = "requestTokenInterceptor")
    @ConditionalOnMissingBean(name = "requestTokenInterceptor")
    public RequestTokenInterceptor requestTokenInterceptor(CoreProperties properties) {
        return new RequestTokenInterceptor(properties);
    }

    /**
     * 创建默认的Http请求辅助对象
     *
     * @return {@link RequestHelper}
     * @see DefaultRequestHelper
     */
    @Bean
    @ConditionalOnMissingBean(RequestHelper.class)
    public RequestHelper requestHelper() {
        return new DefaultRequestHelper();
    }

    @Bean
    @ConditionalOnMissingBean(ScheduleProviderManager.class)
    public ScheduleProviderManager scheduleProviderManager(List<ScheduleProvider> scheduleProviders) {
        return new DefaultScheduleProviderManager(scheduleProviders);
    }

    /**
     * 创建事件调度器
     *
     * @return {@link EventScheduler}
     * @see EventRuleRepository
     * @see OgnlContext
     * @see EventHandlerHolder
     * @see RequestHelper
     * @see AsyncScheduleHelper
     */
    @Bean
    public EventScheduler eventScheduler(EventRuleRepository eventRuleRepository,
                                         ScheduleProviderManager scheduleProviderManager) {
        return new EventScheduler(eventRuleRepository, scheduleProviderManager);
    }
}
