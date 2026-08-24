package org.hzero.iam.domain.vo;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/20 14:09
 */
public class ProfileVO {

    private String levelValue;
    private String value;
    private String levelPath;
    private String inheritLevelPath;

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public String getInheritLevelPath() {
        return inheritLevelPath;
    }

    public void setInheritLevelPath(String inheritLevelPath) {
        this.inheritLevelPath = inheritLevelPath;
    }

    public String getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(String levelValue) {
        this.levelValue = levelValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
