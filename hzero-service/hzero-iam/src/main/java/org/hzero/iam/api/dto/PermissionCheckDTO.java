package org.hzero.iam.api.dto;

import org.hzero.iam.domain.entity.Menu;

/**
 * 权限检查DTO
 *
 * @author bojiangzhou 2019/03/13
 */
public class PermissionCheckDTO {

    private String code;
    private boolean approve = false;
    private String controllerType;

    public PermissionCheckDTO() {
    }

    public PermissionCheckDTO(String code, boolean approve) {
        this.code = code;
        this.approve = approve;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isApprove() {
        return approve;
    }

    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    public String getControllerType() {
        return controllerType;
    }

    public void setControllerType(String controllerType) {
        this.controllerType = controllerType;
    }
}
