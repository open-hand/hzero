package io.choerodon.core.swagger;

/**
 * @author flyleft
 * 2018/4/13
 */
public class LabelData {

    private String labelName;

    private String type;

    private String level;

    private String roleName;

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "LabelData{"
                + "labelName='" + labelName + '\''
                + ", type='" + type + '\''
                + ", level='" + level + '\''
                + ", roleName='" + roleName + '\''
                + '}';
    }
}
