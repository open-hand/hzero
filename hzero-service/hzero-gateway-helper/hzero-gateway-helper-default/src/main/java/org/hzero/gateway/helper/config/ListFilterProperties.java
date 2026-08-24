package org.hzero.gateway.helper.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 名单过滤器配置
 * </p>
 *
 * @author qingsheng.chen 2019/3/15 星期五 13:53
 */
@Component
@ConfigurationProperties(prefix = "hzero.filter")
public class ListFilterProperties {
    private WhiteList whiteList = new WhiteList();
    private BlackList blackList = new BlackList();

    public WhiteList getWhiteList() {
        return whiteList;
    }

    public ListFilterProperties setWhiteList(WhiteList whiteList) {
        this.whiteList = whiteList;
        return this;
    }

    public BlackList getBlackList() {
        return blackList;
    }

    public ListFilterProperties setBlackList(BlackList blackList) {
        this.blackList = blackList;
        return this;
    }

    public static class WhiteList {
        private boolean enable = false;
        /**
         * eg. 192.10,11.0-100.*
         */
        private List<String> ip;

        public boolean isEnable() {
            return enable;
        }

        public WhiteList setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public List<String> getIp() {
            return ip;
        }

        public WhiteList setIp(List<String> ip) {
            this.ip = ip;
            return this;
        }
    }

    public static class BlackList {
        private boolean enable = false;
        /**
         * eg. 192.10,11.0-100.*
         */
        private List<String> ip;

        public boolean isEnable() {
            return enable;
        }

        public BlackList setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public List<String> getIp() {
            return ip;
        }

        public BlackList setIp(List<String> ip) {
            this.ip = ip;
            return this;
        }
    }
}
