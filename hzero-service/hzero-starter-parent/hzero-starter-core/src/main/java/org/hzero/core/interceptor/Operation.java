package org.hzero.core.interceptor;

@FunctionalInterface
public interface Operation<T> {
    void execute(T t);
}