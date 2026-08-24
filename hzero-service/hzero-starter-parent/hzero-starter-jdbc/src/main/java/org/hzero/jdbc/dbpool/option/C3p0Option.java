package org.hzero.jdbc.dbpool.option;

/**
 * C3p0连接池参数属性
 *
 * @author xianzhi.chen@hand-china.com 2018年11月8日上午11:23:54
 */
public class C3p0Option {

    public static final String INITIAL_POOL_SIZE = "initialPoolSize";
    public static final String MIN_POOL_SIZE = "minPoolSize";
    public static final String MAX_POOL_SIZE = "maxPoolSize";
    public static final String MAX_STATEMENTS = "maxStatements";
    public static final String MAX_IDLE_TIME = "maxIdleTime";
    public static final String ACQUIRE_INCREMENT = "acquireIncrement";
    public static final String ACQUIRE_RETRY_ATTEMPTS = "acquireRetryAttempts";
    public static final String IDLE_CONNECTION_TEST_PERIOD = "idleConnectionTestPeriod";
    public static final String BREAK_AFTER_ACQUIRE_FAILURE = "breakAfterAcquireFailure";
    public static final String TEST_CONNECTION_ON_CHECKOUT = "testConnectionOnCheckout";

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
