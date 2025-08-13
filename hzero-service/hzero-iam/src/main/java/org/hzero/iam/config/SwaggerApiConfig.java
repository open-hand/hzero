package org.hzero.iam.config;

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
public class SwaggerApiConfig {
    public static final String DOMAIN_SITE = "Hiam Domain(Site Level)";
    public static final String DOMAIN = "Hiam Domain";
    public static final String USER_SITE = "Hiam User(Site Level)";
    public static final String USER = "Hiam User";
    public static final String USER_SELF = "Hiam User Self";
    public static final String USER_PUBLIC = "Hiam User Public";
    public static final String ROLE_SITE = "Hiam Role(Site Level)";
    public static final String ROLE = "Hiam Role";
    public static final String ROLE_V2 = "Hiam Role V2";
    public static final String HR_UNIT = "HR Unit";
    public static final String ROLE_PERMISSION_SITE = "Hiam Role Permission(Site Level)";
    public static final String ROLE_PERMISSION = "Hiam Role Permission";
    public static final String PERMISSION = "Hiam Permission";
    public static final String PERMISSION_SITE = "Hiam Permission(Site Level)";
    public static final String API_MANAGE_SITE = "Hiam Api Manage(Site Level)";
    public static final String API_MANAGE = "Hiam Api Manage";
    public static final String PERMISSION_REFRESH_SITE = "Permission Refresh(Site Level)";
    public static final String PERMISSION_REFRESH = "Permission Refresh";
    public static final String MENU_SITE = "Hiam Menu(Site Level)";
    public static final String TENANT_MENU_SITE = "Hiam Tenant Menu";
    public static final String MENU = "Hiam Menu";
    public static final String MEMBER_ROLE_SITE = "Hiam Member Role(Site Level)";
    public static final String MEMBER_ROLE = "Hiam Member Role";
    public static final String USER_AUTHORITY = "User Authority";
    public static final String ROLE_DATA_AUTHORITY = "Role Data Authority";
    public static final String USER_AUTHORITY_SITE = "User Authority(Site Level)";
    public static final String DOC_TYPE_SITE = "Doc Type(Site Level)";
    public static final String DOC_TYPE = "Doc Type";
    public static final String MEMBER_ROLE_EXTERNAL = "Member Role External";
    public static final String PERMISSION_SET = "Permission Set";
    public static final String USER_DETAILS_SITE = "User Details(Site Level)";
    public static final String USER_CONFIG_SITE = "User Config(Site Level)";
    public static final String USER_CONFIG = "User Config";
    public static final String OPEN_APP = "Open App";
    public static final String OPEN_APP_SITE = "Open App(Site Level)";
    public static final String USER_OPEN_ACCOUNT = "User Open Account";
    public static final String ACCOUNT_IMPORT= "Account Import";
    public static final String BATCH_DATA_INIT = "Batch Data Init";
    public static final String BATCH_DATA_INIT_SITE = "Batch Data Init(Site Level)";
    public static final String DATA_INIT = "Data Init";
    public static final String USER_GROUP_SITE = "User Group(Site Level)";
    public static final String USER_GROUP = "User Group";
    public static final String USER_GROUP_ASSIGN_SITE = "User Group Assign(Site Level)";
    public static final String USER_GROUP_ASSIGN = "User Group Assign";
    public static final String ROLE_AUTHORITY = "Role Authority";
    public static final String ROLE_AUTHORITY_SITE = "Role Authority(Site Level)";
    public static final String USER_DETAILS = "User Details";
    public static final String CLIENT_DETAILS = "Client Details";
    public static final String CLIENT_DETAILS_SITE = "Client Details(Site Level)";
    public static final String FIELD_PERMISSION = "Field Permission";
    public static final String FIELD_PERMISSION_SITE = "Field Permission(Site Level)";

    public static final String CLIENT = "Client";
    public static final String CLIENT_SITE = "Client(Site)";
    public static final String LDAP = "Ldap";
    public static final String PASSWORD_POLICY = "PasswordPolicy";
    public static final String LOW_CODE_MODE_PUBLISH = "Low Code Mode Publish";
    public static final String DOC_TYPE_DIMENSION = "DocTypeDimension";
    public static final String DOC_TYPE_DIMENSION_SITE = "DocTypeDimension(Site Level)";

    public static final String SEC_GRP_ROLE_ASSIGN = "Sec Grp Role Assign";
    public static final String SEC_GRP_ROLE_ASSIGN_SITE = "Sec Grp Role Assign(Site Level)";
    public static final String SEC_GRP_USER_ASSIGN = "Sec Grp User Assign";
    public static final String SEC_GRP_USER_ASSIGN_SITE = "Sec Grp User Assign(Site Level)";
    public static final String SEC_GRP= "Sec Grp";
    public static final String SEC_GRP_SITE = "Sec Grp(Site Level)";
    public static final String SEC_GRP_ACL= "Sec Grp Acl";
    public static final String SEC_GRP_ACL_SITE = "Sec Grp Acl(Site Level)";
    public static final String SEC_GRP_DCL= "Sec Grp Dcl";
    public static final String SEC_GRP_DCL_SITE = "Sec Grp Dcl(Site Level)";
    public static final String SEC_GRP_DCL_LINE= "Sec Grp Dcl Line";
    public static final String SEC_GRP_DCL_LINE_SITE = "Sec Grp Dcl Line(Site Level)";
    public static final String SEC_GRP_DCL_DIM= "Sec Grp Dim";
    public static final String SEC_GRP_DCL_DIM_SITE = "Sec Grp Dim(Site Level)";
    public static final String SEC_GRP_ACL_FIELD= "Sec Grp Field";
    public static final String SEC_GRP_ACL_FIELD_SITE = "Sec Grp Field(Site Level)";
    public static final String SEC_GRP_ACL_DASHBOARD= "Sec Grp Dashboard";
    public static final String SEC_GRP_ACL_DASHBOARD_SITE = "Sec Grp Dashboard(Site Level)";


    public static final String ACCESS_AUTH_MANAGER="Access Auth Manager";
    public static final String ACCESS_AUTH_MANAGER_SITE="Access Auth Manager(Site Level)";
    public static final String DATA_AUTH_MANAGER = "Data Auth Manager";
    public static final String DATA_AUTH_MANAGER_SITE = "Data Auth Manager(Site Level)";
    public static final String TOKEN_MANAGER = "Token Manager";
    public static final String TOKEN_MANAGER_SITE = "Token Manager(Site Level)";
    public static final String TENANT_CUSTOM_POINT = "Tenant Custom Point(Site Level)";

    public static final String TOOL_CACHE = "Tool Cache";
    public static final String TOOL_DATA_FIX = "Tool Data Fix";
    public static final String TOOL_PERMISSION = "Tool Permission";
    public static final String PERMISSION_CHECK = "Permission Check";
    public static final String PERMISSION_CHECK_SITE = "Permission Check(Site Level)";
    public static final String LABEL_SITE = "Label Manager(Site Level)";
    public static final String LABEL = "Label Manager";
    public static final String ROLE_LABEL_SITE = "Role Label Manager(Site Level)";
    public static final String ROLE_LABEL = "Role Label Manager";
    public static final String USER_DBD_MENU = "User Dashboad Menu";
    public static final String LABEL_RELATION = "Label Relation";
    public static final String LABEL_RELATION_SITE = "Label Relation(Site Level)";
    public static final String IAM_TENANT = "Iam Tenant";
    public static final String IAM_TENANT_SITE = "Iam Tenant(Site Level)";
    public static final String DOMAIN_ASSIGN = "Domain Assign";
    public static final String DOMAIN_ASSIGN_SITE = "Domain Assign(Site Level)";

    @Autowired
    public SwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(DOMAIN_SITE, "二级域名管理(平台级)"),
                new Tag(DOMAIN, "二级域名管理"),
                new Tag(USER_SITE, "用户管理(平台级)"),
                new Tag(USER, "用户管理"),
                new Tag(USER_SELF, "登录用户接口 "),
                new Tag(USER_PUBLIC, "用户公开接口 "),
                new Tag(ROLE_SITE, "角色管理(平台级)"),
                new Tag(ROLE, "角色管理"),
                new Tag(ROLE_V2, "角色管理V2版本"),
                new Tag(HR_UNIT, "HR 组织架构"),
                new Tag(ROLE_PERMISSION_SITE, "角色权限管理(平台级)"),
                new Tag(ROLE_PERMISSION, "角色权限管理"),
                new Tag(PERMISSION, "权限管理"),
                new Tag(PERMISSION_SITE, "权限管理(平台级)"),
                new Tag(API_MANAGE, "Api管理"),
                new Tag(API_MANAGE_SITE, "Api管理(平台级)"),
                new Tag(PERMISSION_REFRESH_SITE, "权限刷新(平台级)"),
                new Tag(PERMISSION_REFRESH, "权限刷新"),
                new Tag(MENU_SITE, "菜单管理(平台级)"),
                new Tag(TENANT_MENU_SITE, "租户菜单管理"),
                new Tag(MENU, "菜单管理"),
                new Tag(MEMBER_ROLE_SITE, "成员角色管理(平台级)"),
                new Tag(MEMBER_ROLE, "成员角色管理"),
                new Tag(USER_AUTHORITY, "用户权限管理"),
                new Tag(USER_AUTHORITY_SITE, "用户权限管理(平台级)"),
                new Tag(DOC_TYPE_SITE, "单据权限类型定义(平台级)"),
                new Tag(ROLE_DATA_AUTHORITY, "角色单据权限管理"),
                new Tag(DOC_TYPE, "单据权限类型定义"),
                new Tag(MEMBER_ROLE_EXTERNAL, "成员角色外部服务辅助管理"),
                new Tag(PERMISSION_SET, "权限集(平台级)"),
                new Tag(USER_DETAILS_SITE, "当前用户详细信息管理(平台级)"),
                new Tag(USER_DETAILS, "当前用户详细信息管理"),
                new Tag(CLIENT_DETAILS, "当前客户端详细信息管理"),
                new Tag(CLIENT_DETAILS_SITE, "当前客户端详细信息管理(平台级)"),
                new Tag(USER_CONFIG_SITE, "当前用户默认配置(平台级)"),
                new Tag(USER_CONFIG, "当前用户默认配置"),
                new Tag(OPEN_APP, "三方网站管理"),
                new Tag(OPEN_APP_SITE, "三方网站管理(平台级)"),
                new Tag(USER_OPEN_ACCOUNT, "用户第三方账号管理"),
                new Tag(ACCOUNT_IMPORT,"子账户导入"),
                new Tag(BATCH_DATA_INIT, "批量数据初始化"),
                new Tag(BATCH_DATA_INIT_SITE, "批量数据初始化(平台级)"),
                new Tag(DATA_INIT, "数据初始化"),
                new Tag(USER_GROUP_SITE, "用户组管理(平台级)"),
                new Tag(USER_GROUP, "用户组管理"),
                new Tag(USER_GROUP_ASSIGN_SITE, "用户组分配(平台级)"),
                new Tag(USER_GROUP_ASSIGN, "用户组分配"),
                new Tag(ROLE_AUTHORITY, "角色数据权限定义"),
                new Tag(ROLE_AUTHORITY_SITE, "角色数据权限定义(平台级)"),
                new Tag(FIELD_PERMISSION, "字段权限"),
                new Tag(FIELD_PERMISSION_SITE, "字段权限(平台级)"),
                new Tag(CLIENT, "客户端"),
                new Tag(CLIENT_SITE, "客户端(平台级)"),
                new Tag(LDAP, "LDAP"),
                new Tag(PASSWORD_POLICY, "密码策略"),
                new Tag(LOW_CODE_MODE_PUBLISH, "Low Code 模型发布"),
                new Tag(DOC_TYPE_DIMENSION, "单据类型维度"),
                new Tag(DOC_TYPE_DIMENSION_SITE, "单据类型维度(平台级)"),


                new Tag(SEC_GRP_ROLE_ASSIGN, "角色安全组分配"),
                new Tag(SEC_GRP_ROLE_ASSIGN_SITE, "角色安全组分配(平台级)"),
                new Tag(SEC_GRP_USER_ASSIGN, "用户安全组分配"),
                new Tag(SEC_GRP_USER_ASSIGN_SITE, "用户安全组分配(平台级)"),
                new Tag(SEC_GRP_ROLE_ASSIGN, "安全组分配"),
                new Tag(SEC_GRP_ROLE_ASSIGN, "安全组分配"),
                new Tag(SEC_GRP, "安全组"),
                new Tag(SEC_GRP_SITE, "安全组(平台级)"),
                new Tag(SEC_GRP_ACL, "安全组访问权限"),
                new Tag(SEC_GRP_ACL_SITE, "安全组访问权限(平台级)"),
                new Tag(SEC_GRP_DCL, "安全组数据权限"),
                new Tag(SEC_GRP_DCL_SITE, "安全组数据权限(平台级)"),
                new Tag(SEC_GRP_DCL_LINE, "安全组数据权限行"),
                new Tag(SEC_GRP_DCL_LINE_SITE, "安全组数据权限行(平台级)"),
                new Tag(SEC_GRP_DCL_DIM, "安全组数据权限维度"),
                new Tag(SEC_GRP_DCL_DIM_SITE, "安全组数据权限维度(平台级)"),
                new Tag(SEC_GRP_ACL_FIELD,"安全组字段权限"),
                new Tag(SEC_GRP_ACL_FIELD_SITE,"安全组字段权限(平台层)"),
                new Tag(SEC_GRP_ACL_DASHBOARD,"安全组工作台权限"),
                new Tag(SEC_GRP_ACL_DASHBOARD_SITE,"安全组工作台权限(平台层)"),

                new Tag(ACCESS_AUTH_MANAGER,"访问权限管理"),
                new Tag(ACCESS_AUTH_MANAGER_SITE,"访问权限管理(平台层)"),
                new Tag(DATA_AUTH_MANAGER,"数据权限管理"),
                new Tag(DATA_AUTH_MANAGER_SITE,"数据权限管理(平台层)"),
                new Tag(TOKEN_MANAGER,"Token管理"),
                new Tag(TOKEN_MANAGER_SITE,"Token管理(平台层)"),
                new Tag(TENANT_CUSTOM_POINT,"租户客户化管理(平台层)"),

                new Tag(TOOL_CACHE,"工具接口：刷新缓存"),
                new Tag(TOOL_DATA_FIX,"工具接口：修复数据"),
                new Tag(TOOL_PERMISSION,"工具接口：刷新权限"),

                new Tag(PERMISSION_CHECK,"权限检查管理"),
                new Tag(PERMISSION_CHECK_SITE,"权限检查管理"),
                new Tag(LABEL,"标签管理"),
                new Tag(ROLE_LABEL,"角色标签关系管理"),
                new Tag(ROLE_LABEL_SITE,"角色标签关系管理（平台层）"),
                new Tag(LABEL_SITE,"标签管理（平台层）"),

                new Tag(USER_DBD_MENU, "工作台用户常用功能"),
                new Tag(LABEL_RELATION, "标签关系"),
                new Tag(IAM_TENANT, "IAM 租户扩展查询"),
                new Tag(IAM_TENANT_SITE, "IAM 租户扩展查询（平台层）"),
                new Tag(LABEL_RELATION_SITE, "标签关系（平台层）"),
                new Tag(DOMAIN_ASSIGN, "域名分配"),
                new Tag(DOMAIN_ASSIGN_SITE, "域名分配（平台层）")

        );
    }
}
