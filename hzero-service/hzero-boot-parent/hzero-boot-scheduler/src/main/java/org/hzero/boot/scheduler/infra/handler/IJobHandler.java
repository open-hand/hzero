package org.hzero.boot.scheduler.infra.handler;

import java.util.Map;

import org.hzero.boot.scheduler.infra.enums.ReturnT;
import org.hzero.boot.scheduler.infra.tool.SchedulerTool;

/**
 * 任务执行器
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/16 16:19
 */
public interface IJobHandler {

    /**
     * 任务执行
     *
     * @param map  任务参数
     * @param tool 工具
     * @return 任务结果
     */
    ReturnT execute(Map<String, String> map, SchedulerTool tool);

    /**
     * 任务开始前执行
     *
     * @param tool 工具
     */
    default void onCreate(SchedulerTool tool) {
    }

    /**
     * 捕获到异常时执行
     *
     * @param tool 工具
     */
    default void onException(SchedulerTool tool) {
    }

    /**
     * 任务正常结束时执行
     *
     * @param tool    工具
     * @param returnT 任务结果
     */
    default void onFinish(SchedulerTool tool, ReturnT returnT) {
    }
}
