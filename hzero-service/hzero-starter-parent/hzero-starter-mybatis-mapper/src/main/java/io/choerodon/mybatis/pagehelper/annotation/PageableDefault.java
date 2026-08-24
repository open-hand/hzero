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
 * source :　org.springframework.data.web
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableDefault {

    /**
     * Alias for {@link #size()}. Prefer to use the {@link #size()} method as it makes the annotation declaration more
     * expressive and you'll probably want to configure the {@link #page()} anyway.
     *
     * @return value
     */
    int value() default 10;

    /**
     * The default-size the injected should get if no corresponding
     * parameter defined in request (default is 10).
     * @return int int
     */
    int size() default 10;

    /**
     * The default-pagenumber the injected should get if no corresponding
     * parameter defined in request (default is 0).
     * @return  int int
     */
    int page() default 0;

    /**
     * The properties to sort by by default. If unset, no sorting will be applied at all.
     *
     * @return sort
     */
    String[] sort() default {};

    /**
     * The direction to sort by. Defaults to {@link Sort.Direction#ASC}.
     *
     * @return direction
     */
    Sort.Direction direction() default Sort.Direction.ASC;
}
