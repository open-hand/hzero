package org.hzero.scheduler.app.service;

import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.domain.entity.ConcurrentRequest;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 并发请求Service
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/13 14:52
 */
public interface ConcurrentRequestService {


    /**
     * 分页查询
     *
     * @param requestQueryDTO 并发请求查询参数
     * @param pageRequest     分页
     * @return 并发请求集合
     */
    Page<ConcurrentRequest> pageRequest(RequestQueryDTO requestQueryDTO, PageRequest pageRequest);

    /**
     * 新建
     *
     * @param concurrentRequest 并发请求对象
     * @return 新建的对象
     */
    ConcurrentRequest createRequest(ConcurrentRequest concurrentRequest);
}
