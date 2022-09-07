package org.hzero.scheduler.infra.constant;

/**
 * 调度服务错误代码
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/08 19:36
 */
public class HsdrErrorCode {

    private HsdrErrorCode() {
    }

    /**
     * 编码重复
     */
    public static final String CODE_REPEAT = "hsdr.error.code_repeat";
    /**
     * 不可被禁用
     */
    public static final String UPDATE_FLAG_ERROR = "hsdr.error.update_flag_error";
    /**
     * 不可被删除
     */
    public static final String DELETE_ERROR = "hsdr.error.delete_error";
    /**
     * 可执行不存在
     */
    public static final String EXECUTABLE_NOT_EXIST = "hsdr.error.executable_not_exist";
    /**
     * 权限表唯一性校验重复
     */
    public static final String PERMISSION_TENANT_REPEAT = "hsdr.error.permission_tenant_repeat";
    /**
     * 参数格式错误
     */
    public static final String WRONG_FORMAT = "hsdr.error.wrong_format";
    /**
     * 执行器不存在
     */
    public static final String EXECUTOR_NOT_FIND = "hsdr.error.executor_not_find";
    /**
     * 获取用户信息失败
     */
    public static final String USER_DETAIL_NOT_FOUND = "hsdr.error.user_detail_not_found";
    /**
     * 当前用户没有权限创建请求
     */
    public static final String USER_AUTHORITY_ERROR = "hsdr.error.user_authority_error";
    /**
     * 关联请求不存在
     */
    public static final String CONCURRENT_NOT_FIND = "hsdr.error.concurrent_not_find";
    /**
     * 参数错误
     */
    public static final String PARAMETER_ERROR = "hsdr.error.parameter_error";
    /**
     * 任务创建失败
     */
    public static final String CREATE_JOB = "hsdr.error.create_job";
    /**
     * 请求次数超过限制
     */
    public static final String LIMIT_QUANTITY_OUT = "hsdr.error.limit_quantity_out";
    /**
     * 地址不能为空
     */
    public static final String ADDRESS_LIST_NULL = "hsdr.error.addressList_null";
    /**
     * 任务正在执行
     */
    public static final String JOB_IS_RUNNING = "hsdr.error.job_is_running";
    /**
     * 最大并发
     */
    public static final String MAX_CONCURRENT = "hsdr.error.max_concurrent";
    /**
     * 编辑错误
     */
    public static final String EDIT_JOB = "hsdr.error.edit_job";
    /**
     * 任务更新
     */
    public static final String UPDATE_JOB = "hsdr.error.update_job";
    /**
     * 执行器地址格式错误
     */
    public static final String EXECUTOR_LIST = "hsdr.error.executor_list";
    /**
     * 执行器状态错误
     */
    public static final String EXECUTOR_STATUS = "hsdr.error.executor_status";
    /**
     * 任务停止被禁止
     */
    public static final String KILL_JOB_THREAD = "hsdr.error.kill_job_thread";

    public static class Quartz {
        private Quartz() {
        }

        public static final String ADD_JOB = "hsdr.error.quartz.add";
        public static final String UPDATE_JOB = "hsdr.error.quartz.update";
        public static final String DELETE_JOB = "hsdr.error.quartz.delete";
        public static final String TRIGGER_JOB = "hsdr.error.quartz.trigger";
        public static final String PAUSE_JOB = "hsdr.error.quartz.pause";
        public static final String RESUME_JOB = "hsdr.error.quartz.resume";
    }
}
