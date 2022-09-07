package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.domain.entity.Config;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2019/1/16 星期三 10:26
 */
public interface ConfigService {

    /**
     * 初始化公司配置
     *
     * @param configList 配置列表
     * @param tenantId   租户ID
     * @return 公司配置
     */
    List<Config> initCompanyConfig(Long tenantId, List<Config> configList);
}
