package org.hzero.platform.domain.vo;

/**
 * 
 * 数据源连接池属性类
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月30日下午1:27:16
 */
public class DatasourcePoolOptionVO {

    /**
     * 
     * C3p0连接池参数属性
     * 
     */
    public static class C3p0OptionVO {

        private Integer initialPoolSize = 3;
        private Integer minPoolSize = 1;
        private Integer maxPoolSize = 20;
        private Integer maxStatements = 50;
        private Integer maxIdleTime = 1800;
        private Integer acquireIncrement = 3;
        private Integer acquireRetryAttempts = 30;
        private Integer idleConnectionTestPeriod = 60;
        private Boolean breakAfterAcquireFailure = false;
        private Boolean testConnectionOnCheckout = false;

        public Integer getInitialPoolSize() {
            return initialPoolSize;
        }

        public void setInitialPoolSize(Integer initialPoolSize) {
            this.initialPoolSize = initialPoolSize;
        }

        public Integer getMinPoolSize() {
            return minPoolSize;
        }

        public void setMinPoolSize(Integer minPoolSize) {
            this.minPoolSize = minPoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getMaxStatements() {
            return maxStatements;
        }

        public void setMaxStatements(Integer maxStatements) {
            this.maxStatements = maxStatements;
        }

        public Integer getMaxIdleTime() {
            return maxIdleTime;
        }

        public void setMaxIdleTime(Integer maxIdleTime) {
            this.maxIdleTime = maxIdleTime;
        }

        public Integer getAcquireIncrement() {
            return acquireIncrement;
        }

        public void setAcquireIncrement(Integer acquireIncrement) {
            this.acquireIncrement = acquireIncrement;
        }

        public Integer getAcquireRetryAttempts() {
            return acquireRetryAttempts;
        }

        public void setAcquireRetryAttempts(Integer acquireRetryAttempts) {
            this.acquireRetryAttempts = acquireRetryAttempts;
        }

        public Integer getIdleConnectionTestPeriod() {
            return idleConnectionTestPeriod;
        }

        public void setIdleConnectionTestPeriod(Integer idleConnectionTestPeriod) {
            this.idleConnectionTestPeriod = idleConnectionTestPeriod;
        }

        public Boolean getBreakAfterAcquireFailure() {
            return breakAfterAcquireFailure;
        }

        public void setBreakAfterAcquireFailure(Boolean breakAfterAcquireFailure) {
            this.breakAfterAcquireFailure = breakAfterAcquireFailure;
        }

        public Boolean getTestConnectionOnCheckout() {
            return testConnectionOnCheckout;
        }

        public void setTestConnectionOnCheckout(Boolean testConnectionOnCheckout) {
            this.testConnectionOnCheckout = testConnectionOnCheckout;
        }

    }

    /**
     * 
     * Dbcp2连接池参数属性
     * 
     */
    public static class Dbcp2OptionVO {

        private Integer initialSize = 3;
        private Integer maxIdle = 20;
        private Integer minIdle = 1;
        private Boolean logAbandoned = true;
        private Integer removeAbandonedTimeout = 180;
        private Integer maxWait = 1000;

        public Integer getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(Integer initialSize) {
            this.initialSize = initialSize;
        }

        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Integer getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(Integer minIdle) {
            this.minIdle = minIdle;
        }

        public Boolean getLogAbandoned() {
            return logAbandoned;
        }

        public void setLogAbandoned(Boolean logAbandoned) {
            this.logAbandoned = logAbandoned;
        }

        public Integer getRemoveAbandonedTimeout() {
            return removeAbandonedTimeout;
        }

        public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
            this.removeAbandonedTimeout = removeAbandonedTimeout;
        }

        public Integer getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(Integer maxWait) {
            this.maxWait = maxWait;
        }

    }

    /**
     * 
     * Druid连接池参数属性
     * 
     */
    public static class DruidOptionVO {

        private Integer initialSize = 3;
        private Integer maxActive = 20;
        private Integer minIdle = 1;
        private Integer maxWait = 60000;
        private Integer timeBetweenEvictionRunsMillis = 60000;
        private Integer minEvictableIdleTimeMillis = 300000;
        private Boolean testWhileIdle = true;
        private Boolean testOnBorrow = false;
        private Boolean testOnReturn = false;
        private Integer maxOpenPreparedStatements = 20;
        private Boolean removeAbandoned = true;
        private Integer removeAbandonedTimeout = 1800;
        private Boolean logAbandoned = true;
        private String validationQuery = "select 1";

        public Integer getInitialSize() {
            return initialSize;
        }

        public void setInitialSize(Integer initialSize) {
            this.initialSize = initialSize;
        }

        public Integer getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(Integer maxActive) {
            this.maxActive = maxActive;
        }

        public Integer getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(Integer minIdle) {
            this.minIdle = minIdle;
        }

        public Integer getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(Integer maxWait) {
            this.maxWait = maxWait;
        }

        public Integer getTimeBetweenEvictionRunsMillis() {
            return timeBetweenEvictionRunsMillis;
        }

        public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }

        public Integer getMinEvictableIdleTimeMillis() {
            return minEvictableIdleTimeMillis;
        }

        public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        }

        public Boolean getTestWhileIdle() {
            return testWhileIdle;
        }

        public void setTestWhileIdle(Boolean testWhileIdle) {
            this.testWhileIdle = testWhileIdle;
        }

        public Boolean getTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(Boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public Boolean getTestOnReturn() {
            return testOnReturn;
        }

        public void setTestOnReturn(Boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public Integer getMaxOpenPreparedStatements() {
            return maxOpenPreparedStatements;
        }

        public void setMaxOpenPreparedStatements(Integer maxOpenPreparedStatements) {
            this.maxOpenPreparedStatements = maxOpenPreparedStatements;
        }

        public Boolean getRemoveAbandoned() {
            return removeAbandoned;
        }

        public void setRemoveAbandoned(Boolean removeAbandoned) {
            this.removeAbandoned = removeAbandoned;
        }

        public Integer getRemoveAbandonedTimeout() {
            return removeAbandonedTimeout;
        }

        public void setRemoveAbandonedTimeout(Integer removeAbandonedTimeout) {
            this.removeAbandonedTimeout = removeAbandonedTimeout;
        }

        public Boolean getLogAbandoned() {
            return logAbandoned;
        }

        public void setLogAbandoned(Boolean logAbandoned) {
            this.logAbandoned = logAbandoned;
        }

        public String getValidationQuery() {
            return validationQuery;
        }

        public void setValidationQuery(String validationQuery) {
            this.validationQuery = validationQuery;
        }
    }


}
