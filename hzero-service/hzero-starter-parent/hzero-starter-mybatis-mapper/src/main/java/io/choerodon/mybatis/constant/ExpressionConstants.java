package io.choerodon.mybatis.constant;

import org.hzero.mybatis.common.query.Where;

/**
 * {@link Where}注解的 Expression 表达式所需常量
 *
 * @author shira 2019/03/20 18:41
 */
public class ExpressionConstants {

    /**
     * 本表别名占位符的正则表达式，默认本表别名为a
     */
    public static final String TABLE_ALIAS_REG = "(\\ba\\.)|(\\bA\\.)|(\\bA0\\.)|(\\ba0\\.)";

    /**
     * 变量的正则表达式 ： 匹配 #{companyId} 不匹配#{record.companyId}
     */
    public static final String VARIABLE_REG = "(#\\{)(?!record\\.)(?=[^\\}]*\\})";

    /**
     * 本表别名占位符，默认本表别名为a
     */
    public static final String PLACEHOLDER_DTO = "#{record.";
}
