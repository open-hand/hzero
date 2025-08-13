package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.HService;

import java.util.List;

/**
 * 应用服务Mapper
 *
 * @author bojiangzhou
 * @author zhiying.dong@hand-china.com 2018-12-14 10:35:51
 */
public interface ServiceMapper extends BaseMapper<HService> {

    /**
     * 查询服务列表（未启用服务治理时查询默认）
     *
     * @param param              应用服务实体
     * @return List<HService>    查询返回List参数
     */
    List<HService> selectDefaultServices(HService param);

    /**
     * 查询服务列表 关联服务版本（未启用服务治理时查询默认）
     *
     * @param param              应用服务实体
     * @return List<HService>    查询返回List参数
     */
    List<HService> selectDefaultServicesWithVersion(HService param);

    /**
     * 查询服务明细信息
     *
     * @param serviceId 主键id
     * @return HService
     */
    HService selectServiceDetails(@Param("serviceId") Long serviceId);
}
