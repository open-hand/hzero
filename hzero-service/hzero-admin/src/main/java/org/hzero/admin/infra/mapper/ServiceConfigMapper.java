package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.ServiceConfig;

import java.util.List;

/**
 * 服务配置Mapper
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 * @author xiaoyu.zhao@hand-china.com
 */
public interface ServiceConfigMapper extends BaseMapper<ServiceConfig> {

    /**
     * 分页查询服务配置信息,按照serviceCode和configVersion进行过滤
     *
     * @param serviceConfig 服务配置实体
     * @return List<ServiceConfig>
     */
    List<ServiceConfig> selectServiceConfigList(ServiceConfig serviceConfig);

    /**
     * 根据版本查询服务配置，版本为空时查询默认
     *
     * @param serviceCode 服务编码
     * @param version     版本
     * @return ServiceConfig
     */
    ServiceConfig selectConfigWithVersion(@Param("serviceCode") String serviceCode,
                                          @Param("version") String version);

    /**
     * 根据服务编码、版本查询配置数目
     * @param serviceCode
     * @param serviceVersion
     * @return 符合条件的配置数目
     */
    int selectConfigCount(String serviceCode, String serviceVersion);
}
