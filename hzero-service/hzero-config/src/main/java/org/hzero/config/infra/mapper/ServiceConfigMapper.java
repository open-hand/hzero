package org.hzero.config.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.config.domain.entity.ServiceConfig;
import org.hzero.config.domain.vo.ConfigParam;

import java.util.List;

/**
 * 服务配置Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
public interface ServiceConfigMapper extends BaseMapper<ServiceConfig> {

    /**
     * 查询默认配置
     *
     * @param param 配置参数
     * @return ServiceConfig
     */
    ServiceConfig selectDefaultConfig(ConfigParam param);

}
