package org.hzero.boot.message.entity;

import javax.validation.constraints.NotBlank;

import com.google.common.base.Objects;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/17 11:25
 */
public class WeChatFont {

    /**
     * 模板数据
     */
    @NotBlank
    private String value;
    /**
     * 模板内容字体颜色，不填默认为黑色
     */
    private String color = "#000000";

    public String getValue() {
        return value;
    }

    public WeChatFont setValue(String value) {
        this.value = value;
        return this;
    }

    public String getColor() {
        return color;
    }

    public WeChatFont setColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String toString() {
        return "WeChatFont{" +
                "value='" + value + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WeChatFont)) {
            return false;
        }
        WeChatFont that = (WeChatFont) o;
        return Objects.equal(value, that.value) &&
                Objects.equal(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value, color);
    }
}
