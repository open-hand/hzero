package org.hzero.core.observer;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;

/**
 * 支持异步执行的事件总线，注意观察者执行顺序无法保证
 *
 * @author bojiangzhou 2020/07/08
 */
public class AsyncEventBus<T> extends EventBus<T> {

    private final ThreadPoolExecutor executor;

    public AsyncEventBus(String identifier, List<? extends Observer<T>> observers, ThreadPoolExecutor executor) {
        super(identifier, observers);
        this.executor = executor;
    }

    @Override
    public void notifyObservers(T target) {
        notifyObservers(target, new Object[]{});
    }

    @Override
    public void notifyObservers(T target, Object... args) {
        if (CollectionUtils.isEmpty(observers)) {
            return;
        }

        CustomUserDetails self = DetailsHelper.getUserDetails();

        List<AsyncTask<Boolean>> tasks = super.observers.stream().map(o -> new AsyncTask<Boolean>() {
            @Override
            public String taskName() {
                return o.getClass().getSimpleName();
            }

            @Override
            public Boolean doExecute() {
                if (self != null) {
                    DetailsHelper.setCustomUserDetails(self);
                }
                o.update(target, args);
                return Boolean.TRUE;
            }
        }).collect(Collectors.toList());

        CommonExecutor.batchExecuteAsync(tasks, executor, identifier);
    }
}
