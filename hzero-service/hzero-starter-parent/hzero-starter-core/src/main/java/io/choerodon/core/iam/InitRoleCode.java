package io.choerodon.core.iam;


/**
 * 初始化角色的code
 *
 * @author superlee
 */
public class InitRoleCode {

    /**
     * 全局层管理员
     */
    public static final String SITE_ADMINISTRATOR = "role/site/default/administrator";

    /**
     * 组织层管理员
     */
    public static final String ORGANIZATION_ADMINISTRATOR = "role/organization/default/administrator";

    /**
     * 项目层管理员
     */
    public static final String PROJECT_ADMINISTRATOR = "role/project/default/administrator";

    /**
     * 部署管理员
     */
    public static final String DEPLOY_ADMINISTRATOR = "role/project/default/deploy-administrator";

    /**
     * 项目成员
     */
    public static final String PROJECT_MEMBER = "role/project/default/project-member";

    /**
     * 项目所有者
     */
    public static final String PROJECT_OWNER = "role/project/default/project-owner";

    /**
     * 组织成员
     */
    public static final String ORGANIZATION_MEMBER = "role/organization/default/organization-member";

    /**
     * 平台开发者
     */
    public static final String SITE_DEVELOPER = "role/site/default/developer";

    public static String[] values() {
        return new String[]{
                SITE_ADMINISTRATOR,
                ORGANIZATION_ADMINISTRATOR,
                PROJECT_ADMINISTRATOR,
                DEPLOY_ADMINISTRATOR,
                PROJECT_MEMBER,
                PROJECT_OWNER,
                ORGANIZATION_MEMBER,
                SITE_DEVELOPER
        };
    }
}
