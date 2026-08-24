package org.hzero.platform.app.service;

import org.hzero.boot.platform.entity.dto.RegistParam;

/**
 * 实体表应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-07-08 15:56:36
 */
public interface EntityTableService {
    /**
     * 执行注册
     *
     * @param param 注册参数
     */
    void doRegist(RegistParam param);

    /**
     * 初始化数据到缓存
     */
    void initAllDataToRedis();

}
