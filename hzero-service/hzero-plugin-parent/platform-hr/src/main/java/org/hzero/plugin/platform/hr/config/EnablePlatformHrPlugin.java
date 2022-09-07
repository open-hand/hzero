package org.hzero.plugin.platform.hr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * <p>
 *
 * </p>
 *
 * @author qingsheng.chen 2019/1/10 星期四 20:07
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.plugin.platform.hr")
public class EnablePlatformHrPlugin {
    public static final String UNIT = "Unit";
    public static final String POSITION = "Position";
    public static final String EMPLOYEE = "Employee";
    public static final String EMPLOYEE_ASSIGN = "Employee Assign";
    public static final String EMPLOYEE_USER = "Employee User";
    public static final String UNIT_AGGREGATE = "Unit Aggregate";
    public static final String HR_SYNC = "Hr Sync";
    /**
     * 工作流HR组织架构接口
     */
    public static final String WORKFLOW_HR = "Workflow - HR";
    public static final String WORKFLOW_PLUS_HR = "Workflow Plus - HR";

    @Autowired
    public EnablePlatformHrPlugin(Docket docket) {
        docket.tags(
                new Tag(HR_SYNC, "Hr基础数据同步外部系统"),
                new Tag(UNIT, "部门管理"),
                new Tag(POSITION, "岗位管理"),
                new Tag(EMPLOYEE, "员工管理"),
                new Tag(EMPLOYEE_ASSIGN, "员工岗位分配"),
                new Tag(EMPLOYEE_USER, "员工用户关系"),
                new Tag(UNIT_AGGREGATE, "组织架构聚合"),
                new Tag(WORKFLOW_HR, "HR组织架构"),
                new Tag(WORKFLOW_PLUS_HR, "新工作流-HR组织架构")
        );
    }
}
