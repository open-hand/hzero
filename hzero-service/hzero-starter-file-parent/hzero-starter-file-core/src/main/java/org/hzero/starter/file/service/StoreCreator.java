package org.hzero.starter.file.service;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 16:54
 */
public interface StoreCreator {

    /**
     * 获取文件存储类型，与值集HFLE.SERVER_PROVIDER对应即可
     *
     * @return 文件存储类型
     */
    Integer storeType();

    /**
     * 获取文件处理类
     *
     * @return 文件处理类实例化对象
     */
    AbstractFileService getFileService();
}
