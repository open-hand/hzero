package org.hzero.scheduler.domain.service;

import java.util.List;

import org.hzero.scheduler.domain.entity.Executor;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/17 14:12
 */
public interface IAddressService {

    /**
     * 根据服务名获取地址列表
     *
     * @param serverName 服务名
     * @return 地址
     */
    List<String> getAddressList(String serverName);

    /**
     * 获取执行器地址
     *
     * @param executor 执行器
     * @return 地址
     */
    List<String> getAddressList(Executor executor);

    /**
     * 校验是否服务名称
     *
     * @param serverName 服务名
     * @return 是否
     */
    boolean isServerName(String serverName);
}
