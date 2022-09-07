/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package org.hzero.starter.sqlparser.util;

import org.hzero.starter.sqlparser.support.logging.Log;
import org.hzero.starter.sqlparser.support.logging.LogFactory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class JdbcSqlStatUtils {

    private final static Log LOG = LogFactory.getLog(JdbcSqlStatUtils.class);

    public static long[] rtrim(long[] array) {
        int notZeroLen = array.length;
        for (int i = array.length - 1; i >= 0; --i, notZeroLen--) {
            if (array[i] != 0) {
                break;
            }
        }

        if (notZeroLen != array.length) {
            array = Arrays.copyOf(array, notZeroLen);
        }

        return array;
    }

    public static <T> int get(T stat, AtomicIntegerFieldUpdater<T> updater, boolean reset) {
        if (reset) {
            return updater.getAndSet(stat, 0);
        } else {
            return updater.get(stat);
        }
    }

    public static <T> long get(T stat, AtomicLongFieldUpdater<T> updater, boolean reset) {
        if (reset) {
            return updater.getAndSet(stat, 0);
        } else {
            return updater.get(stat);
        }
    }
    
    public static long get(AtomicLong counter, boolean reset) {
        if (reset) {
            return counter.getAndSet(0);
        } else {
            return counter.get();
        }
    }
    
    public static int get(AtomicInteger counter, boolean reset) {
        if (reset) {
            return counter.getAndSet(0);
        } else {
            return counter.get();
        }
    }
}
