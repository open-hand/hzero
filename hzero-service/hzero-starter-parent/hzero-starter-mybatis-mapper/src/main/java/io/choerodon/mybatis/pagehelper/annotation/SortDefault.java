/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.choerodon.mybatis.pagehelper.annotation;

import java.lang.annotation.*;

import io.choerodon.mybatis.pagehelper.domain.Sort;



/**
 * Annotation to define the default {@link Sort} options to be used when injecting a {@link Sort} instance into a
 * controller handler method.
 *
 * @author Oliver Gierke
 * @since 1.6
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SortDefault {

    /**
     * Alias for {@link #sort()} to make a declaration configuring fields only more concise.
     *
     * @return String[] String[]
     */
    String[] value() default {};

    /**
     * The properties to sort by by default. If unset, no sorting will be applied at all.
     *
     * @return String[] String[]
     */
    String[] sort() default {};

    /**
     * The direction to sort by. Defaults to {@link Sort.Direction#ASC}.
     *
     * @return Sort.Direction Sort.Direction
     */
    Sort.Direction direction() default Sort.Direction.ASC;

    /**
     * Wrapper annotation to allow declaring multiple {@link SortDefault} annotations on a method parameter.
     *
     * @author Oliver Gierke
     * @since 1.6
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface SortDefaults {

        /**
         * The individual {@link SortDefault} declarations to be sorted by.
         *
         * @return SortDefault[] SortDefault[]
         */
        SortDefault[] value();
    }
}
