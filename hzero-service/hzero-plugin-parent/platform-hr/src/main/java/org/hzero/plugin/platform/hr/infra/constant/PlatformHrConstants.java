package org.hzero.plugin.platform.hr.infra.constant;


/**
 * 平台常量
 *
 * @author gaokuo.dai@hand-china.com 2018年6月12日下午2:38:38
 */
public class PlatformHrConstants {
    private PlatformHrConstants() {
    }

    public static final String SPLIT = "|";

    /**
     * 快速索引长度
     */
    public static final int QUICK_INDEX_LENGTH = 240;
    public static final int PINYIN_LENGTH = 240;
    public static final String EMP_ASSIGN_CACHE_KEY = "hpfm:hr:employee-assign:";

    /**
     * 组织类型
     *
     * @author gaokuo.dai@hand-china.com 2018年6月26日下午3:14:53
     */
    public static class UnitType {
        private UnitType() {
        }

        /**
         * 集团
         */
        public static final String GROUP = "G";
        /**
         * 公司
         */
        public static final String COMPANY = "C";
        /**
         * 部门
         */
        public static final String DEPARTMENT = "D";
        /**
         * 岗位
         */
        public static final String POSITION = "P";

        /**
         * 上级组织tag值
         */
        public static final String TOP = "TOP";

        public static final String TOP_UNIT_CODE = "HPFM.HR.UNIT_TYPE";
    }

    /**
     * 基础异常编码<br/>
     * <i>待合并</i>
     */
    public static class ErrorCode {
        private ErrorCode() {
        }

        /**
         * 数据库异常：部门下包含多个主管岗位
         */
        public static final String ERROR_POSITION_MULTIPLE_SUPERVISOR_EXCEPTIONS = "error.hr.portion.multiple.supervisor.exceptions";
        /**
         * 父级别未启用
         */
        public static final String ERROR_UNIT_PARENT_DISABLED = "error.hr.unit.parent_disabled";
        /**
         * 数据库异常：同级部门只能有一个主管部门
         */
        public static final String ERROR_UNIT_MULTIPLE_SUPERVISOR_EXCEPTIONS = "error.hr.unit.multiple.supervisor.exceptions";
        /**
         * 错误: 顶层组织类型必须为公司或集团
         */
        public static final String ERROR_UNIT_TOP_TYPE_ERROR = "error.hr.unit.top_type_error";
        /**
         * 岗位信息异常：父级岗位未启用
         */
        public static final String ERROR_POSITION_NOT_ALLOWED = "error.position.not.allowed";
        /**
         * 岗位信息异常：父级岗位不能是他的子岗位
         */
        public static final String CHILD_CAN_NOT_BE_PARENT = "error.child.can.not.be.parent";

        public static final String GET_TOKEN = "error.get_token";
        /**
         * 员工不存在
         */
        public static final String EMPLOYEE_NOT_EXIST = "error.employee.not.exist";
        /**
         * 岗位不存在
         */
        public static final String POSITION_NOT_EXIST = "error.position.not.exist";
        /**
         * 子账户不存在或已关联员工
         */
        public static final String USER_NOT_EXISTS_OR_ALREADY_LINKED = "error.user_not_exists.or.already_linked";
        /**
         * 上级组织不存在
         */
        public static final String PARENT_UNIT_NOT_EXIST = "error.parent_unit.not.exist";
        /**
         * 部门没有上级组织
         */
        public static final String DEPARTMENT_MISSING_PARENT_UNIT = "error.department.missing.parent_unit";
        /**
         * 所属部门不存在或者组织类型非部门
         */
        public static final String DEPARTMENT_MISSING_OR_NOT_DEPARTMENT = "error.department.missing.or.not.department";
        /**
         * 上级岗位不存在
         */
        public static final String PARENT_POSITION_NOT_EXIST = "error.parent_position.not.exist";
        /**
         * 部门或所属公司未启用
         */
        public static final String DEPARTMENT_OR_COMPANY_NOT_ENABLED = "error.department.or.company.not.enabled";
    }

    public static final String AT = "@";

    public static final String SLASH = "/";

    /**
     * 组织编码
     */
    public static final String HPFM_UNIT = "HPFM.UNIT";

    /**
     * 岗位编码
     */
    public static final String HPFM_POSITION = "HPFM.POSITION";


    public static final String DEFAULT_UNIT_NAME = "默认组织";

    /**
     * 默认岗位排序
     */
    public static final Integer DEFAULT_ORDER_SEQ = 1;

    /**
     * 员工状态
     */
    public static class EmployeeStatus {
        /**
         * 在职
         */
        public static final String ON = "ON";
        /**
         * 试用
         */
        public static final String TRIAL = "TRIAL";
        /**
         * 实习
         */
        public static final String INTERNSHIP = "INTERNSHIP";
        /**
         * 离职
         */
        public static final String LEAVE = "LEAVE";
    }

    public static class SyncStatus {
        /**
         * 获取token失败
         */
        public static final String ERROR_GET_TOKEN = "Get Token Failed!";
        /**
         * 初始化同步service失败
         */
        public static final String ERROR_INIT_CORP_SERVICE = "CorpSyncService Init Failed!";
    }

    public static class Lock {

        /**
         * 同步到平台锁key
         */
        public static final String LOCK_KEY_TO_LOCAL = "hpfm:hr:sync:local";
        /**
         * 同步到外部key
         */
        public static final String LOCK_KEY_TO_THIRD = "hpfm:hr:sync:third";
        /**
         * 锁值
         */
        public static final String LOCK_VALUE = "LOCK";
        /**
         * 过期时间
         */
        public static final Long EXPIRE_TIME = 3*60*60L;
    }

    /**
     * 同步方向
     */
    public static class SyncDirection {

        /**
         * 平台到外部
         */
        public static final String P = "P";

        /**
         * 外部到平台
         */
        public static final String O = "O";
    }

    /**
     * 导入模板编码
     */
    public static class ImportTemplateCode {
        /**
         * 员工定义岗位分配导入
         */
        public static final String EMPLOYEE_ASSIGN_TEMP = "HPFM.EMPLOYEE_ASSIGN";
        /**
         * 员工定义用户分配导入
         */
        public static final String EMPLOYEE_USER_TEMP = "HPFM.EMPLOYEE_USER";
        /**
         * 部门和岗位导入
         */
        public static final String UNIT_POSITION_TEMP = "HPFM.UNIT_POSITION";
    }

    /**
     * 员工定义岗位分配导入
     */
    public static class EmployeeAssignImport {
        /**
         * 是否主岗
         */
        public static final String IS_PRIMARY_POSITION = "Y";

        public static final String NOT_PRIMARY_POSITION = "N";
    }
}
