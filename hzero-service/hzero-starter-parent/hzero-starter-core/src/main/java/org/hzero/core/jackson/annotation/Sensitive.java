package org.hzero.core.jackson.annotation;

import java.lang.annotation.*;

import org.springframework.core.annotation.AliasFor;

/**
 * <p>
 * 敏感信息处理，将信息按照设置的规则进行加密处理
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 11:42
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {
    /**
     * 从左边开始前 n 位替换
     */
    int left() default 0;

    /**
     * 从右边开始后 n 位替换
     */
    int right() default 0;

    /**
     * @see Sensitive#cipher()
     */
    @AliasFor("cipher")
    String[] value() default {};

    /**
     * 密文规则，加密内容的下标，从1开始
     * 单个下标：数字
     * 范围下标：数字1-数字2/数字1-
     * 多个规则合并：规则1，规则2
     * 实例：4-7，9，11-    表示第4，5，6，7，9，11以及11之后的位使用加密字符替换
     * 密文规则优先级高于明文规则，如果设置了密文规则，反转规则固定 false
     */
    @AliasFor("value")
    String[] cipher() default {};

    /**
     * 明文规则，明文内容的下标，从1开始
     * 单个下标：数字
     * 范围下标：数字1-数字2/数字1-
     * 多个规则合并：规则1，规则2
     * 实例：4-7，9，11-    表示第4，5，6，7，9，11以及11之后的位<strong>不使用</strong>加密字符替换
     * 密文规则优先级高于明文规则，如果设置了明文规则，反转规则固定 true
     */
    String[] clear() default {};

    /**
     * 默认的加密字符
     */
    char symbol() default Symbol.DEFAULT_SYMBOL;

    /**
     * 反转规则
     */
    boolean reverse() default false;

    class Symbol {
        
        private Symbol() throws IllegalAccessException {
            throw new IllegalAccessException();
        }
        
        public static final char DEFAULT_SYMBOL = '*';
    }

    /**
     * 常用密文规则
     */
    class Cipher {
        
        private Cipher() throws IllegalAccessException {
            throw new IllegalAccessException();
        }
        
        /**
         * 电话号码
         */
        public static final String PHONE = "4-7";
        /**
         * 身份证
         */
        public static final String ID_CARD = "5-14";
    }
}
