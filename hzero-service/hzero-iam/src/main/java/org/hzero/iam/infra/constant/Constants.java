package org.hzero.iam.infra.constant;


import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;

/**
 * 平台常量
 *
 * @author bojiangzhou
 */
public interface Constants {

    /**
     * 国际冠码 值集编码
     */
    String IDD_LOV_CODE = "HPFM.IDD";
    /**
     * 默认集团
     */
    String HZERO_GROUP = "HZERO";

    String METADATA_CONTEXT = "CONTEXT";

    /**
     * SQL 更新最大数量
     */
    int BATCH_SIZE = 999;

    /**
     * 导入模板定义
     */
    interface ImportTemplateCode {
        /**
         * 批量创建的TEMPCODE
         */
        String CREATE_TEMP = "HIAM.ACCOUNT_CREATE";
        /**
         * 角色创建模板
         */
        String ROLE_TEMP = "HIAM.ROLE_CREATE";
        /**
         * 权限导入模板
         */
        String AUTH_TEMP = "HIAM.AUTH_CREATE";
    }

    interface HandlerCheckStatus{
        String HANDLE_STATUS_PROCESSED = "PROCESSED";
        String HANDLE_STATUS_UNTREATED = "UNTREATED";
        String HANDLE_STATUS_FAILED = "FAILED";
    }

    /**
     * 服务简称
     */
    String APP_CODE = HZeroService.Iam.CODE;

    /**
     * 二级域名缓存
     */
    String HIAM_DOMAIN = HZeroService.Oauth.CODE + ":domain";

    /**
     * admin用户
     */
    String SITE_ADMIN = HZeroConstant.ADMIN;

    /**
     * 全局层租户ID
     */
    Long SITE_TENANT_ID = HZeroConstant.SITE_TENANT_ID;

    /**
     * 各层分配层级最高层级值, 也即各层次角色起始层级
     */
    Long TOP_ASSIGN_LEVEL_VALUE = 0L;

    /**
     * 平台超级角色编码
     */
    String SITE_SUPER_ROLE_CODE = HZeroConstant.RoleCode.SITE;

    /**
     * 租户超级角色编码
     */
    String TENANT_SUPER_ROLE_CODE = HZeroConstant.RoleCode.TENANT;

    /**
     * 游客角色编码
     */
    String SITE_GUEST_ROLE_CODE = HZeroConstant.RoleCode.GUEST;

    /**
     * 游客角色编码(租户层)
     */
    String ORGANIZATION_GUEST_ROLE_CODE = "role/organization/default/guest";

    /**
     * 销售员角色模板
     *
     * @author mingwei.liu@hand-china.com
     * @since 2018/12/12
     */
    String ORGANIZATION_SALES_ROLE_TPL_CODE = "role/organization/default/template/sales";

    /**
     * 租户管理员模板
     *
     * @author mingwei.liu@hand-china.com
     * @since 2018/12/12
     */
    String ORGANIZATION_TENANT_ROLE_TPL_CODE = HZeroConstant.RoleCode.TENANT_TEMPLATE;

    /**
     * 专家角色模板
     *
     * @author gaokuo.dai@hand-china.com
     * @since 2019/01/26
     */
    String ORGANIZATION_EXPERTS_ROLE_TPL_CODE = "role/organization/default/template/experts";

    /**
     * 数字
     */
    char[] NUMBERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    String ADMIN_ROLE_PS = "%role.ps.create";

    /**
     * 角色成员类型
     */
    interface MemberType {
        /**
         * 用户
         */
        String USER = "user";
        /**
         * 客户端
         */
        String CLIENT = "client";
    }

    /**
     * 角色来源
     */
    interface RoleSource {
        /**
         * 默认
         */
        String DEFAULT = "default";
        /**
         * 自定义
         */
        String CUSTOM = "custom";
    }

    interface YesNoFlag {
        String YES = "Y";
        String NO = "N";
        String DELETE = "X";
    }

    /**
     * 缓存KEY
     */
    interface CacheKey {
        /**
         * 验证码间隔时间
         */
        String VALIDATE_INTERVAL = Constants.APP_CODE + ":captcha:interval";
        /**
         * 验证码
         */
        String VALIDATE_CAPTCHA = Constants.APP_CODE + ":captcha:code";
        /**
         * 前置校验验证结果
         */
        String RPECHECK_RESULT = Constants.APP_CODE + ":captcha:pre-validate";
        /**
         * 角色可分配权限集
         */
        String ROLE_ASSIGNABLE_PS = Constants.APP_CODE + ":role:assignable-ps";
        /**
         * 权限集角色标记缓存
         */
        String PS_ROLE_CHECK_FLAG = Constants.APP_CODE + ":ps-role:check-flag";
    }

    /**
     * 用户权限类型编码
     */
    interface AUTHORITY_TYPE_CODE {
        /**
         * 公司
         */
        String COMPANY = "COMPANY";
        /**
         * 业务实体
         */
        String OU = "OU";
        /**
         * 库存组织
         */
        String INVORG = "INV_ORGANIZATION";
        /**
         * 客户
         */
        String CUSTOMER = "CUSTOMER";
        /**
         * 供应商
         */
        String SUPPLIER = "SUPPLIER";
        /**
         * 采购组织
         */
        String PURORG = "PURCHASE_ORGANIZATION";
        /**
         * 采购员
         */
        String PURAGENT = "PURCHASE_AGENT";
        /**
         * 采购品类
         */
        String PURCAT = "PURCAT";
        /**
         * 采购物料
         */
        String PURITEM = "PURITEM";
        /**
         * 销售产品
         */
        String SALITEM = "SALITEM";
        /**
         * 值集
         */
        String LOV = "LOV";
        /**
         * 值集视图
         */
        String LOV_VIEW = "LOV_VIEW";
        /**
         * 数据源
         */
        String DATA_SOURCE = "DATASOURCE";
    }

    /**
     * 用户权限范围编码
     */
    interface AUTHORITY_SCOPE_CODE {
        String BIZ = "BIZ";
        String USER = "USER";
    }


    /**
     * 单据权限类型
     */
    interface DocType {
        Integer ACTION_TYPE_DELETE = 0;
        Integer ACTION_TYPE_ADD = 1;
        String DOC_TENANT_LEVEL = "TENANT";
        String DOC_SITE_LEVEL = "GLOBAL";

        String AUTH_TYPE_ALL = "ALL";
        /**
         * FIX20200421 新版单据权限默认权限控制范围编码
         */
        String NEW_AUTH_TYPE_ALL = "ALL_USER";
        String AUTH_TYPE_ONLY_CONFIG = "ONLY_CONFIG";

        String RULE_TYPE_COLUMN = "COLUMN";
        String RULE_TYPE_SUB_SELECT = "SUB_SELECT";

        /**
         * 使用当前用户所属租户做单据权限SQL中的租户Id
         */
        String DOC_USER_TENANT = "USER_TENANT";
        /**
         * 使用当前租户Id
         */
        String DOC_CUSTOM_TENANT = "CUSTOM_TENANT";
    }

    /**
     * 单据值来源类型
     */
    interface DocValueSourceType {
        /**
         * 值集
         */
        String LOV = "LOV";
        /**
         * 本地编码
         */
        String LOCAL = "LOCAL";
    }

    /**
     * 单据本地编码权限类型代码
     */
    interface DocLocalAuthorityTypeCode {
        /**
         * 本地编码：公司
         */
        String COMPANY = "COMPANY";
        /**
         * 本地编码：业务单元
         */
        String OU = "OU";
        /**
         * 本地编码：库存组织
         */
        String INVORG = "INV_ORGANIZATION";

        /**
         * 本地编码：采购组织
         */
        String PURORG = "PURCHASE_ORGANIZATION";
        /**
         * 本地编码：值集
         */
        String LOV = "LOV";
        /**
         * 本地编码：值集视图
         */
        String LOV_VIEW = "LOV_VIEW";
        /**
         * 本地编码：采购员
         */
        String PURAGENT = "PURCHASE_AGENT";
        /**
         * 本地编码：数据源
         */
        String DATA_SOURCE = "DATASOURCE";
        /**
         * 本地编码：数据组
         */
        String DATA_GROUP = "DATA_GROUP";
    }

    interface ErrorCode {
        /**
         * 传入实体不能为空
         */
        String ENTITY_NOT_EMPTY = "user.import.entity_not_empty";
        /**
         * LDAO不存在
         */
        String LDAP_NOT_EXIST_EXCEPTION = "hiam.warn.ldap.notFound";
        /**
         * 恢复调度任务失败
         */
        String LDAP_SYNC_RESUME_JOB_FAILED = "error.ldap.sync.resume.job.failed";
        /**
         * 暂停调度任务失败
         */
        String LDAP_SYNC_PAUSE_JOB_FAILED = "error.ldap.sync.pause.job.failed";
        /**
         * 创建调度任务失败
         */
        String LDAP_SYNC_CREATE_JOB_FAILED = "error.ldap.sync.create.job.failed";
        /**
         * 同步频率异常
         */
        String LDAP_SYNC_FREQUENCY_TYPE_WRONG = "error.ldap.sync.frequency.type.wrong";
        /**
         * 查询调度任务失败
         */
        String LDAP_SYNC_QUERY_JOB_FAILED = "error.ldap.sync.query.job.failed";
        /**
         * 更新调度任务失败
         */
        String LDAP_SYNC_UPDATE_JOB_FAILED = "error.ldap.sync.update.job.failed";
        /**
         * 查询执行器失败
         */
        String LDAP_SYNC_QUERY_EXECUTOR_FAILED = "error.ldap.sync.query.executor.failed";

        /**
         * 模板角色不存在
         */
        String TPL_ROLE_NOT_FOUND = "hiam.error.role.tplRoleNotFound";
    }

    interface AssignLevel {
        /**
         * 组织层
         */
        String ORG = "org";

        /**
         * 租户层
         */
        String ORGANIZATION = "organization";
    }


    /**
     * 配置常量
     */
    interface Config {
        String CONFIG_CODE_MENU_LAYOUT = "MENU_LAYOUT";
        String CONFIG_CODE_MENU_LAYOUT_THEME = "MENU_LAYOUT_THEME";
        String CONFIG_CODE_FAVICON = "FAVICON";
        String CONFIG_CODE_ROLE_MERGE = "ROLE_MERGE";
        String CONFIG_CODE_ROLE_DISABLE_INHERIT = "HIAM.ROLE_DISABLE_INHERIT";
        String TENANT_DEFAULT_LANGUAGE = "TENANT_DEFAULT_LANGUAGE";
        String CONFIG_CODE_WATERMARK = "WATERMARK";
    }


    /**
     * 租户初始化
     */
    String HIAM_TENANT_CREATE = "HIAM.TENANT.CREATE";
    String HIAM_TENANT_UPDATE = "HIAM.TENANT.UPDATE";

    /**
     * 编码规则代码
     *
     * @author gaokuo.dai@hand-china.com 2018年7月10日下午5:27:21
     */
    interface RuleCodes {
        /**
         * 公司编码
         */
        String HPFM_COMPANY = "HPFM.COMPANY";
        /**
         * 集团编码
         */
        String HPFM_GROUP = "HPFM.GROUP";
        /**
         * 租户编码
         */
        String HPFM_TENANT = "HPFM.TENANT";
    }

    /**
     * 编码规则应用层级
     */
    interface CodeRuleLevelCode {

        /**
         * 全局级
         */
        String GLOBAL = "GLOBAL";
    }

    /**
     * 应用维度
     */
    interface Level {
        /**
         * 应用维度-租户级
         */
        String TENANT = "T";
    }

    /**
     * 字段权限维度
     */
    interface FieldPermissionDimension {
        /**
         * 用户维度
         */
        String USER = "USER";
        /**
         * 角色维度
         */
        String ROLE = "ROLE";
    }

    /**
     * 角色来源
     */
    interface SecGrpSource {
        /**
         * 当前角色
         */
        String SELF = "self";
        /**
         * 父级角色
         */
        String PARENT = "parent";
        /**
         * 子级角色
         */
        String CHILDREN = "children";
    }

    /**
     * 路由信息缓存前坠
     */
    String ADMIN_ROUTE_INFO_CACHE_PREFIX = "hadm:routes";

    interface ClearPermissionCheck {
        String CODE = "HIAM.PERMISSION_CHECK.CLEAR_TYPE";
        String THREE_DAY = "1";
        String ONE_WEEK = "2";
        String ONE_MONTH = "3";
        String THREE_MONTH = "4";
        String SIX_MONTH = "5";
        String ONE_YEAR = "6";
    }

    interface CheckState {
        String PERMISSION_MISMATCH = "PERMISSION_MISMATCH";
        String PERMISSION_NOT_PASS = "PERMISSION_NOT_PASS";
    }

    /**
     * 安全组分配相关常量
     */
    interface SecGrpAssign {

        String USER_DIMENSION = "USER";
        String ROLE_DIMENSION = "ROLE";
        String DEFAULT_DATA_SOURCE = "DEFAULT";
        String SEC_GRP_DATA_SOURCE = "SEC_GRP";
        String DEFAULT_SEC_GRP_DATA_SOURCE = "DEFAULT,SEC_GRP";

    }

    interface Authority {
        Integer NOT_INCLUDE = 0;
        Integer INCLUDE = 1;
    }

    /**
     * 显示状态
     */
    interface DisplayStatus {
        Long DISABLED = 0L;
        Long ENABLE = 1L;
    }

    /**
     * 安全组数据分配类型
     */
    interface SecGrpAssignTypeCode {
        /**
         * 自己创建
         */
        String SELF = "SELF";

        /**
         * 父类分配
         */
        String PARENT = "PARENT";

        /**
         * 自己创建之后，父类也创建
         */
        String SELF_PARENT = "SELF_PARENT";
    }

    /**
     * 安全组数据权限范围唯一键模板
     * docId - authScopeCode
     */
    String DIM_UNIQUE_KEY_TEMPLATE = "%s-%s";

    /**
     * 权限编码
     */
    interface PermissionTypeCode {
        String API = "API";
        String LOV = "LOV";
    }

    /**
     * CORN表达式格式
     */
    String CRON_FORMAT = "%s %s %s %s %s %s";

    /**
     * LDAP定时同步调度任务编码
     */
    interface LdapSyncJobCode {
        String LDAP_SYNC = "HIAM.LDAP_SYNC";
        String LDAP_SYNC_LEAVE = "HIAM.LDAP_SYNC_LEAVE";
    }

    /**
     * LDAP定时同步调度任务描述
     */
    interface LdapSyncJobDescription {
        String LDAP_SYNC = "LDAP用户同步";
        String LDAP_SYNC_LEAVE = "LDAP离职用户同步";
    }

    /**
     * LDAP定时同步调度任务JobHandler
     */
    interface LdapSyncJobHandler {
        String LDAP_SYNC = "ldapSyncJob";
        String LDAP_SYNC_LEAVE = "ldapLeaveSyncJob";
    }

    /**
     * LDAP定时同步调度任务参数
     */
    interface LdapSyncJobParameters {
        String EXECUTOR_STRATEGY = "POLLING";
        String FAIL_STRATEGY = "IGNORE";
        String GLUE_TYPE = "SIMPLE";
    }

    /**
     * 定时调度任务状态
     */
    interface LdapSyncJobStatus {
        /**
         * 正常
         */
        String NORMAL = "NORMAL";
        /**
         * 暂停
         */
        String PAUSED = "PAUSED";
        /**
         * 结束
         */
        String NONE = "NONE";
        /**
         * 错误
         */
        String ERROR = "ERROR";
        /**
         * 完成
         */
        String COMPLETE = "COMPLETE";
        /**
         * 阻塞
         */
        String BLOCKED = "BLOCKED";
    }

    /**
     * Ldap同步记录类型
     */
    interface LdapHistorySyncType {

        /**
         * 自动同步
         */
        String A = "A";

        /**
         * 手动同步
         */
        String M = "M";

    }


    /**
     * 值集类型
     */
    interface LovType {

        /**
         * URL
         */
        String URL = "URL";

        /**
         * SQL
         */
        String SQL = "SQL";

    }
}
