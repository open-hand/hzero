package org.hzero.core.properties;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Service 属性配置
 *
 * @author bojiangzhou 2019/07/27
 */
@ConfigurationProperties(prefix = ServiceProperties.PREFIX, ignoreInvalidFields = true)
public class ServiceProperties {

    public static final String PREFIX = "hzero";

    private Map<String, Service> service = new HashMap<>();

    public Map<String, Service> getService() {
        return service;
    }

    public void setService(Map<String, Service> service) {
        this.service = service;
    }

    public static class Service {
        /**
         * service simple code
         */
        private String name;
        /**
         * service id
         */
        private String code;
        /**
         * service path
         */
        private String path;
        /**
         * service port
         */
        private int port;
        /**
         * service redis database
         */
        private int redisDb;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getRedisDb() {
            return redisDb;
        }

        public void setRedisDb(int redisDb) {
            this.redisDb = redisDb;
        }
    }

}
