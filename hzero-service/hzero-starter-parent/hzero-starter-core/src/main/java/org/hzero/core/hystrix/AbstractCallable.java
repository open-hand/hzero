package org.hzero.core.hystrix;

import java.util.concurrent.Callable;

public abstract class AbstractCallable<T> implements Callable<T> {
    private final Callable<T> target;

    public AbstractCallable(Callable<T> target) {
        this.target = target;
    }

    @Override
    public T call() throws Exception {
        if (target != null) {
            return target.call();
        }
        return null;
    }
}
