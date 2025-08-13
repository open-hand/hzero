package org.hzero.scheduler.infra.util;

import org.hzero.scheduler.infra.constant.HsdrConstant;

/**
 * 生成Cron表达式
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/18 20:24
 */
public class CronUtils {

    private CronUtils() {
    }

    /**
     * 周期性corn表达式
     *
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @param number 间隔大小
     * @return cron表达式
     */
    public static String getCron(Long hour, Long minute, Long second, Long number, String intervalType) {
        if (HsdrConstant.IntervalType.DAY.equals(intervalType)) {
            return second + " " + minute + " " + hour + " 1/" + number + " * ? ";
        } else if (HsdrConstant.IntervalType.HOUR.equals(intervalType)) {
            return second + " " + minute + " 0/" + number + " * * ? ";
        } else if (HsdrConstant.IntervalType.MINUTE.equals(intervalType)) {
            return second + " 0/" + number + " * * * ? ";
        } else if (HsdrConstant.IntervalType.SECOND.equals(intervalType)) {
            return "0/" + number + " * * * * ? ";
        }
        return null;
    }
}