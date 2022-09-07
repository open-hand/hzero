package org.hzero.gateway.helper.util;

import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * <p>
 * IP 工具类
 * </p>
 *
 * @author qingsheng.chen 2019/3/20 星期三 10:59
 */
public class IpUtils {
    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private IpUtils() {
    }

    public static boolean match(Collection<String> ruleList, String ip) {
        if (org.apache.commons.collections.CollectionUtils .isEmpty(ruleList)) {
            return false;
        }
        for (String rule : ruleList) {
            if (match(rule, ip)) {
                return true;
            }
        }
        return false;
    }

    public static boolean match(String rule, String ip) {
        if (!StringUtils.hasText(rule)) {
            return false;
        }
        String[] ruleSegments = rule.split("\\.");
        if (ruleSegments.length != BaseConstants.Digital.FOUR) {
            logger.error("Non-compliant IP rules : {}", rule);
            return false;
        }
        String[] ipSegments = ip.split("\\.");
        if (ipSegments.length != BaseConstants.Digital.FOUR) {
            logger.error("Non-compliant IP : {}", ip);
            return false;
        }
        boolean match = true;
        for (int i = 0; i < BaseConstants.Digital.FOUR; ++i) {
            String ruleSegment = ruleSegments[i];
            int ipSegment = Integer.parseInt(ipSegments[i]);
            match &= match(ruleSegment, ipSegment);
        }
        return match;
    }

    public static boolean match(String ruleSegment, int ipSegment) {
        if (!StringUtils.hasText(ruleSegment)) {
            return false;
        }
        String[] ruleItems = ruleSegment.split(",");
        for (String ruleItem : ruleItems) {
            if ("*".equals(ruleItem)) {
                return true;
            } else if (ruleItem.contains("-")) {
                String[] ruleIps = ruleItem.split("-");
                int start = Integer.parseInt(ruleIps[0]);
                int end = Integer.parseInt(ruleIps[ruleIps.length - 1]);
                if (start <= ipSegment && ipSegment <= end) {
                    return true;
                }
            } else {
                int target = Integer.parseInt(ruleItem);
                if (target == ipSegment) {
                    return true;
                }
            }
        }
        return false;
    }
}
