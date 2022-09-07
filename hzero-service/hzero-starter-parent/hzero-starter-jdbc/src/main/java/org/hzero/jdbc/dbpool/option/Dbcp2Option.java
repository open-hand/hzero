package org.hzero.jdbc.dbpool.option;

/**
 * Dbcp2连接池参数属性
 *
 * @author xianzhi.chen@hand-china.com 2018年11月8日上午11:43:54
 */
public class Dbcp2Option {

    public static final String INITIAL_SIZE = "initialSize";
    public static final String MAX_IDLE = "maxIdle";
    public static final String MIN_IDLE = "minIdle";
    public static final String LOG_ABANDONED = "logAbandoned";
    public static final String REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    public static final String MAX_WAIT = "maxWait";

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
