package org.hzero.core.hystrix;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface HystrixCallableWrapper {
    default int order() {
        return 0;
    }

    default boolean shouldWrap() {
        return true;
    }

    <T> Callable<T> wrapCallable(Callable<T> callable);
}
