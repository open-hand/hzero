package org.hzero.export;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 异步导出任务管理器
 * @author XCXCXCXCX
 * @date 2019/8/5
 */
public class ExportFutureManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportFutureManager.class);

    private Map<String, Future> futureMap = new ConcurrentHashMap<>();

    public List<String> getAllKeys(){
        return new ArrayList<>(futureMap.keySet());
    }

    public Future get(String uuid){
        return futureMap.get(uuid);
    }

    public Future get(UUID uuid){
        return get(uuid.toString());
    }

    public void put(UUID uuid, Future future){
        futureMap.put(uuid.toString(), future);
    }

    public void remove(UUID uuid){
        remove(uuid.toString());
    }

    public void remove(String uuid){
        futureMap.remove(uuid);
    }

    public TaskState cancel(String uuid){
        Future future = futureMap.remove(uuid);
        if (future == null) {
            LOGGER.warn("未找到该导出任务[" + uuid + "]");
            return TaskState.REMOVED;
        }
        if (future.isCancelled()) {
            LOGGER.warn("该导出任务[" + uuid + "]已取消");
            return TaskState.CANCELED;
        }
        if (future.isDone()) {
            LOGGER.warn("该导出任务[" + uuid + "]已完成");
            return TaskState.DONE;
        }
        return future.cancel(true) ? TaskState.SUCCESS : TaskState.FAILED;

    }

    public enum TaskState {
        //操作成功
        SUCCESS,
        //操作失败
        FAILED,
        //任务已移除
        REMOVED,
        //任务已取消
        CANCELED,
        //任务已结束
        DONE
    }

}
