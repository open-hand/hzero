package org.hzero.core.observer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

/**
 * 观察者事件总线
 *
 * @author bojiangzhou 2020/07/08
 */
public class EventBus<T> {

    protected final String identifier;
    protected final List<? extends Observer<T>> observers;

    /**
     * 构造事件总线
     *
     * @param identifier 事件总线标识
     * @param observers  观察者列表
     */
    public EventBus(String identifier, List<? extends Observer<T>> observers) {
        this.identifier = identifier;
        this.observers = Optional.ofNullable(observers).orElse(new ArrayList<>())
                .stream().sorted(Comparator.comparingInt(Observer::order)).collect(Collectors.toList());
    }

    public void notifyObservers(T target) {
        notifyObservers(null);
    }

    public void notifyObservers(T target, Object... args) {
        if (CollectionUtils.isEmpty(observers)) {
            return;
        }

        for (Observer<T> observer : observers) {
            observer.update(target, args);
        }
    }

    public String getIdentifier() {
        return identifier;
    }
}
