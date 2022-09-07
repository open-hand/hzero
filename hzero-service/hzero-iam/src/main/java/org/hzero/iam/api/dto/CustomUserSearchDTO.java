package org.hzero.iam.api.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author mingwei.liu@hand-china.com 2018/9/17
 */
@ApiModel(value = "客户化用户查询参数")
public class CustomUserSearchDTO {
    @ApiModelProperty(value = "唯一标识")
    private String id;
    @ApiModelProperty(value = "登录用户名")
    private String loginName;
    @ApiModelProperty(value = "昵称")
    private String realName;
    @ApiModelProperty(value = "电话")
    private String phone;
    @ApiModelProperty(value = "Email")
    private String email;
    @ApiModelProperty(value = "租住ID")
    private Long organizationId;
    @ApiModelProperty(value = "排除的用户")
    private List<Long> excludeUserIds;
    @ApiModelProperty(value = "是否排除已分配员工的记录")
    private Boolean excludeHasAssignedEmployeeFlag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Long> getExcludeUserIds() {
        return excludeUserIds;
    }

    public void setExcludeUserIds(List<Long> excludeUserIds) {
        this.excludeUserIds = excludeUserIds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Boolean getExcludeHasAssignedEmployeeFlag() {
        return excludeHasAssignedEmployeeFlag;
    }

    public void setExcludeHasAssignedEmployeeFlag(Boolean excludeHasAssignedEmployeeFlag) {
        this.excludeHasAssignedEmployeeFlag = excludeHasAssignedEmployeeFlag;
    }
}
