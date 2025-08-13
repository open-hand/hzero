package org.hzero.scheduler.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.domain.entity.ConcurrentRequest;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 并发请求Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-13 14:30:24
 */
public interface ConcurrentRequestMapper extends BaseMapper<ConcurrentRequest> {

    /**
     * 查询列表
     *
     * @param requestQueryDTO 并发请求查询参数
     * @return request列表
     */
    List<ConcurrentRequest> listRequest(RequestQueryDTO requestQueryDTO);

    /**
     * 查询详情
     *
     * @param tenantId  租户Id
     * @param requestId 请求Id
     * @return 请求对象
     */
    ConcurrentRequest queryRequestById(@Param("tenantId") Long tenantId,
                                       @Param("requestId") Long requestId);
}
