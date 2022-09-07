package org.hzero.core.async;

import java.util.Map;

/**
 * 异步模版
 * @author XCXCXCXCX
 * @date 2019/8/7
 */
public interface AsyncTemplate<T extends TaskDTO> {

    default String submit(T dto, Executor executor){
        String uuid = executor.execute();
        afterSubmit(dto);
        return uuid;
    }

    void afterSubmit(T dto);

    Object doWhenFinish(T dto, Map<String, Object> additionInfo);

    Object doWhenOccurException(T dto, Throwable e);

}
