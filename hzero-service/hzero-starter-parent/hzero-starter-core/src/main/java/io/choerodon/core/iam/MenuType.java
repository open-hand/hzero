package io.choerodon.core.iam;

/**
 * menu中所用类型
 * @author superlee
 * 2018/04/09
 */
public enum MenuType {

    /**
     * 根目录
     */
    ROOT("root"),

    /**
     * 菜单文件夹
     */
    DIR("dir"),

    /**
     * 菜单
     */
    MENU("menu");

    private final String value;

    MenuType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
