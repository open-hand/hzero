package org.hzero.core.interceptor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import io.choerodon.core.exception.NotFoundException;

/**
 * 拦截器链管理器
 *
 * @param <T> The target object to interceptor.
 * @author bojiangzhou 2020/05/28
 */
public abstract class AbstractInterceptorChainManager<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractInterceptorChainManager.class);

    private final Map<ChainId, InterceptorChain<T>> chainMap;

    public AbstractInterceptorChainManager(List<HandlerInterceptor<T>> interceptorList,
                                           List<InterceptorChainConfigurer<T, InterceptorChainBuilder<T>>> configurerList) {
        this.chainMap = ImmutableMap.copyOf(initInterceptorChain(interceptorList, configurerList));

        LOGGER.info("Register {} InterceptorChain, names are [{}]", this.chainMap.size(), this.chainMap.keySet().stream().map(ChainId::id).collect(Collectors.joining(",")));
    }

    private Map<ChainId, InterceptorChain<T>> initInterceptorChain(List<HandlerInterceptor<T>> interceptorList,
                                                                   List<InterceptorChainConfigurer<T, InterceptorChainBuilder<T>>> configurerList) {
        if (CollectionUtils.isEmpty(interceptorList)) {
            throw new IllegalArgumentException("Interceptors is empty.");
        }

        if (CollectionUtils.isEmpty(configurerList)) {
            throw new IllegalArgumentException("Interceptor configurers is empty.");
        }

        InterceptorChainBuilder<T> builder = new InterceptorChainBuilder<>(interceptorList);

        configurerList.sort(AnnotationAwareOrderComparator.INSTANCE);

        configurerList.forEach(configurer -> {
            configurer.configure(builder);
            builder.clearChain();
        });

        List<InterceptorChain<T>> chains = builder.performBuild();

        return chains.stream().collect(Collectors.toMap(InterceptorChain::getChainId, Function.identity()));
    }


    /**
     * 拦截器调用入口，将核心操作封装成 Consumer 对象传入。
     *
     * @param target    The target to handle.
     * @param operation The core operation to intercept.
     */
    public final void doInterceptor(ChainId chainId, T target, Operation<T> operation) {
        InterceptorChain<T> chain = chainMap.get(chainId);
        if (chain == null) {
            throw new NotFoundException("InterceptorChain Not Found For [" + chainId.id() + "]");
        }

        chain.doExecute(target, operation);
    }

    public final void doInterceptor(ChainId chainId, T target) {
        InterceptorChain<T> chain = chainMap.get(chainId);
        if (chain == null) {
            throw new NotFoundException("InterceptorChain Not Found For [" + chainId.id() + "]");
        }

        chain.doExecute(target, null);
    }

}
