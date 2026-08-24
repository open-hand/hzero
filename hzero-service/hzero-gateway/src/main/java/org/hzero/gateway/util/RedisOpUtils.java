package org.hzero.gateway.util;

import org.hzero.core.redis.RedisHelper;
import org.hzero.gateway.filter.Query;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/6 1:47 下午
 */
public class RedisOpUtils {

    public static <T> T selectDbAndClear(RedisHelper redisHelper, int db, Query<T> query){

        try {
            redisHelper.setCurrentDatabase(db);
            return query.get();
        }finally {
            redisHelper.clearCurrentDatabase();
        }

    }
}
