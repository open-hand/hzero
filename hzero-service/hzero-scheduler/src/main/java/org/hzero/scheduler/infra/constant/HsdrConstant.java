package org.hzero.scheduler.infra.constant;

/**
 * 调度平台常量类
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/10 14:46
 */
public class HsdrConstant {

    private HsdrConstant() {

    }

    /**
     * 并发程序明细接口，转译对象
     */
    public static final String BODY_PARAM_LIST = "body.paramList";

    /**
     * 不指定cron时的默认值  2120-01-01 00:00:00  执行一次
     */
    public static final String CRON = "0 0 0 1 1 ? 2120";

    public static final Integer HUNDRED = 100;
    public static final Long NEGATIVE_ONE = -1L;

    public static final String COMMA = ",";
    public static final String COLON = ":";

    public static final String TOKEN = "token";

    public static final class StrategyParam {
        private StrategyParam() {
        }

        /**
         * 最大重试次数
         */
        public static final String RETRY_NUMBER = "retryNumber";

        public static final String JOB_WEIGHT = "jobWeight";
    }

    /**
     * 任务类型
     */
    public static final class JobType {
        private JobType() {
        }

        public static final String CODE = "HSDR.GLUE_TYPE";
        /**
         * 简单任务
         */
        public static final String SIMPLE = "SIMPLE";
        /**
         * 脚本任务
         */
        public static final String SCRIPT = "SCRIPT";
    }

    /**
     * 时间间隔类型
     */
    public static final class IntervalType {
        private IntervalType() {
        }

        public static final String CODE = "HSDR.REQUEST.INTERVAL_TYPE";

        public static final String DAY = "DAY";
        public static final String HOUR = "HOUR";
        public static final String MINUTE = "MINUTE";
        public static final String SECOND = "SECOND";
    }

    /**
     * 任务状态
     */
    public static final class JobStatus {
        private JobStatus() {
        }

        public static final String CODE = "HSDR.JOB_STATUS";

        /**
         * 结束
         */
        public static final String NONE = "NONE";
        /**
         * 正常
         */
        public static final String NORMAL = "NORMAL";
        /**
         * 暂停
         */
        public static final String PAUSED = "PAUSED";
        /**
         * 完成
         */
        public static final String COMPLETE = "COMPLETE";
        /**
         * 错误
         */
        public static final String ERROR = "ERROR";
        /**
         * 阻塞
         */
        public static final String BLOCKED = "BLOCKED";
    }

    /**
     * 触发器状态
     * NONE： null  DELETED
     * NORMAL： WAITING  ACQUIRED  EXECUTING
     * PAUSED：PAUSED  PAUSED_BLOCKED
     * COMPLETE：COMPLETE
     * ERROR： ERROR
     * BLOCKED： BLOCKED
     */
    public static final class TriggerStatus {
        private TriggerStatus() {
        }

        public static final String WAITING = "WAITING";
        public static final String ACQUIRED = "ACQUIRED";
        public static final String EXECUTING = "EXECUTING";
        public static final String COMPLETE = "COMPLETE";
        public static final String BLOCKED = "BLOCKED";
        public static final String ERROR = "ERROR";
        public static final String PAUSED = "PAUSED";
        public static final String PAUSED_BLOCKED = "PAUSED_BLOCKED";
        public static final String DELETED = "DELETED";
    }

    /**
     * 执行器状态
     */
    public static final class ExecutorStatus {
        private ExecutorStatus() {
        }

        public static final String CODE = "HSDR.EXECUTOR_STATUS";

        /**
         * 在线
         */
        public static final String ONLINE = "ONLINE";
        /**
         * 下线
         */
        public static final String OFFLINE = "OFFLINE";
        /**
         * 自动下线
         */
        public static final String A_OFFLINE = "A-OFFLINE";
    }

    /**
     * 执行器策略
     */
    public static final class ExecutorStrategy {
        private ExecutorStrategy() {
        }

        public static final String CODE = "HSDR.EXECUTOR_STRATEGY";
        /**
         * 轮询
         */
        public static final String POLLING = "POLLING";
        /**
         * 执行器权重
         */
        public static final String WEIGHT = "WEIGHT";
        /**
         * 任务-执行器权重
         */
        public static final String JOB_WEIGHT = "JOB_WEIGHT";

    }

    /**
     * 失败处理策略
     */
    public static final class FailStrategy {
        private FailStrategy() {
        }

        public static final String CODE = "HSDR.FAIL_STRATEGY";
        /**
         * 忽略
         */
        public static final String IGNORE = "IGNORE";
        /**
         * 转移
         */
        public static final String TRANSFER = "TRANSFER";
        /**
         * 重试
         */
        public static final String RETRY = "RETRY";
    }

    /**
     * 任务调度结果
     */
    public static final class JobResult {
        private JobResult() {
        }

        public static final String CODE = "HSDR.LOG.JOB_RESULT";
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

    public static final class ClearLogType {
        private ClearLogType() {
        }

        public static final String CODE = "HSDR.JOB_LOG_CLEAR_TYPE";
        public static final String ONE_DAY = "1";
        public static final String ONE_WEEK = "2";
        public static final String ONE_MONTH = "3";
        public static final String THREE_MONTH = "4";
        public static final String SIX_MONTH = "5";
        public static final String ONE_YEAR = "6";
        public static final String ALL = "7";
        public static final String THREE_DAY = "8";
    }

    public static final class ParamFormat {
        private ParamFormat() {
        }

        public static final String NUMBER = "NUMERICAL";
        public static final String DATE = "DATE";
        public static final String TEXT = "TEXT";
    }
}
