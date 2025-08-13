package org.hzero.admin.app.service.impl;

import org.hzero.admin.api.dto.ApiAccessStatisticsDTO;
import org.hzero.admin.api.dto.ServiceAccessStatisticsDTO;
import org.hzero.admin.app.service.AccessStatisticsService;
import org.hzero.admin.domain.repository.AccessStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

/**
 * 访问统计服务实现
 *
 * @author bergturing on 2020-5-7.
 */
@Service
public class AccessStatisticsServiceImpl implements AccessStatisticsService {
    /**
     * 访问统计资源库对象
     */
    private final AccessStatisticsRepository accessStatisticsRepository;

    @Autowired
    public AccessStatisticsServiceImpl(AccessStatisticsRepository accessStatisticsRepository) {
        this.accessStatisticsRepository = accessStatisticsRepository;
    }

    @Override
    public Map<String, Object> queryInstanceApiCount() {
        // 查询数据，并返回结果
        return this.accessStatisticsRepository.queryInstanceApiCount();
    }

    @Override
    public ApiAccessStatisticsDTO queryApiInvokeCount(LocalDate beginDate, LocalDate endDate, String service) {
        // 查询数据，并返回结果
        return this.accessStatisticsRepository.queryApiInvokeCount(beginDate, endDate, service);
    }

    @Override
    public ServiceAccessStatisticsDTO queryServiceInvokeCount(LocalDate beginDate, LocalDate endDate) {
        // 查询数据，并返回结果
        return this.accessStatisticsRepository.queryServiceInvokeCount(beginDate, endDate);
    }
}
