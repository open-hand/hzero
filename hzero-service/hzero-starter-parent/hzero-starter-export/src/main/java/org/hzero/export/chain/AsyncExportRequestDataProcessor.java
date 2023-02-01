package org.hzero.export.chain;

import java.lang.reflect.Method;

import org.hzero.export.entity.ExportTaskDTO;

/**
 * 异步导出数据查询埋点
 *
 * @author xiangyu.qi01@hand-china.com on 2021-10-27
 */
public interface AsyncExportRequestDataProcessor {

    /**
     * 切入点的执行顺序，从小到大
     * 0 到 10 为平台预留，项目请使用大于10的值
     *
     * @return 顺序
     */
    default int order() {
        return 50;
    }


    /**
     * 请求数据前置处理
     *
     * @param target
     * @param method
     * @param args
     * @param exportTaskDTO 只有异步任务时才有
     */
    void before(Object target, Method method, Object[] args, ExportTaskDTO exportTaskDTO);

    /**
     * 请求数据后置处理
     *
     * @param target
     * @param method
     * @param args
     * @param result        请求的结果，
     * @param exportTaskDTO 只有异步任务时才有
     * @return 修改后的请求结果，如果不修改请返回 resutl
     */
    Object after(Object target, Method method, Object[] args, Object result, ExportTaskDTO exportTaskDTO);

}
