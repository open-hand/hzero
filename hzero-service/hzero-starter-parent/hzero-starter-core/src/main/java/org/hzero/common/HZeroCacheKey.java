package org.hzero.common;

/**
 * 缓存KEY
 *
 * @author bojiangzhou 2018/08/10
 */
public final class HZeroCacheKey {

    /**
     * 用户
     */
    public static final String USER = HZeroService.Iam.CODE + ":user";

    /**
     * 值集
     */
    public interface Lov {

        /**
         * 值集缓存路径
         */
        String LOV_KEY = HZeroService.Platform.CODE + ":lov";

        /**
         * 值集头缓存目录
         */
        String HEADER_KEY_PREFIX = LOV_KEY + ":lov:";

        /**
         * 值集头防击穿缓存目录
         */
        String HEADER_FAIL_FAST_KEY_PREFIX = LOV_KEY + ":lov_fail_fast:";

        /**
         * 值集值缓存目录
         */
        String VALUE_KEY_PREFIX = LOV_KEY + ":values:";

        /**
         * 值集值防击穿缓存目录
         */
        String VALUE_FAIL_FAST_KEY_PREFIX = LOV_KEY + ":values_fail_fast:";

        /**
         * 值集Sql缓存目录
         */
        String SQL_KEY_PREFIX = LOV_KEY + ":sql:";

        /**
         * 值集翻译Sql缓存目录
         */
        String TRANSLATION_SQL_KEY_PREFIX = LOV_KEY + ":translation-sql:";

        /**
         * 值集视图缓存目录
         */
        String VIEW_KEY_PREFIX = LOV_KEY + ":view:";

        /**
         * 值集值防击穿缓存目录
         */
        String VIEW_FAIL_FAST_KEY_PREFIX = LOV_KEY + ":view_fail_fast:";

    }

}
