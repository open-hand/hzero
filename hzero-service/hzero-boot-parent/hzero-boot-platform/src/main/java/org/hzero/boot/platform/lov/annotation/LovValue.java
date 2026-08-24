package org.hzero.boot.platform.lov.annotation;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.constraints.LovValueConstraint;
import org.springframework.core.annotation.AliasFor;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>指定需要自动处理值集值的字段及其处理规则</p>
 *
 * @author gaokuo.dai@hand-china.com 2018年7月3日下午3:38:38
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
@Constraint(validatedBy = LovValueConstraint.class)
public @interface LovValue {

    /**
     * @return 值集值所在的值集代码
     * @see #lovCode()
     */
    @AliasFor("lovCode")
    String value() default StringUtils.EMPTY;

    /**
     * @return 值集值所在的值集代码
     */
    @AliasFor("value")
    String lovCode() default StringUtils.EMPTY;

    /**
     * 处理后的含义字段放在哪个字段中<br/>
     * 不设置则进行默认映射,将注解所在字段名末尾的Code(如果有)替换为Meaning,例:<br/>
     * <ul>
     * <li>statusCode -> statusMeaning</li>
     * <li>processStatus -> processStatusMeaning</li>
     * <li>codeTypeCode -> codeTypeMeaning</li>
     * </ul>
     */
    String meaningField() default StringUtils.EMPTY;

    /**
     * @return 如果处理失败, 含义字段默认设置的值, 默认为原value
     */
    String defaultMeaning() default StringUtils.EMPTY;

    /**
     * @return 值集校验不通过时的错误信息
     */
    String message() default "{org.hzero.boot.platform.lov.annotation.LovValue.message}";

    /**
     * @return 值集校验分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return 是否需要校验
     */
    boolean mustIn() default true;

    /**
     * @return 仅翻译固定值集(添加这个属性是因为原本仅支持IDP，若不加该属性，需要先判断值集类型，多了一次查询)
     */
    boolean idp() default true;
}
