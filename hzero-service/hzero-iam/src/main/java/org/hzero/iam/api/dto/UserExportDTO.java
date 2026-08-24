package org.hzero.iam.api.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;
import org.hzero.iam.domain.vo.UserVO;

/**
 * 用户信息导出DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年8月12日下午11:33:05
 */
@ApiModel("用户信息导出DTO")
@ExcelSheet(zh = "用户", en = "User")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserExportDTO {

    public static final String FIELD_ROLE_LIST = "roleList";
    public static final String FIELD_USER_AUTHORITY_LIST = "userAuthorityList";

    public UserExportDTO() {}

    public UserExportDTO(UserVO user) {
        if(user == null) {
            return;
        }
        this.loginName = user.getLoginName();
        this.realName = user.getRealName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.startDateActive = user.getStartDateActive();
        this.endDateActive = user.getEndDateActive();
        this.enabled = 1 - (Objects.equals(user.getEnabled(), Boolean.TRUE) ? BaseConstants.Flag.YES : BaseConstants.Flag.NO);
    }

    @ApiModelProperty("账号")
    @ExcelColumn(zh = "账号", en = "Login Name", showInChildren = true)
    private String loginName;
    @ApiModelProperty("名称")
    @ExcelColumn(zh = "名称", en = "Real Name")
    private String realName;
    @ApiModelProperty("邮箱")
    @ExcelColumn(zh = "邮箱", en = "Email")
    private String email;
    @ApiModelProperty("手机")
    @ExcelColumn(zh = "手机", en = "Phone")
    private String phone;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @ApiModelProperty("有效期从")
    @ExcelColumn(zh = "有效期从", en = "Start Date Active", pattern = BaseConstants.Pattern.DATE)
    private LocalDate startDateActive;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @ApiModelProperty("有效期至")
    @ExcelColumn(zh = "有效期至", en = "End Date Active", pattern = BaseConstants.Pattern.DATE)
    private LocalDate endDateActive;
    @ApiModelProperty("冻结")
    @LovValue(lovCode = "HPFM.FLAG")
    private Integer enabled;
    @ApiModelProperty("冻结含义")
    @ExcelColumn(zh = "冻结", en = "Enabled")
    private String enabledMeaning;
    @ApiModelProperty("用户类型")
    @LovValue(lovCode = "HIAM.USER_TYPE")
    private String userType;
    @ApiModelProperty("用户类型含义")
    @ExcelColumn(zh = "用户类型", en = "UserType")
    private String userTypeMeaning;
    @ApiModelProperty("角色列表")
    @ExcelColumn(zh = "角色", en = "Role", child = true)
    private List<RoleExportDTO> roleList;
    @ApiModelProperty("用户权限列表")
    @ExcelColumn(zh = "用户权限", en = "User Authority", child = true)
    private List<UserAuthorityExportDTO> userAuthorityList;

    /**
     * @return 账号
     */
    public String getLoginName() {
        return loginName;
    }
    /**
     * @return 名称
     */
    public String getRealName() {
        return realName;
    }
    /**
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }
    /**
     * @return 手机
     */
    public String getPhone() {
        return phone;
    }
    /**
     * @return 有效期从
     */
    public LocalDate getStartDateActive() {
        return startDateActive;
    }
    /**
     * @return 有效期至
     */
    public LocalDate getEndDateActive() {
        return endDateActive;
    }
    /**
     * @return 冻结
     */
    public Integer getEnabled() {
        return enabled;
    }
    /**
     * @return 角色列表
     */
    public List<RoleExportDTO> getRoleList() {
        return roleList;
    }
    /**
     * @return 冻结含义
     */
    public String getEnabledMeaning() {
        return enabledMeaning;
    }
    /**
     * @return 用户权限列表
     */
    public List<UserAuthorityExportDTO> getUserAuthorityList() {
        return userAuthorityList;
    }
    public void setUserAuthorityList(List<UserAuthorityExportDTO> userAuthorityList) {
        this.userAuthorityList = userAuthorityList;
    }
    public void setEnabledMeaning(String enabledMeaning) {
        this.enabledMeaning = enabledMeaning;
    }
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setStartDateActive(LocalDate startDateActive) {
        this.startDateActive = startDateActive;
    }
    public void setEndDateActive(LocalDate endDateActive) {
        this.endDateActive = endDateActive;
    }
    public void setEnabled(Integer enabled) {
        // 取反，页面意思为 冻结
        this.enabled = 1 - enabled;
    }
    public void setRoleList(List<RoleExportDTO> roleList) {
        this.roleList = roleList;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserTypeMeaning() {
        return userTypeMeaning;
    }

    public void setUserTypeMeaning(String userTypeMeaning) {
        this.userTypeMeaning = userTypeMeaning;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserExportDTO [loginName=");
        builder.append(loginName);
        builder.append(", realName=");
        builder.append(realName);
        builder.append(", email=");
        builder.append(email);
        builder.append(", phone=");
        builder.append(phone);
        builder.append(", startDateActive=");
        builder.append(startDateActive);
        builder.append(", endDateActive=");
        builder.append(endDateActive);
        builder.append(", enabled=");
        builder.append(enabled);
        builder.append(", enabledMeaning=");
        builder.append(enabledMeaning);
        builder.append(", userType=");
        builder.append(userType);
        builder.append(", userTypeMeaning=");
        builder.append(userTypeMeaning);
        builder.append(", roleList=");
        builder.append(roleList);
        builder.append(", userAuthorityList=");
        builder.append(userAuthorityList);
        builder.append("]");
        return builder.toString();
    }

}
