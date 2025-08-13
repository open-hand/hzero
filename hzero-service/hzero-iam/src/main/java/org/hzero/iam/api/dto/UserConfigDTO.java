package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * userConfig DTO
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/02 15:54
 */
public class UserConfigDTO {

    // 菜单布局默认配置
    public static final String DEFAULT = "default";
    // 角色合并、弹窗提醒 默认配置
    public static final Integer NEGATIVE = -1;

    @ApiModelProperty(value = "菜单布局，包含左右布局和水平布局两种")
    private String menuLayout;
    @ApiModelProperty(value = "菜单布局主题")
    private String menuLayoutTheme;
    @ApiModelProperty(value = "角色合并标识")
    private Integer roleMergeFlag;
    @ApiModelProperty(value = "弹框提醒标识")
    private Integer popoutReminderFlag;

    public UserConfigDTO() {
    }

    public Integer getRoleMergeFlag() {
        return roleMergeFlag;
    }

    public void setRoleMergeFlag(Integer roleMergeFlag) {
        this.roleMergeFlag = roleMergeFlag;
    }

    public String getMenuLayout() {
        return menuLayout;
    }

    public void setMenuLayout(String menuLayout) {
        this.menuLayout = menuLayout;
    }

    public String getMenuLayoutTheme() {
        return menuLayoutTheme;
    }

    public void setMenuLayoutTheme(String menuLayoutTheme) {
        this.menuLayoutTheme = menuLayoutTheme;
    }

    public Integer getPopoutReminderFlag() {
        return popoutReminderFlag;
    }

    public void setPopoutReminderFlag(Integer popoutReminderFlag) {
        this.popoutReminderFlag = popoutReminderFlag;
    }
}
