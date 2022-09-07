package org.hzero.scheduler.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.domain.entity.ConcurrentRequest;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/10 16:06
 */
public interface ConcurrentRequestRepository extends BaseRepository<ConcurrentRequest> {

    /**
     * 查询列表
     *
     * @param pageRequest     分页
     * @param requestQueryDTO 并发请求查询参数
     * @return request列表
     */
    Page<ConcurrentRequest> pageRequest(PageRequest pageRequest, RequestQueryDTO requestQueryDTO);

    /**
     * 查询详情
     *
     * @param tenantId  租户Id
     * @param requestId 请求Id
     * @return 请求对象
     */
    ConcurrentRequest selectById(Long tenantId, Long requestId);
}
