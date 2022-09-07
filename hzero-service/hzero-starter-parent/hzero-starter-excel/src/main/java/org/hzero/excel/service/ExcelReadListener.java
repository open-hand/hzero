package org.hzero.excel.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 导入执行
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/03 16:36
 */
public interface ExcelReadListener {

    /**
     * 导入执行方法
     *
     * @param object 参数对象
     */
    void invoke(JSONObject object);

    /**
     * 开始
     */
    default void onStart(){
    }

    /**
     * 结束
     */
    default void onFinish(){
    }
}
