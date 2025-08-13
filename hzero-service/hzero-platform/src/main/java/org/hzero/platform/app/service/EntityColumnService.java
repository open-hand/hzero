package org.hzero.platform.app.service;

/**
 * 实体列应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
public interface EntityColumnService {
    /**
     * 删除注定entityTableId下所有的column
     *
     * @param entityTableId
     */
    void deleteByEntityTableId(Long entityTableId);

}
