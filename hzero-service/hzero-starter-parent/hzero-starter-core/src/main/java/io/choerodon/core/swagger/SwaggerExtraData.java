package io.choerodon.core.swagger;

/**
 * @author flyleft
 * 2018/4/13
 */
public class SwaggerExtraData {

    private PermissionData permission;

    private LabelData label;

    public PermissionData getPermission() {
        return permission;
    }

    public void setPermission(PermissionData permission) {
        this.permission = permission;
    }

    public LabelData getLabel() {
        return label;
    }

    public void setLabel(LabelData label) {
        this.label = label;
    }
}
