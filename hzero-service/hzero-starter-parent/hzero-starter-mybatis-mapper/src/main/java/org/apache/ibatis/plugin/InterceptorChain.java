/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.plugin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class InterceptorChain {

    private final List<Interceptor> interceptors = new LinkedList<>();
    private final Comparator<Interceptor> comparator = (i1, i2) -> {
        if (i1 == null && i2 == null) {
            return 0;
        }
        if (i1 == null) {
            return 1;
        }
        if (i2 == null) {
            return -1;
        }
        int order1 = i1.getClass().isAnnotationPresent(InterceptorOrder.class) ?
                i1.getClass().getAnnotation(InterceptorOrder.class).value() : Integer.MAX_VALUE;
        int order2 = i2.getClass().isAnnotationPresent(InterceptorOrder.class) ?
                i2.getClass().getAnnotation(InterceptorOrder.class).value() : Integer.MAX_VALUE;
        return order2 - order1;
    };

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(findIndex(interceptor), interceptor);
    }

    private int findIndex(Interceptor interceptor) {
        if (interceptors.isEmpty()) {
            return 0;
        }
        if (interceptors.size() == 1) {
            return comparator.compare(interceptors.get(0), interceptor) > 0 ? 0 : 1;
        }
        return dichotomyFindIndex(interceptor, 0, interceptors.size() - 1);
    }

    private int dichotomyFindIndex(Interceptor interceptor, int start, int end) {
        if (comparator.compare(interceptors.get(start), interceptor) > 0) {
            return start;
        }
        if (comparator.compare(interceptors.get(end), interceptor) < 0) {
            return end + 1;
        }
        if (end - start <= 1) {
            return end;
        }
        int middle = (start + end) / 2;
        if (comparator.compare(interceptors.get(middle), interceptor) < 0) {
            return dichotomyFindIndex(interceptor, middle, end);
        } else {
            return dichotomyFindIndex(interceptor, start, middle);
        }
    }

    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

}
