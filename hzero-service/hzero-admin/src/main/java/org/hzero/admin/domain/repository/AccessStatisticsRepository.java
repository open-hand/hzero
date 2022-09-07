package org.hzero.admin.domain.repository;

import org.hzero.admin.api.dto.ApiAccessStatisticsDTO;
import org.hzero.admin.api.dto.ServiceAccessStatisticsDTO;

import java.time.LocalDate;
import java.util.Map;

/**
 * 访问统计资源库接口
 *
 * @author bergturing on 2020-5-7.
 */
public interface AccessStatisticsRepository {
    /**
     * 查询各在线服务API总数
     *
     * @return 各在线服务API总数
     */
    Map<String, Object> queryInstanceApiCount();

    /**
     * 查询单个API访问总次数
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @param service   服务名称
     * @return 查询结果
     */
    ApiAccessStatisticsDTO queryApiInvokeCount(LocalDate beginDate, LocalDate endDate, String service);

    /**
     * 查询各服务API访问总次数
     *
     * @param beginDate 开始日期
     * @param endDate   结束日期
     * @return 查询结果
     */
    ServiceAccessStatisticsDTO queryServiceInvokeCount(LocalDate beginDate, LocalDate endDate);
}
