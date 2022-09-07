package org.hzero.boot.imported.app.service;

import java.util.List;

/**
 * 批量导入接口
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/11 9:56
 */
public interface IBatchImportService {

    /**
     * 从临时表拉取的数据批量
     * 小于1表示不分批
     *
     * @return 批量
     */
    default int getSize() {
        return 0;
    }

    /**
     * 逐条导入处理程序
     *
     * @param data 行数据，Json格式，可以转化为Json对象进行处理
     * @return true成功  false失败
     */
    Boolean doImport(List<String> data);

    /**
     * 导入开始前调用
     */
    default void onStart(){

    }

    /**
     * 导入结束后调用
     */
    default void onFinish(){

    }
}
