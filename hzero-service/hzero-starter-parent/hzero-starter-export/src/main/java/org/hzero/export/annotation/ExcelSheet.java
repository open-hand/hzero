package org.hzero.export.annotation;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;

/**
 * Excel Sheet 一个类对应一个Sheet页
 *
 * @author bojiangzhou 2018/07/25
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelSheet {

    /**
     * Sheet(Excel)标题 首先根据多语言取，如果多语言为空则取title，title为空则取类名
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
     * 多语言KEY 根据 key & code 获取多语言
     * @see #promptCode
     * @see #title
     */
    String promptKey() default "";

    /**
     * 多语言CODE 根据 key & code 获取多语言
     * @see #promptKey
     * @see #title
     */
    String promptCode() default "";

    /**
     * 行偏移量 从第几行开始显示数据 大于等于0
     */
    int rowOffset() default 0;

    /**
     * 占位符，偏移的列可使用占位符显示
     */
    String placeholder() default "*****";

    /**
     * 列偏移量 从第几列开始显示数据 大于等于0
     */
    int colOffset() default 0;

    /**
     * 分页大小 每次查询的数量
     */
    int pageSize() default 5000;

}
