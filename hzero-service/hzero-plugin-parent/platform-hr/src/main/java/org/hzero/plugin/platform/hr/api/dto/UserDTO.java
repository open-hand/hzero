package org.hzero.plugin.platform.hr.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 查询用户DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/04/22 10:42
 */
@ApiModel("用户DTO")
public class UserDTO {
    @ApiModelProperty("用户Id")
    private Long id;
    @ApiModelProperty("登录账号")
    private String loginName;
    @ApiModelProperty("租户Id")
    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "UserDTO{" + "id=" + id + ", loginName='" + loginName + '\'' + ", organizationId=" + organizationId
                        + '}';
    }
}
