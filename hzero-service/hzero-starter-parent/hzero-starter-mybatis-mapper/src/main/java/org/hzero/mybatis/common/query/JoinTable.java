/**
 * Copyright 2016 www.extdo.com 
 */
package org.hzero.mybatis.common.query;


import java.lang.annotation.*;
import javax.persistence.criteria.JoinType;

/**
 * @author njq.niu@hand-china.com
 */
@Repeatable(JoinTables.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTable {
    
    String name();
    
    Class<?> target();

    
    JoinType type() default JoinType.INNER;
    
    JoinOn[] on() default {};

    boolean joinMultiLanguageTable() default false;
}
