package org.hzero.admin.infra.factory;

import org.hzero.admin.api.dto.ApiAccessStatisticsDTO;
import org.hzero.admin.api.dto.ServiceAccessStatisticsDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 访问统计工厂对象
 *
 * @author bergturing on 2020-5-7.
 */
@Component
public class AccessStatisticsFactory {
    /**
     * 创建API访问统计数据传输对象
     *
     * @param originData 原始数据   key ---> value === date ---> Map<api, count>
     * @return API访问统计数据传输对象
     */
    public ApiAccessStatisticsDTO createApiAccessStatisticsDTO(Map<String, Map<String, Integer>> originData) {
        // 日期信息
        List<String> dates = new ArrayList<>(originData.size());
        // api 详细信息
        Map<String, ApiAccessStatisticsDTO.ApiDetail> apiDetailMap = new HashMap<>(16);

        // 统计api访问
        originData.forEach((date, apiValues) -> {
            // 日期的大小作为序号
            int index = dates.size();
            dates.add(date);

            apiValues.forEach((api, count) -> {
                // api详情
                ApiAccessStatisticsDTO.ApiDetail apiDetail;

                // 判断detail数据是否存在
                if (apiDetailMap.containsKey(api)) {
                    apiDetail = apiDetailMap.get(api);
                } else {
                    apiDetail = ApiAccessStatisticsDTO.ApiDetail.of(api, new int[originData.size()]);
                    apiDetailMap.put(api, apiDetail);
                }

                // 设置值
                apiDetail.getData()[index] = count;
            });
        });

        // 创建并返回API访问统计数据传输对象对象
        return ApiAccessStatisticsDTO.of(dates, apiDetailMap.keySet(), apiDetailMap.values());
    }

    /**
     * 创建服务访问统计数据传输对象
     *
     * @param originData 原始数据   key ---> value === date ---> Map<service, count>
     * @return 服务访问统计数据传输对象
     */
    public ServiceAccessStatisticsDTO createServiceAccessStatisticsDTO(Map<String, Map<String, Integer>> originData) {
        // 日期信息
        List<String> dates = new ArrayList<>(originData.size());
        // 服务详细信息
        Map<String, ServiceAccessStatisticsDTO.ServiceDetail> serviceDetailMap = new HashMap<>(16);

        // 统计服务访问
        originData.forEach((date, serviceValues) -> {
            // 日期的大小作为序号
            int index = dates.size();
            dates.add(date);

            serviceValues.forEach((service, count) -> {
                // 服务详情
                ServiceAccessStatisticsDTO.ServiceDetail serviceDetail;

                if (serviceDetailMap.containsKey(service)) {
                    serviceDetail = serviceDetailMap.get(service);
                } else {
                    serviceDetail = ServiceAccessStatisticsDTO.ServiceDetail.of(service, new int[originData.size()]);
                    serviceDetailMap.put(service, serviceDetail);
                }

                // 设置值
                serviceDetail.getData()[index] = count;
            });
        });

        // 创建并返回服务访问统计数据传输对象
        return ServiceAccessStatisticsDTO.of(dates, serviceDetailMap.keySet(), serviceDetailMap.values());
    }
}
