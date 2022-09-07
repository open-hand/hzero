package org.hzero.core.interceptor;

import java.util.function.Consumer;

/**
 * 通用拦截器链
 *
 * @author bojiangzhou 2020/05/28
 */
public final class InterceptorChain<T> {

    private final ChainId chainId;
    private final Consumer<T> preConsumer;
    private final Consumer<T> postConsumer;

    protected InterceptorChain(ChainId chainId, Consumer<T> preConsumer, Consumer<T> postConsumer) {
        this.chainId = chainId;
        this.preConsumer = preConsumer;
        this.postConsumer = postConsumer;
    }

    /**
     * 拦截器调用入口，将核心操作封装成 Consumer 对象传入。
     *
     * @param target      The target to handle.
     * @param operation The core operation to intercept.
     */
    public final void doExecute(T target, Operation<T> operation) {
        preConsumer.accept(target);

        if (operation != null) {
            operation.execute(target);
        }

        postConsumer.accept(target);
    }

    /**
     * @return The interceptor chain's name.
     */
    public ChainId getChainId() {
        return this.chainId;
    }

}
