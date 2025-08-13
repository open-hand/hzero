package org.hzero.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * <p>
 * Swagger Api 描述配置
 * </p>
 *
 * @author qingsheng.chen 2018/7/30 星期一 14:26
 */
@Configuration
public class PlatformSwaggerApiConfig {
    /**
     * 国家地区相关
     */
    public static final String COUNTRY = "Country";
    public static final String COUNTRY_SITE = "Country(Site Level)";
    public static final String REGION = "Country Region";
    public static final String REGION_SITE = "Country Region(Site Level)";
    /**
     * 登录审计
     */
    public static final String AUDIT_LOGIN = "Audit Login";
    public static final String AUDIT_LOGIN_SITE = "Audit Login(Site Level)";
    public static final String CODE_RULE = "Code Rule";
    public static final String CODE_RULE_SITE = "Code Rule(Site Level)";
    public static final String CODE_RULE_MANAGE = "Code Rule Manager";
    public static final String CODE_RULE_MANAGE_SITE = "Code Rule Manager(Site Level)";
    public static final String COMPANY = "Company";
    public static final String COMPANY_SITE = "Company(Site Level)";
    public static final String CONFIG = "Config";
    public static final String CONFIG_SITE = "Config(Site Level)";
    public static final String CONFIG_MANAGE = "Config Manager";
    public static final String CONFIG_MANAGE_SITE = "Config Manager(Site Level)";
    public static final String CUSTOM_TABLE = "Custom Table";
    public static final String COMMON = "Common";

    /**
     * 平台卡片
     */
    public static final String DASHBOARD_LAYOUT_CONFIG = "Dashboard Layout Config";
    public static final String DASHBOARD_CARD = "Dashboard Card";
    public static final String DASHBOARD_CARD_SITE = "Dashboard Card(Site Level)";
    public static final String DASHBOARD_ROLE_CARD = "Dashboard Role Card";
    public static final String DASHBOARD_ROLE_CARD_SITE = "Dashboard Role Card(Site Level)";
    public static final String DASHBOARD_TENANT_CARD = "Dashboard Tenant Card";
    public static final String DASHBOARD_TENANT_CARD_SITE = "Dashboard Tenant Card(Site Level)";

    /**
     * 工作条目配置
     */
    public static final String DASHBOARD_CLAUSE = "Dashboard Clause";
    public static final String DASHBOARD_CLAUSE_SITE = "Dashboard Clause(Site Level)";
    public static final String DASHBOARD_CLAUSE_ASSIGN = "Dashboard Clause Assign";
    public static final String DASHBOARD_CLAUSE_ASSIGN_SITE = "Dashboard Clause Assign(Site Level)";

    /**
     * 数据权限数据源关系
     */
    public static final String DATABASE = "Database";
    public static final String DATABASE_SITE = "Database(Site Level)";
    public static final String DATABASE_TENANT = "Database Tenant";
    public static final String DATABASE_TENANT_SITE = "Database Tenant(Site Level)";
    public static final String DATASOURCE = "Datasource";
    public static final String DATASOURCE_SITE = "Datasource(Site Level)";
    public static final String DATASOURCE_SERVICE = "Datasource Service";
    public static final String DATASOURCE_SERVICE_SITE = "Datasource Service(Site Level)";
    public static final String DATASOURCE_DRIVER = "Datasource Driver";
    public static final String DATASOURCE_DRIVER_SITE = "Datasource Driver(Site Level)";

    public static final String EVENT = "Event";
    public static final String EVENT_SITE = "Event(Site Level)";

    public static final String GROUP = "Group";
    /**
     * 语言
     */
    public static final String LANGUAGE = "Language";
    public static final String LANGUAGE_SITE = "Language(Site Level)";

    public static final String LOV = "Lov";
    public static final String LOV_PUB = "Lov Public";
    public static final String LOV_MANAGE = "Lov Manage";
    public static final String LOV_MANAGE_SITE = "Lov Manage(Site Level)";
    public static final String LOV_VIEW = "Lov View";
    public static final String LOV_VIEW_MANAGE = "Lov View Manage";
    public static final String LOV_VIEW_MANAGE_SITE = "Lov View Manage(Site Level)";

    public static final String RESPONSE_MESSAGE = "Response Message";
    public static final String RESPONSE_MESSAGE_SITE = "Response Message(Site Level)";
    /**
     * 多语言
     */
    public static final String MULTI_LANGUAGE = "Multi-Language";
    public static final String MULTI_LANGUAGE_SITE = "Multi-Language(Site Level)";

    public static final String PERMISSION_RANGE = "Permission Range";
    public static final String PERMISSION_RANGE_SITE = "Permission Range(Site Level)";
    public static final String PERMISSION_RULE = "Permission Rule";
    public static final String PERMISSION_RULE_SITE = "Permission Rule(Site Level)";
    public static final String PERMISSION_REL = "Permission Rel";
    public static final String PERMISSION_REL_SITE = "Permission Rel(Site Level)";
    public static final String PROFILE = "Profile";
    public static final String PROFILE_SITE = "Profile(Site Level)";
    public static final String PROFILE_MANAGE = "Profile Manager";
    public static final String PROFILE_MANAGE_SITE = "Profile Manager(Site Level)";

    public static final String PROMPT = "Prompt";
    public static final String PROMPT_SITE = "Prompt(Site Level)";
    public static final String PROMPT_MANAGE = "Prompt Manager";
    public static final String PROMPT_MANAGE_V2 = "Prompt Manager V2";
    public static final String PROMPT_MANAGE_SITE = "Prompt Manager(Site Level)";
    public static final String PROMPT_MANAGE_SITE_V2 = "Prompt Manager V2(Site Level)";
    /**
     * 规则引擎相关
     */
    public static final String RULE_SCRIPT_CONFIG = "Rule script config";
    public static final String RULE_SCRIPT_CONFIG_SITE = "Rule script config(Site Level)";

    public static final String STATIC_TEXT = "Static Text";
    public static final String STATIC_TEXT_SITE = "Static Text(Site Level)";

    public static final String TENANT = "Tenant";
    public static final String TENANT_SITE = "Tenant(Site Level)";
    /**
     * 自定义个性化页面
     */
    public static final String UI_PAGE = "Ui Page";
    public static final String UI_PAGE_MANAGER = "Ui Page Manager";
    public static final String UI_PAGE_MANAGER_SITE = "Ui Page Manager(Site Level)";

    public static final String ICON = "Icon";
    public static final String ICON_SITE = "Icon(Site Level)";
    public static final String DASHBOARD_CARD_CLAUSE = "Dashboard Card Clause";
    public static final String DASHBOARD_CARD_CLAUSE_SITE = "Dashboard Card Clause(Site Level)";


    public static final String CONTENT_TEMPLATE = "Content Template";
    public static final String CONTENT_TEMPLATE_SITE = "Content Template(Site Level)";
    public static final String TEMPLATE_CONFIG = "Template Config";
    public static final String TEMPLATE_CONFIG_SITE = "Template Config(Site Level)";
    public static final String TEMPLATE_ASSIGN = "Template Assign";
    public static final String TEMPLATE_ASSIGN_SITE = "Template Assign(Site Level)";

    /**
     * 服务器管理
     */
    public static final String SERVER = "Server";
    public static final String SERVER_SITE = "Server(Site Level)";
    public static final String SERVER_CLUSTER = "Server Cluster";
    public static final String SERVER_CLUSTER_SITE = "Server Cluster(Site Level)";
    public static final String SERVER_ASSIGN = "Server Assign";
    public static final String SERVER_ASSIGN_SITE = "Server Assign(Site Level)";

    /**
     * 表单配置相关
     */
    public static final String FORM_HEADER = "Form Header";
    public static final String FORM_LINE = "Form Line";
    public static final String FORM_HEADER_SITE = "Form Header(Site Level)";
    public static final String FORM_LINE_SITE = "Form Line(Site Level)";

    /**
     * 数据组相关
     */
    public static final String DATA_GROUP = "Data Group";
    public static final String DATA_GROUP_SITE = "Data Group(Site Level)";
    public static final String DATA_GROUP_LINE = "Data Group Line";
    public static final String DATA_GROUP_LINE_SITE = "Data Group Line(Site Level)";
    public static final String DATA_GROUP_LINE_DTL = "Data Group Dtl";
    public static final String DATA_GROUP_LINE_DTL_SITE = "Data Group Dtl(Site Level)";
    public static final String DATA_GROUP_DIMENSION = "Data Group Dimension";
    public static final String DATA_GROUP_DIMENSION_SITE = "Data Group Dimension(Site Level)";

    /**
     * 异步导出
     */
    public static final String EXPORT_TASK = "Export Task";

    /**
     * 数据层级
     */
    public static final String DATA_HIERARCHY = "Data Hierarchy";
    public static final String DATA_HIERARCHY_SITE = "Data Hierarchy(Site Level)";
    public static final String DATA_HIERARCHY_SWITCH = "Data Hierarchy Switch";

    /**
     * CA证书
     */
    public static final String CERTIFICATE = "Certificate";
    public static final String CERTIFICATE_SITE = "Certificate(Site Level)";

    /**
     * 在线用户
     */
    public static final String ONLINE_USER = "Online User";
    public static final String ONLINE_USER_SITE = "Online User(Site Level)";


    public static final String TOOL_CACHE = "Tool Cache";
    public static final String TOOL_PASSWORD = "Tool Password";

    /**
     * 通用模板
     */
    public static final String COMMON_TEMPLATE = "Common Template";
    public static final String COMMON_TEMPLATE_SITE = "Common Template(Site Level)";

    @Autowired
    public PlatformSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(COUNTRY, "国家信息管理"),
                new Tag(COUNTRY_SITE, "国家信息管理(平台级)"),
                new Tag(REGION, "地区信息管理"),
                new Tag(REGION_SITE, "地区信息管理(平台级)"),

                new Tag(SERVER, "服务器"),
                new Tag(SERVER_SITE, "服务器(平台级)"),
                new Tag(SERVER_CLUSTER, "服务器集群"),
                new Tag(SERVER_CLUSTER_SITE, "服务器集群(平台级)"),
                new Tag(SERVER_ASSIGN, "服务器关联集群"),
                new Tag(SERVER_ASSIGN_SITE, "服务器关联集群(平台级)"),
                new Tag(AUDIT_LOGIN, "登录审计"),
                new Tag(AUDIT_LOGIN_SITE, "登录审计(平台级)"),
                new Tag(CODE_RULE, "编码规则"),
                new Tag(CODE_RULE_SITE, "编码规则(平台级)"),
                new Tag(CODE_RULE_MANAGE, "编码规则管理"),
                new Tag(CODE_RULE_MANAGE_SITE, "编码规则管理(平台级)"),
                new Tag(COMPANY, "公司信息"),
                new Tag(COMPANY_SITE, "公司信息(平台级)"),
                new Tag(CONFIG, "系统配置"),
                new Tag(CONFIG_SITE, "系统配置(平台级)"),
                new Tag(CONFIG_MANAGE, "系统配置管理"),
                new Tag(CONFIG_MANAGE_SITE, "系统配置管理(平台级)"),
                new Tag(CUSTOM_TABLE, "个性化表格配置"),
                new Tag(DASHBOARD_LAYOUT_CONFIG, "工作台布局配置"),
                new Tag(DASHBOARD_CARD, "工作台卡片管理"),
                new Tag(DASHBOARD_ROLE_CARD, "工作台角色卡片分配"),
                new Tag(DASHBOARD_TENANT_CARD, "工作台租户卡片分配"),
                new Tag(DASHBOARD_TENANT_CARD_SITE, "工作台租户卡片分配(平台级)"),
                new Tag(DASHBOARD_CARD_CLAUSE, "工作台条目卡片分配"),
                new Tag(DASHBOARD_CARD_CLAUSE_SITE, "工作台条目卡片分配(平台级)"),
                new Tag(DASHBOARD_ROLE_CARD_SITE, "工作台角色卡片分配(平台级)"),
                new Tag(DASHBOARD_CARD_SITE, "工作台卡片管理(平台级)"),


                new Tag(DATABASE, "数据库"),
                new Tag(DATABASE_SITE, "数据库(平台级)"),
                new Tag(DATABASE_TENANT, "数据权限数据源关系"),
                new Tag(DATABASE_TENANT_SITE, "数据权限数据源关系(平台级)"),
                new Tag(DATASOURCE, "数据源"),
                new Tag(DATASOURCE_SITE, "数据源(平台级)"),
                new Tag(DATASOURCE_SERVICE, "数据源服务关系"),
                new Tag(DATASOURCE_SERVICE_SITE, "数据源服务关系(平台级)"),

                new Tag(EVENT, "事件管理"),
                new Tag(EVENT_SITE, "事件管理(平台级)"),

                new Tag(GROUP, "集团信息"),

                new Tag(LANGUAGE, "语言"),
                new Tag(LANGUAGE_SITE, "语言(平台级)"),

                new Tag(LOV, "值集"),
                new Tag(LOV_PUB, "值集（公开API）"),
                new Tag(LOV_MANAGE, "值集管理"),
                new Tag(LOV_MANAGE_SITE, "值集管理(平台级)"),
                new Tag(LOV_VIEW, "值集视图"),
                new Tag(LOV_VIEW_MANAGE, "值集视图管理"),
                new Tag(LOV_VIEW_MANAGE_SITE, "值集视图管理(平台级)"),

                new Tag(RESPONSE_MESSAGE, "后端消息返回"),
                new Tag(RESPONSE_MESSAGE_SITE, "后端消息返回(平台级)"),

                new Tag(MULTI_LANGUAGE, "多语言"),
                new Tag(MULTI_LANGUAGE_SITE, "多语言(平台级)"),

                new Tag(PERMISSION_RANGE, "屏蔽范围管理"),
                new Tag(PERMISSION_RANGE_SITE, "屏蔽范围管理(平台级)"),
                new Tag(PERMISSION_RULE, "屏蔽规则管理"),
                new Tag(PERMISSION_RULE_SITE, "屏蔽规则管理(平台级)"),
                new Tag(PERMISSION_REL, "屏蔽范围规则关系管理"),
                new Tag(PERMISSION_REL_SITE, "屏蔽范围规则关系管理(平台级)"),

                new Tag(PROFILE, "配置"),
                new Tag(PROFILE_SITE, "配置(平台级)"),
                new Tag(PROFILE_MANAGE, "配置管理"),
                new Tag(PROFILE_MANAGE_SITE, "配置管理(平台级)"),

                new Tag(PROMPT, "多语言描述"),
                new Tag(PROMPT_SITE, "多语言描述(平台级)"),
                new Tag(PROMPT_MANAGE, "多语言描述管理"),
                new Tag(PROMPT_MANAGE_V2, "多语言描述管理V2"),
                new Tag(PROMPT_MANAGE_SITE, "多语言描述管理(平台级)"),
                new Tag(PROMPT_MANAGE_SITE_V2, "多语言描述管理V2(平台级)"),

                new Tag(RULE_SCRIPT_CONFIG, "规则引擎脚本代码维护"),
                new Tag(RULE_SCRIPT_CONFIG_SITE, "规则引擎脚本代码维护(平台级)"),
                new Tag(STATIC_TEXT, "静态信息管理"),
                new Tag(STATIC_TEXT_SITE, "静态信息管理(平台级)"),
                new Tag(TENANT, "租户管理"),
                new Tag(TENANT_SITE, "租户管理(平台级)"),

                new Tag(UI_PAGE, "个性化页面应用"),
                new Tag(UI_PAGE_MANAGER, "个性化页面管理维护"),
                new Tag(UI_PAGE_MANAGER_SITE, "个性化页面管理维护(平台级)"),
                new Tag(DASHBOARD_CLAUSE, "工作台条目配置"),
                new Tag(DASHBOARD_CLAUSE_SITE, "工作台条目配置(平台级)"),
                new Tag(DASHBOARD_CLAUSE_ASSIGN, "工作台条目分配租户"),
                new Tag(DASHBOARD_CLAUSE_ASSIGN_SITE, "工作台条目分配租户(平台级)"),
                new Tag(ICON, "图标"),
                new Tag(ICON_SITE, "图标(平台级)"),
                new Tag(COMMON, "通用功能接口"),


                new Tag(CONTENT_TEMPLATE, "内容模板维护"),
                new Tag(CONTENT_TEMPLATE_SITE, "通用模板维护(平台级)"),
                new Tag(TEMPLATE_CONFIG, "模板配置"),
                new Tag(TEMPLATE_CONFIG_SITE, "模板配置(平台级)"),
                new Tag(TEMPLATE_ASSIGN, "分配模板"),
                new Tag(TEMPLATE_ASSIGN_SITE, "分配模板(平台级)"),


                new Tag(FORM_HEADER, "表单头管理"),
                new Tag(FORM_HEADER_SITE, "表单头管理(平台级)"),
                new Tag(FORM_LINE, "表单行管理"),
                new Tag(FORM_LINE_SITE, "表单行管理(平台级)"),

                new Tag(DATA_GROUP, "数据组"),
                new Tag(DATA_GROUP_SITE, "数据组(平台级)"),
                new Tag(DATA_GROUP_LINE, "数据组行"),
                new Tag(DATA_GROUP_LINE_SITE, "数据组行(平台级)"),
                new Tag(DATA_GROUP_LINE_DTL, "数据组行明细"),
                new Tag(DATA_GROUP_LINE_DTL_SITE, "数据组行明细(平台级)"),
                new Tag(DATA_GROUP_DIMENSION_SITE, "数据组维度(平台级)"),
                new Tag(DATA_GROUP_DIMENSION, "数据组维度"),

                new Tag(EXPORT_TASK, "导出任务"),

                new Tag(DATA_HIERARCHY, "数据层级配置"),
                new Tag(DATA_HIERARCHY_SITE, "数据层级配置(平台级)"),
                new Tag(DATASOURCE_DRIVER, "数据源驱动"),
                new Tag(DATASOURCE_DRIVER_SITE, "数据源驱动(平台级)"),
                new Tag(DATA_HIERARCHY_SWITCH, "数据层级切换"),
                new Tag(CERTIFICATE, "CA证书配置"),
                new Tag(CERTIFICATE_SITE, "CA证书配置(平台级)"),

                new Tag(ONLINE_USER, "在线用户"),
                new Tag(ONLINE_USER_SITE, "在线用户(平台级)"),

                new Tag(TOOL_CACHE, "刷新缓存工具接口"),
                new Tag(TOOL_PASSWORD, "密码加密解密相关工具"),

                new Tag(COMMON_TEMPLATE, "通用模板"),
                new Tag(COMMON_TEMPLATE_SITE, "通用模板(平台级)")
        );
    }
}
