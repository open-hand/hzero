package org.hzero.boot.scheduler.infra.constant;

/**
 * 调度服务客户端常量类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/17 11:12
 */
public class BootSchedulerConstant {

    private BootSchedulerConstant() {
    }

    public static final String LOG_DIR = "hsdr01";

    public static final String QUEUE_NAME = "hsdr-log";

    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String WARN = "WARN";
    public static final String ERROR = "ERROR";

    public static final String WRAP = System.getProperty("line.separator");

    /**
     * 任务执行api
     */
    public static final String EXECUTOR_PATH = "/v1/scheduler/executor";
    /**
     * 任务停止api
     */
    public static final String STOP_JOB_PATH = "/v1/scheduler/stop";
    /**
     * 任务暂停api
     */
    public static final String PAUSE_JOB_PATH = "/v1/scheduler/pause";

    /**
     * 健康检查api
     */
    public static final String EXECUTOR_HEALTH_PATH = "/scheduler/executor/health";

    public static final int MAX_LOG = 2000;
    public static final int HEADER_MAX_LOG = 400;

    public static final class Response {
        private Response() {
        }

        public static final String SUCCESS = "SUCCESS";
        public static final String FAILURE = "FAILURE";
    }

    /**
     * 客户端执行结果
     */
    public static final class ClientResult {
        private ClientResult() {
        }

        public static final String CODE = "HSDR.LOG.CLIENT_RESULT";
        public static final String SUCCESS = "SUCCESS";
        public static final String WARNING = "WARNING";
        public static final String FAILURE = "FAILURE";
        public static final String DOING = "DOING";
    }
}
