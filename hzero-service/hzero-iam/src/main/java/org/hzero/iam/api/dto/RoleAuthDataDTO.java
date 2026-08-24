package org.hzero.iam.api.dto;

import org.hzero.iam.domain.entity.RoleAuthData;
import org.hzero.iam.domain.entity.RoleAuthDataLine;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-14 15:35
 */
public class RoleAuthDataDTO {
    @Valid
    @NotNull
    private RoleAuthData roleAuthData;
    @Valid
    private List<RoleAuthDataLine> roleAuthDataLineList;

    public RoleAuthData getRoleAuthData() {
        return roleAuthData;
    }

    public RoleAuthDataDTO setRoleAuthData(RoleAuthData roleAuthData) {
        this.roleAuthData = roleAuthData;
        return this;
    }

    public List<RoleAuthDataLine> getRoleAuthDataLineList() {
        return roleAuthDataLineList;
    }

    public RoleAuthDataDTO setRoleAuthDataLineList(List<RoleAuthDataLine> roleAuthDataLineList) {
        this.roleAuthDataLineList = roleAuthDataLineList;
        return this;
    }
}
