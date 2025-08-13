package org.hzero.boot.scheduler.infra.handler;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/15 15:45
 */
public interface IJobHandlerAllowStop extends IJobHandler {

    /**
     * 允许停止
     *
     * @return boolean
     */
    default boolean allowStop() {
        return false;
    }

    /**
     * 任务暂停会执行的方法
     */
    default void onPause() {
    }

    /**
     * 任务停止补偿执行
     */
    default void onStop() {
    }

    /**
     * 任务暂停会执行的方法
     *
     * @param thread 任务执行线程
     */
    default void onPause(Thread thread) {
    }

    /**
     * 任务停止补偿执行
     *
     * @param thread 任务执行线程
     */
    default void onStop(Thread thread) {
    }
}
