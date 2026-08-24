package org.hzero.export.endpoint;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.util.Assert;

import org.hzero.export.ExportFutureManager;
import org.hzero.export.constant.AsyncTaskState;

/**
 * 异步导出端点
 *
 * @author XCXCXCXCX 2019/8/5
 */
@Endpoint(id = "async-export-endpoint")
public class AsyncExportEndpoint {

    private static final String NOT_FOUND = "404";
    private static final String SUCCESS = "success";
    private static final String FAILED = "failed";

    @Autowired
    private ExportFutureManager futureManager;

    @ReadOperation
    public List<String> getExportTaskIds() {
        return futureManager == null ? null : futureManager.getAllKeys();
    }

    /**
     * 404 or done or cancelled or doing
     */
    @ReadOperation
    public String getExportTaskState(@Selector String uuid) {
        Assert.notNull(uuid, "UUID must not be empty");
        Future<?> future;
        if (futureManager == null || (future = futureManager.get(UUID.fromString(uuid))) == null) {
            return NOT_FOUND;
        }
        if (future.isDone()) {
            return AsyncTaskState.DONE.getCode();
        }
        if (future.isCancelled()) {
            return AsyncTaskState.CANCELLED.getCode();
        }
        return AsyncTaskState.DOING.getCode();
    }

    /**
     * success or failed
     * <p>
     * success when task canceled or removed
     * failed when task done or canceled failed
     */
    @WriteOperation
    public ExportFutureManager.TaskState cancelExportTask(@Selector String uuid) {
        Assert.notNull(uuid, "UUID must not be empty");
        if (futureManager != null) {
            return futureManager.cancel(uuid);
        }
        return ExportFutureManager.TaskState.FAILED;
    }

}
