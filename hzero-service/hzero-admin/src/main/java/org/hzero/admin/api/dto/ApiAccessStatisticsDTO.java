package org.hzero.admin.api.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * API访问统计数据传输对象
 *
 * @author bergturing on 2020-5-7.
 */
public class ApiAccessStatisticsDTO extends BaseAccessStatisticsDTO<ApiAccessStatisticsDTO.ApiDetail> {
    private ApiAccessStatisticsDTO(List<String> dates, Collection<String> apis, Collection<ApiDetail> details) {
        super(dates, apis, details);
    }

    /**
     * 静态工厂方法
     *
     * @param dates   日期信息
     * @param apis    API列表
     * @param details API详细信息
     * @return API访问统计数据传输对象
     */
    public static ApiAccessStatisticsDTO of(List<String> dates, Collection<String> apis, Collection<ApiDetail> details) {
        return new ApiAccessStatisticsDTO(dates, apis, details);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * API详细信息
     *
     * @author bergturing on 2020-5-7.
     */
    public static class ApiDetail {
        /**
         * 具体的api
         */
        private String api;
        /**
         * 访问的具体统计数据
         */
        private int[] data;

        private ApiDetail(String api, int[] data) {
            this.api = api;
            this.data = data;
        }

        /**
         * 静态工厂方法
         *
         * @param api  具体的api
         * @param data 访问的具体统计数据
         * @return API详细信息
         */
        public static ApiDetail of(String api, int[] data) {
            return new ApiDetail(api, data);
        }

        public int[] getData() {
            return this.data;
        }

        public String getApi() {
            return this.api;
        }

        @Override
        public String toString() {
            return "ApiDetail{" +
                    "api='" + api + '\'' +
                    ", data=" + Arrays.toString(data) +
                    '}';
        }
    }
}
