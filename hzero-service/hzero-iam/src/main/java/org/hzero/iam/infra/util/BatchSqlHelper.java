package org.hzero.iam.infra.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.util.AsyncTask;
import org.hzero.core.util.CommonExecutor;
import org.hzero.mybatis.util.DatabaseUtils;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class BatchSqlHelper {

    private static ThreadPoolExecutor executor;

    public static <T> void batchExecute(List<T> dataList, int paramSize, Consumer<List<T>> consumer, String taskName) {
        int maxBathSize = DatabaseUtils.maxBatchSize(paramSize);

        initExecutor();

        if (dataList.size() > maxBathSize) {
            List<List<T>> subList = CollectionSubUtils.subList(dataList, maxBathSize);

            List<AsyncTask<Integer>> tasks = subList.stream().map(list -> (AsyncTask<Integer>) () -> {
                consumer.accept(list);
                return list.size();
            }).collect(Collectors.toList());
            CommonExecutor.batchExecuteAsync(tasks, executor, taskName);
        } else {
            consumer.accept(dataList);
        }
    }

    public static <T> void batchExecute(Set<T> dataList, int paramSize, Consumer<Set<T>> consumer, String taskName) {
        int maxBathSize = DatabaseUtils.maxBatchSize(paramSize);

        initExecutor();

        if (dataList.size() > maxBathSize) {
            List<Set<T>> subList = CollectionSubUtils.subSet(dataList, maxBathSize);

            List<AsyncTask<Integer>> tasks = subList.stream().map(list -> (AsyncTask<Integer>) () -> {
                consumer.accept(list);
                return list.size();
            }).collect(Collectors.toList());
            CommonExecutor.batchExecuteAsync(tasks, executor, taskName);
        } else {
            consumer.accept(dataList);
        }
    }

    private static void initExecutor() {
        if (executor == null) {
            synchronized (BatchSqlHelper.class) {
                BatchSqlHelper.executor = ApplicationContextHelper.getContext().getBean("IamCommonAsyncTaskExecutor", ThreadPoolExecutor.class);
            }
        }
    }


}
