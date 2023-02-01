package org.hzero.export.vo;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.hzero.export.annotation.*;
import org.hzero.export.render.ValueRenderer;

/**
 * ExcelColumn 注解属性
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/30 16:20
 */
public class ExcelColumnProperty {

    public ExcelColumnProperty() {
    }

    public ExcelColumnProperty(ExcelColumn excelColumn) {
        this.titleFont = excelColumn.titleFont();
        this.titleForegroundColor = excelColumn.titleForegroundColor();
        this.titleBackgroundColor = excelColumn.titleBackgroundColor();
        this.showInChildren = excelColumn.showInChildren();
        this.pattern = excelColumn.pattern();
        this.expandProperty = excelColumn.expandProperty();
        this.font = excelColumn.font();
        this.foregroundColor = excelColumn.foregroundColor();
        this.backgroundColor = excelColumn.backgroundColor();
        this.comment = excelColumn.comment();
        this.child = excelColumn.child();
        this.width = excelColumn.width();
        this.renderers = Arrays.asList(excelColumn.renderers());
        this.lovCode = excelColumn.lovCode();
        this.safeCheck = excelColumn.safeCheck();
    }

    /**
     * 标题字体
     */
    private Class<? extends Font> titleFont = EmptyFont.class;
    /**
     * 标题底色
     */
    private Class<? extends Color> titleForegroundColor = EmptyColor.class;
    /**
     * 标题背景色
     */
    private Class<? extends Color> titleBackgroundColor = EmptyColor.class;
    /**
     * 在子列表中显示该列
     */
    private boolean showInChildren = false;
    /**
     * Cell 格式，参考
     *
     * @link BaseConstants.Pattern
     */
    private String pattern = "";
    /**
     * 扩展属性
     */
    private Class<? extends ExpandProperty> expandProperty = EmptyExpandProperty.class;
    /**
     * 字体
     */
    private Class<? extends Font> font = EmptyFont.class;
    /**
     * 底色
     */
    private Class<? extends Color> foregroundColor = EmptyColor.class;
    /**
     * 背景色
     */
    private Class<? extends Color> backgroundColor = EmptyColor.class;
    /**
     * 批注
     */
    private Class<? extends Comment> comment = EmptyComment.class;
    /**
     * 是否子节点
     */
    private boolean child = false;
    /**
     * 列宽度
     */
    private int width = 0;
    /**
     * 数据渲染器，根据需求自行实现渲染器，设置Cell数据时会通过该渲染器来渲染数据和类型。
     *
     * <pre> example:
     *  public static class ExampleRenderer implements ValueRenderer {
     *
     *      public Object render(Object value, Object data) {
     *          ExampleDTO dto = (ExampleDTO) data;
     *          return "template name = " + dto.name;
     *      }
     *  }
     * </pre>
     */
    private List<Class<? extends ValueRenderer>> renderers;
    /**
     * 值集编码  指定编码会为导出的excel渲染下拉选项
     */
    private String lovCode = "";
    /**
     * 是否进行安全检查
     */
    private boolean safeCheck = true;

    public Class<? extends Font> getTitleFont() {
        return titleFont;
    }

    public ExcelColumnProperty setTitleFont(Class<? extends Font> titleFont) {
        this.titleFont = titleFont;
        return this;
    }

    public Class<? extends Color> getTitleForegroundColor() {
        return titleForegroundColor;
    }

    public ExcelColumnProperty setTitleForegroundColor(Class<? extends Color> titleForegroundColor) {
        this.titleForegroundColor = titleForegroundColor;
        return this;
    }

    public Class<? extends Color> getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public ExcelColumnProperty setTitleBackgroundColor(Class<? extends Color> titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
        return this;
    }

    public boolean isShowInChildren() {
        return showInChildren;
    }

    public ExcelColumnProperty setShowInChildren(boolean showInChildren) {
        this.showInChildren = showInChildren;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public ExcelColumnProperty setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public Class<? extends ExpandProperty> getExpandProperty() {
        return expandProperty;
    }

    public ExcelColumnProperty setExpandProperty(Class<? extends ExpandProperty> expandProperty) {
        this.expandProperty = expandProperty;
        return this;
    }

    public Class<? extends Font> getFont() {
        return font;
    }

    public ExcelColumnProperty setFont(Class<? extends Font> font) {
        this.font = font;
        return this;
    }

    public Class<? extends Color> getForegroundColor() {
        return foregroundColor;
    }

    public ExcelColumnProperty setForegroundColor(Class<? extends Color> foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }

    public Class<? extends Color> getBackgroundColor() {
        return backgroundColor;
    }

    public ExcelColumnProperty setBackgroundColor(Class<? extends Color> backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Class<? extends Comment> getComment() {
        return comment;
    }

    public ExcelColumnProperty setComment(Class<? extends Comment> comment) {
        this.comment = comment;
        return this;
    }

    public boolean isChild() {
        return child;
    }

    public ExcelColumnProperty setChild(boolean child) {
        this.child = child;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ExcelColumnProperty setWidth(int width) {
        this.width = width;
        return this;
    }

    public List<Class<? extends ValueRenderer>> getRenderers() {
        return renderers;
    }

    public ExcelColumnProperty setRenderers(List<Class<? extends ValueRenderer>> renderers) {
        this.renderers = renderers;
        return this;
    }

    public String getLovCode() {
        return lovCode;
    }

    public ExcelColumnProperty setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public boolean isSafeCheck() {
        return safeCheck;
    }

    public ExcelColumnProperty setSafeCheck(boolean safeCheck) {
        this.safeCheck = safeCheck;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExcelColumnProperty that = (ExcelColumnProperty) o;
        return showInChildren == that.showInChildren
                && child == that.child
                && width == that.width
                && safeCheck == that.safeCheck
                && Objects.equals(titleFont, that.titleFont)
                && Objects.equals(titleForegroundColor, that.titleForegroundColor)
                && Objects.equals(titleBackgroundColor, that.titleBackgroundColor)
                && Objects.equals(pattern, that.pattern)
                && Objects.equals(expandProperty, that.expandProperty)
                && Objects.equals(font, that.font)
                && Objects.equals(foregroundColor, that.foregroundColor)
                && Objects.equals(backgroundColor, that.backgroundColor)
                && Objects.equals(comment, that.comment)
                && Objects.equals(renderers, that.renderers)
                && Objects.equals(lovCode, that.lovCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titleFont, titleForegroundColor, titleBackgroundColor, showInChildren, pattern, expandProperty, font, foregroundColor, backgroundColor, comment, child, width, renderers, lovCode, safeCheck);
    }

    @Override
    public String toString() {
        return "ExcelColumnProperty{" +
                ", titleFont=" + titleFont +
                ", titleForegroundColor=" + titleForegroundColor +
                ", titleBackgroundColor=" + titleBackgroundColor +
                ", showInChildren=" + showInChildren +
                ", pattern='" + pattern + '\'' +
                ", expandProperty=" + expandProperty +
                ", font=" + font +
                ", foregroundColor=" + foregroundColor +
                ", backgroundColor=" + backgroundColor +
                ", comment=" + comment +
                ", child=" + child +
                ", width=" + width +
                ", renderers=" + renderers +
                ", lovCode='" + lovCode + '\'' +
                ", safeCheck=" + safeCheck +
                '}';
    }
}
