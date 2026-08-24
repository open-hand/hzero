package org.hzero.admin.app.service.metric;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/11 10:46 上午
 */
public class MetricMap {

    private Map<String, RequestCount> requestCountMap = new HashMap<>();

    public Map<String, RequestCount> getRequestCountMap() {
        return requestCountMap;
    }

    public void setRequestCountMap(Map<String, RequestCount> requestCountMap) {
        this.requestCountMap = requestCountMap;
    }

    public static class RequestCount{

        private int maxRequests;

        private int minRequests;

        private int avgCount;

        private int avgFailedRequests;

        private int avgRequests;

        private int sumFailedRequests;

        private int sumRequests;

        private long startTime;

        private long endTime;

        public int getMaxRequests() {
            return maxRequests;
        }

        public void setMaxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
        }

        public int getMinRequests() {
            return minRequests;
        }

        public void setMinRequests(int minRequests) {
            this.minRequests = minRequests;
        }

        public int getAvgCount() {
            return avgCount;
        }

        public void setAvgCount(int avgCount) {
            this.avgCount = avgCount;
        }

        public int getAvgFailedRequests() {
            return avgFailedRequests;
        }

        public void setAvgFailedRequests(int avgFailedRequests) {
            this.avgFailedRequests = avgFailedRequests;
        }

        public int getAvgRequests() {
            return avgRequests;
        }

        public void setAvgRequests(int avgRequests) {
            this.avgRequests = avgRequests;
        }

        public int getSumFailedRequests() {
            return sumFailedRequests;
        }

        public void setSumFailedRequests(int sumFailedRequests) {
            this.sumFailedRequests = sumFailedRequests;
        }

        public int getSumRequests() {
            return sumRequests;
        }

        public void setSumRequests(int sumRequests) {
            this.sumRequests = sumRequests;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
    }

}
