package org.hzero.admin.api.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 服务访问统计数据传输对象
 *
 * @author bergturing on 2020-5-7.
 */
public class ServiceAccessStatisticsDTO extends BaseAccessStatisticsDTO<ServiceAccessStatisticsDTO.ServiceDetail> {
    private ServiceAccessStatisticsDTO(List<String> dates, Collection<String> services, Collection<ServiceDetail> details) {
        super(dates, services, details);
    }

    /**
     * 静态工厂方法
     *
     * @param dates    日期信息
     * @param services 服务列表
     * @param details  服务详细信息
     * @return 服务访问统计数据传输对象
     */
    public static ServiceAccessStatisticsDTO of(List<String> dates, Collection<String> services, Collection<ServiceDetail> details) {
        return new ServiceAccessStatisticsDTO(dates, services, details);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * 服务详细信息
     *
     * @author bergturing on 2020-5-7.
     */
    public static class ServiceDetail {
        /**
         * 具体的服务
         */
        private String service;
        /**
         * 访问的具体统计数据
         */
        private int[] data;

        private ServiceDetail(String service, int[] data) {
            this.service = service;
            this.data = data;
        }

        /**
         * 静态工厂方法
         *
         * @param service 具体的服务
         * @param data    访问的具体统计数据
         * @return 服务详细信息
         */
        public static ServiceDetail of(String service, int[] data) {
            return new ServiceDetail(service, data);
        }

        public int[] getData() {
            return this.data;
        }

        public String getService() {
            return this.service;
        }

        @Override
        public String toString() {
            return "ServiceDetail{" +
                    "service='" + service + '\'' +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }
    }
}
