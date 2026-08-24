package org.hzero.config.domain.repository;

import org.hzero.config.domain.entity.ServiceConfig;
import org.hzero.config.domain.vo.ConfigParam;
import org.hzero.mybatis.base.BaseRepository;


/**
 * 服务配置资源库
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
public interface ServiceConfigRepository extends BaseRepository<ServiceConfig> {

    /**
     * 不启用服务治理，查询配置
     *
     * @param param 配置参数
     * @return ServiceConfig
     */
    ServiceConfig selectDefaultConfig(ConfigParam param);

}
