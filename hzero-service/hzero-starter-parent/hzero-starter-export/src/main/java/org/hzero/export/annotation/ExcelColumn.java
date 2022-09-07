package org.hzero.export.annotation;

import java.lang.annotation.*;

import org.hzero.core.base.BaseConstants;
import org.hzero.export.render.ValueRenderer;
import org.springframework.core.annotation.AliasFor;

/**
 * Excel Column 标注导出字段 <p></p>
 *
 * @author bojiangzhou 2018/07/25
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {

    /**
     * 列标题 首先根据多语言取，如果多语言为空则取title，title为空则取类名
     */
    @AliasFor("zh")
    String title() default "";

    /**
     * 中文标题
     */
    @AliasFor("title")
    String zh() default "";

    /**
     * 英文标题
     */
    String en() default "";

    /**
     * 标题字体
     */
    Class<? extends Font> titleFont() default EmptyFont.class;

    /**
     * 标题底色
     */
    Class<? extends Color> titleForegroundColor() default EmptyColor.class;

    /**
     * 标题背景色
     */
    Class<? extends Color> titleBackgroundColor() default EmptyColor.class;


    /**
     * 多语言KEY 根据 key & code 获取多语言
     *
     * @see #promptCode
     * @see #title
     */
    String promptKey() default "";

    /**
     * 多语言CODE 根据 key & code 获取多语言
     *
     * @see #promptKey
     * @see #title
     */
    String promptCode() default "";

    /**
     * 在子列表中显示该列
     */
    boolean showInChildren() default false;

    /**
     * 列顺序
     */
    int order() default 1;

    /**
     * Cell 格式，参考 {@link BaseConstants.Pattern}
     */
    String pattern() default "";

    /**
     * 扩展属性
     */
    Class<? extends ExpandProperty> expandProperty() default EmptyExpandProperty.class;

    /**
     * 字体
     */
    Class<? extends Font> font() default EmptyFont.class;

    /**
     * 底色
     */
    Class<? extends Color> foregroundColor() default EmptyColor.class;

    /**
     * 背景色
     */
    Class<? extends Color> backgroundColor() default EmptyColor.class;

    /**
     * 批注
     */
    Class<? extends Comment> comment() default EmptyComment.class;


    /**
     * 是否子节点
     */
    boolean child() default false;

    /**
     * 列宽度
     */
    int width() default 0;

    /**
     * 分组标识
     */
    Class<?>[] groups() default {};

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
    Class<? extends ValueRenderer>[] renderers() default {};

    /**
     * The column to be exported is selected by default
     */
    boolean defaultSelected() default false;

    /**
     * 值集编码
     */
    String lovCode() default "";

    /**
     * 是否进行安全检查
     *
     * @return boolean
     */
    boolean safeCheck() default true;
}
